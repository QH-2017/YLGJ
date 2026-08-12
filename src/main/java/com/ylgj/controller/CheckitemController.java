package com.ylgj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylgj.commons.MessageConstant;
import com.ylgj.commons.PageResult;
import com.ylgj.commons.QueryPageBean;
import com.ylgj.commons.Result;
import com.ylgj.pojo.Checkitem;
import com.ylgj.service.CheckitemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lixin
 * @since 2026-07-04
 */
@RestController
@RequestMapping("/checkitem")
public class CheckitemController {

    @Autowired
    private CheckitemService checkitemService;

    @PostMapping("/add")
    public Result add(@RequestBody Checkitem checkitem) {  // 当后台提交的数据是json格式时，controller的方法中要使用 @RequestBody进行接受
        try {
            checkitemService.save(checkitem);
            return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
            // {flag:true, message:'新增检查项成功',null}
        }catch (Exception e){
            return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
        }
    }

    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {

        Page<Checkitem> pageParam = new Page<>(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());

        // 根据queryPageBean里面的 queryString 是否有值来决定使用拼接查询条件
        QueryWrapper<Checkitem> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        if (queryPageBean.getQueryString()!=null && !queryPageBean.getQueryString().equals("")) {
            queryWrapper.like("code", queryPageBean.getQueryString());
            queryWrapper.or();
            queryWrapper.like("name", queryPageBean.getQueryString());
        }

        Page<Checkitem> page = checkitemService.page(pageParam, queryWrapper);

        return new PageResult(page.getTotal(), page.getRecords());

    }

    @DeleteMapping("/deleteInfoById/{id}")
    public Result deleteInfoById(@PathVariable Integer id) {
         try {
             checkitemService.removeById(id);
             return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
         }catch (Exception e){
             return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
         }
    }

    @GetMapping("/findAll")
    public Result findAll() {
        return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS, checkitemService.list());
    }

    @PostMapping("/edit")
    public Result edit(@RequestBody Checkitem checkitem) {
        try {
            checkitemService.updateById(checkitem);
            return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
        } catch (Exception e) {
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
    }

    @GetMapping("/findById/{id}")
    public Result findById(@PathVariable Integer id) {
        Checkitem checkitem = checkitemService.getById(id);
        return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS, checkitem);
    }

}
