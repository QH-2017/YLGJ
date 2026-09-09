package com.ylgj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylgj.commons.Result;
import com.ylgj.pojo.Menu;
import com.ylgj.pojo.RoleMenu;
import com.ylgj.pojo.UserRole;
import com.ylgj.security.SecurityUser;
import com.ylgj.service.MenuService;
import com.ylgj.service.RoleMenuService;
import com.ylgj.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleMenuService roleMenuService;

    @GetMapping("/findAll")
    public Result findAll() {
        List<Menu> list = menuService.list();
        return new Result(true, "查询菜单成功", list);
    }

    /**
     * 当前登录用户的可见菜单（RBAC 动态菜单）：
     * user → role → menu 三级联查；用户无角色或未配置菜单时返回全量（系统管理员兜底）。
     */
    @GetMapping("/my")
    public Result my() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            userId = ((SecurityUser) authentication.getPrincipal()).getUserId();
        }
        if (userId == null) {
            return new Result(true, "未登录，返回全量菜单", menuService.list());
        }

        // 1. 用户角色
        List<Integer> roleIds = new ArrayList<>();
        for (UserRole ur : userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", userId))) {
            if (ur.getRoleId() != null) roleIds.add(ur.getRoleId());
        }

        // 2. 角色 -> 菜单
        List<Menu> menus;
        if (roleIds.isEmpty()) {
            menus = menuService.list();
        } else {
            List<Integer> menuIds = new ArrayList<>();
            for (RoleMenu rm : roleMenuService.list(new QueryWrapper<RoleMenu>().in("role_id", roleIds))) {
                if (rm.getMenuId() != null) menuIds.add(rm.getMenuId());
            }
            menus = menuIds.isEmpty() ? menuService.list() : menuService.listByIds(menuIds);
        }

        menus.sort(Comparator.comparing(Menu::getPriority, Comparator.nullsLast(Integer::compareTo)));
        return new Result(true, "查询我的菜单成功", menus);
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
