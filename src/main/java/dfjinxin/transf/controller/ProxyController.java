package dfjinxin.transf.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.*;
import dfjinxin.transf.util.R;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @GetMapping("/get")
    public R get(@RequestParam("url") String url) {
        String content = HttpUtil.get(url);
        JSON json = null;

        try {
            json = JSONObject.parseObject(content);
        } catch (Exception ex) {
            json = JSONObject.parseArray(content);
        }
        return R.ok().put("data", json);
    }

    @PostMapping("/post")
    public R post(@RequestParam Map<String, Object> params) {
        String url = (String) params.get("url");
        params.remove("url");

        String content = HttpUtil.post(url, params);
        JSON json = null;

        try {
            json = JSONObject.parseObject(content);
        } catch (Exception ex) {
            json = JSONObject.parseArray(content);
        }
        return R.ok().put("data", json);
    }
}
