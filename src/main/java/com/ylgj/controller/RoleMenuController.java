package com.ylgj.controller;

import com.ylgj.commons.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role-menu")
public class RoleMenuController {

    @GetMapping("/findAll")
    public Result findAll() {
        return new Result(true, "查询成功");
    }
}
