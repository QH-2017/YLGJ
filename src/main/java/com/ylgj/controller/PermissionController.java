package com.ylgj.controller;

import com.ylgj.commons.Result;
import com.ylgj.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/findAll")
    public Result findAll() {
        return new Result(true, "查询权限成功", permissionService.list());
    }
}
