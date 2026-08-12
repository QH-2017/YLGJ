package com.ylgj.controller;

import com.ylgj.commons.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setmeal-checkgroup")
public class SetmealCheckgroupController {

    @GetMapping("/findAll")
    public Result findAll() {
        return new Result(true, "查询成功");
    }
}
