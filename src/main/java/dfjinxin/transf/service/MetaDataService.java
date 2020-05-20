package dfjinxin.transf.service;

import dfjinxin.transf.util.PageUtils;

import java.util.List;

public interface MetaDataService {

    List tableList();

    List fieldList(String tableName);

    PageUtils dataList(String tableName, String where, Integer pageIndex, Integer pageSize);
}
