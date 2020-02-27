package dfjinxin.demo.service.impl;

import dfjinxin.demo.service.MetaDataService;
import dfjinxin.demo.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
@Slf4j
public class MetaDataServiceImpl implements MetaDataService {

    @Autowired
    private Environment env;

    private String getUrl() {
        return "jdbc:mysql://" + env.getProperty("metadata.db.ip") +":" + env.getProperty("metadata.db.port") +"/" + env.getProperty("metadata.db.name") + "?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai";
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        Properties properties = new Properties();

        properties.setProperty("user", env.getProperty("metadata.db.user"));
        properties.setProperty("password", env.getProperty("metadata.db.password"));
        properties.put("remarks", "true");
        properties.put("useInformationSchema", "true");

        return DriverManager.getConnection(getUrl(), properties);
    }

    @Override
    public List tableList() {
        List<Map<String, String>> list = new ArrayList();

        try (Connection conn = this.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(env.getProperty("metadata.db.name"), null, "%%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap() {{
                        put("tableName", rs.getString("TABLE_NAME"));
                        put("tableRemarks", rs.getString("REMARKS"));
                    }};
                    list.add(map);
                }
            }
        } catch (Exception e) {
            log.error("{}", e);
        }

        return list;
    }

    @Override
    public List fieldList(String tableName) {
        List<Map<String, String>> list = new ArrayList();

        try (Connection conn = this.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();

            try (ResultSet rs = meta.getColumns(env.getProperty("metadata.db.name"), null, tableName, null)) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap() {{
                        put("columnName", rs.getString("COLUMN_NAME"));
                        put("columnRemarks", rs.getString("REMARKS"));
                        put("type", rs.getString("TYPE_NAME"));

                        put("nullable", rs.getInt("NULLABLE") == 0 ? "false" : "true");
                        put("digits", rs.getInt("DECIMAL_DIGITS"));
                        put("size", rs.getInt("COLUMN_SIZE"));
                    }};
                    list.add(map);
                }
            }

            try (ResultSet keyrs = meta.getPrimaryKeys(env.getProperty("metadata.db.name"), null, tableName)) {
                while (keyrs.next()) {
                    String column = keyrs.getString("COLUMN_NAME");

                    list.forEach(item -> {
                        boolean ret = item.get("columnName").equals(column);

                        item.put("IS_PRIMARYKEY", Boolean.toString(ret));
                    });
                }
            }
        } catch (Exception e) {
            log.error("{}", e);
        }

        return list;
    }

    @Override
    public PageUtils dataList(String tableName, String where, Integer pageIndex, Integer pageSize) {
        String sql =  "select * from " + tableName
                + (StringUtils.isEmpty(where) ? "" : " where " + where)
                + " limit " + (pageIndex - 1) * pageSize + "," + pageSize + ";";
        List<Map<String, Object>> list = new ArrayList();
        List<String> fileds = new ArrayList<>();
        ResultSetMetaData rsmd = null;
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                fileds.add(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();

                for(String filed:fileds) {
                    map.put(filed, rs.getObject(filed));
                }
                list.add(map);
            }

        } catch (Exception e) {
            log.error("{}", e);
            throw new RuntimeException(e.getMessage());
        }

        sql = "select count(0) from " + tableName;
        int total = 0;
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if  (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            log.error("{}", e);
            throw new RuntimeException(e.getMessage());
        }
        PageUtils page = new PageUtils(list, total, pageSize, pageIndex);
        return page;
    }
}
