package com.ylgj.controller;

import com.ylgj.commons.Result;
import com.ylgj.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/findAll")
    public Result findAll() {
        return new Result(true, "查询角色成功", roleService.list());
    }
}
