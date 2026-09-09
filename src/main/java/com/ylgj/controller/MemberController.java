package com.ylgj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylgj.commons.MessageConstant;
import com.ylgj.commons.PageResult;
import com.ylgj.commons.QueryPageBean;
import com.ylgj.commons.Result;
import com.ylgj.pojo.Member;
import com.ylgj.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/add")
    public Result add(@RequestBody Member member) {
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            return new Result(false, "会员姓名不能为空");
        }
        if (member.getPhoneNumber() == null || !member.getPhoneNumber().matches("^1[3-9]\\d{9}$")) {
            return new Result(false, "请输入正确的11位手机号");
        }
        if (member.getIdCard() == null || !member.getIdCard().matches("^\\d{17}[\\dXx]$")) {
            return new Result(false, "请输入正确的18位身份证号");
        }
        // 检查手机号是否已存在
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Member> wrapper =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("phoneNumber", member.getPhoneNumber());
        if (memberService.count(wrapper) > 0) {
            return new Result(false, "该手机号已被注册，请勿重复添加");
        }
        try {
            if (member.getRegTime() == null) {
                member.setRegTime(java.time.LocalDate.now());
            }
            memberService.save(member);
            return new Result(true, MessageConstant.ADD_MEMBER_SUCCESS);
        } catch (Exception e) {
            return new Result(false, MessageConstant.ADD_MEMBER_FAIL);
        }
    }

    /**
     * 移动端按手机号精确查询会员健康档案（匿名，供“我的/健康档案”使用）
     */
    @GetMapping("/findByPhone")
    public Result findByPhone(@RequestParam String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return new Result(false, "请输入手机号");
        }
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("phoneNumber", phone.trim());
        Member member = memberService.getOne(wrapper, false);
        if (member == null) {
            return new Result(false, "未找到该手机号对应的健康档案，请先完成一次预约");
        }
        return new Result(true, "查询成功", member);
    }

    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        Page<Member> pageParam = new Page<>(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (queryPageBean.getQueryString() != null && !queryPageBean.getQueryString().equals("")) {
            queryWrapper.like("name", queryPageBean.getQueryString());
            queryWrapper.or();
            queryWrapper.like("phoneNumber", queryPageBean.getQueryString());
            queryWrapper.or();
            queryWrapper.like("idCard", queryPageBean.getQueryString());
        }
        Page<Member> page = memberService.page(pageParam, queryWrapper);
        return new PageResult(page.getTotal(), page.getRecords());
    }

    @DeleteMapping("/deleteInfoById/{id}")
    public Result deleteInfoById(@PathVariable Integer id) {
        try {
            memberService.removeById(id);
            return new Result(true, MessageConstant.DELETE_MEMBER_SUCCESS);
        } catch (Exception e) {
            return new Result(false, MessageConstant.DELETE_MEMBER_FAIL);
        }
    }

    @PostMapping("/edit")
    public Result edit(@RequestBody Member member) {
        try {
            memberService.updateById(member);
            return new Result(true, MessageConstant.EDIT_MEMBER_SUCCESS);
        } catch (Exception e) {
            return new Result(false, MessageConstant.EDIT_MEMBER_FAIL);
        }
    }

    @GetMapping("/findById/{id}")
    public Result findById(@PathVariable Integer id) {
        Member member = memberService.getById(id);
        return new Result(true, "查询会员成功", member);
    }

    @GetMapping("/findAll")
    public Result findAll() {
        return new Result(true, "查询会员列表成功", memberService.list());
    }
}
