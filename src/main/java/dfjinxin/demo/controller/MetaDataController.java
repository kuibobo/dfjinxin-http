package dfjinxin.demo.controller;

import dfjinxin.demo.service.MetaDataService;
import dfjinxin.demo.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 元数据Controller
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/metadata")
public class MetaDataController {

    @Autowired
    private MetaDataService metaDataService;

    @GetMapping("/table/list")
    public R tableList() {
        return R.ok().put("data", metaDataService.tableList());
    }

    @GetMapping("/field/list")
    public R fieldList(@RequestParam("tableName") String tableName) {
        return R.ok().put("data", metaDataService.fieldList(tableName));
    }

    @GetMapping("/data/list")
    public R dataList(@RequestParam("tableName") String tableName,
                      @RequestParam(value = "where", required = false) String where,
                      @RequestParam("pageIndex") Integer pageIndex,
                      @RequestParam("pageSize") Integer pageSize) {
        return R.ok().put("data", metaDataService.dataList(tableName, where, pageIndex, pageSize));
    }
}
