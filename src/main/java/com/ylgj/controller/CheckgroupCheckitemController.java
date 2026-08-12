package com.ylgj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylgj.commons.Result;
import com.ylgj.pojo.CheckgroupCheckitem;
import com.ylgj.service.CheckgroupCheckitemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/checkgroup-checkitem")
public class CheckgroupCheckitemController {

    @Autowired
    private CheckgroupCheckitemService checkgroupCheckitemService;

    @GetMapping("/findCheckItemIdsByCheckGroupId/{checkGroupId}")
    public Result findCheckItemIdsByCheckGroupId(@PathVariable Integer checkGroupId) {
        QueryWrapper<CheckgroupCheckitem> wrapper = new QueryWrapper<>();
        wrapper.eq("checkgroup_id", checkGroupId);
        List<CheckgroupCheckitem> list = checkgroupCheckitemService.list(wrapper);
        List<Integer> checkItemIds = list.stream()
                .map(CheckgroupCheckitem::getCheckitemId)
                .collect(Collectors.toList());
        return new Result(true, "查询成功", checkItemIds);
    }

    @PostMapping("/editCheckGroupAndCheckItem")
    public Result editCheckGroupAndCheckItem(@RequestParam Integer checkGroupId, @RequestParam Integer[] checkitemIds) {
        try {
            // 先删除旧的关联关系
            QueryWrapper<CheckgroupCheckitem> wrapper = new QueryWrapper<>();
            wrapper.eq("checkgroup_id", checkGroupId);
            checkgroupCheckitemService.remove(wrapper);
            // 再添加新的关联关系
            if (checkitemIds != null && checkitemIds.length > 0) {
                for (Integer checkitemId : checkitemIds) {
                    CheckgroupCheckitem cgc = new CheckgroupCheckitem();
                    cgc.setCheckgroupId(checkGroupId);
                    cgc.setCheckitemId(checkitemId);
                    checkgroupCheckitemService.save(cgc);
                }
            }
            return new Result(true, "编辑成功");
        } catch (Exception e) {
            return new Result(false, "编辑失败");
        }
    }
}
