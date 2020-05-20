package dfjinxin.transf.controller;

import org.springframework.web.bind.annotation.*;
import dfjinxin.transf.util.R;
import java.util.Map;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @GetMapping("/get")
    public R get(@RequestParam("url") String url) {
        return R.ok();
    }

    @PostMapping("/post")
    public R post(@RequestBody Map<String, String> params) {
        return R.ok();
    }
}
