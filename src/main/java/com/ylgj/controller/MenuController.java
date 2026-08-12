package com.ylgj.controller;

import com.ylgj.commons.Result;
import com.ylgj.pojo.Menu;
import com.ylgj.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/findAll")
    public Result findAll() {
        List<Menu> list = menuService.list();
        return new Result(true, "查询菜单成功", list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Menu menu) {
        try {
            menuService.save(menu);
            return new Result(true, "新增菜单成功");
        } catch (Exception e) {
            return new Result(false, "新增菜单失败");
        }
    }

    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        try {
            menuService.removeById(id);
            return new Result(true, "删除菜单成功");
        } catch (Exception e) {
            return new Result(false, "删除菜单失败");
        }
    }
}
