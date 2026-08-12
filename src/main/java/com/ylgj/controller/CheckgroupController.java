package com.ylgj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylgj.commons.MessageConstant;
import com.ylgj.commons.PageResult;
import com.ylgj.commons.QueryPageBean;
import com.ylgj.commons.Result;
import com.ylgj.pojo.Checkgroup;
import com.ylgj.pojo.CheckgroupCheckitem;
import com.ylgj.service.CheckgroupCheckitemService;
import com.ylgj.service.CheckgroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkgroup")
public class CheckgroupController {

    @Autowired
    private CheckgroupService checkgroupService;

    @Autowired
    private CheckgroupCheckitemService checkgroupCheckitemService;

    @PostMapping("/add")
    @Transactional
    public Result add(@RequestBody Checkgroup checkgroup) {
        try {
            checkgroup.setId(null);
            checkgroupService.save(checkgroup);
            saveCheckgroupCheckitem(checkgroup);
            return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
        }
    }

    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        Page<Checkgroup> pageParam = new Page<>(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        QueryWrapper<Checkgroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (queryPageBean.getQueryString() != null && !queryPageBean.getQueryString().equals("")) {
            queryWrapper.like("code", queryPageBean.getQueryString());
            queryWrapper.or();
            queryWrapper.like("name", queryPageBean.getQueryString());
            queryWrapper.or();
            queryWrapper.like("helpCode", queryPageBean.getQueryString());
        }
        Page<Checkgroup> page = checkgroupService.page(pageParam, queryWrapper);
        return new PageResult(page.getTotal(), page.getRecords());
    }

    @GetMapping("/findAll")
    public Result findAll() {
        List<Checkgroup> list = checkgroupService.list();
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, list);
    }

    @DeleteMapping("/deleteInfoById/{id}")
    @Transactional
    public Result deleteInfoById(@PathVariable Integer id) {
        try {
            QueryWrapper<CheckgroupCheckitem> wrapper = new QueryWrapper<>();
            wrapper.eq("checkgroup_id", id);
            checkgroupCheckitemService.remove(wrapper);
            checkgroupService.removeById(id);
            return new Result(true, MessageConstant.DELETE_CHECKGROUP_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_CHECKGROUP_FAIL);
        }
    }

    @PostMapping("/edit")
    @Transactional
    public Result edit(@RequestBody Checkgroup checkgroup) {
        try {
            checkgroupService.updateById(checkgroup);
            QueryWrapper<CheckgroupCheckitem> wrapper = new QueryWrapper<>();
            wrapper.eq("checkgroup_id", checkgroup.getId());
            checkgroupCheckitemService.remove(wrapper);
            saveCheckgroupCheckitem(checkgroup);
            return new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
    }

    private void saveCheckgroupCheckitem(Checkgroup checkgroup) {
        List<Integer> checkitemIds = checkgroup.getCheckitemIds();
        if (checkitemIds != null && !checkitemIds.isEmpty()) {
            for (Integer checkitemId : checkitemIds) {
                CheckgroupCheckitem cgc = new CheckgroupCheckitem();
                cgc.setCheckgroupId(checkgroup.getId());
                cgc.setCheckitemId(checkitemId);
                checkgroupCheckitemService.save(cgc);
            }
        }
    }

    @GetMapping("/findById/{id}")
    public Result findById(@PathVariable Integer id) {
        Checkgroup checkgroup = checkgroupService.getById(id);
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, checkgroup);
    }
}
