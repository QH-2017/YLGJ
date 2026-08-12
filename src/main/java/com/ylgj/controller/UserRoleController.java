package com.ylgj.controller;

import com.ylgj.commons.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-role")
public class UserRoleController {

    @GetMapping("/findAll")
    public Result findAll() {
        return new Result(true, "查询成功");
    }
}
