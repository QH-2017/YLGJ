package com.ylgj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylgj.commons.Result;
import com.ylgj.pojo.Member;
import com.ylgj.pojo.Order;
import com.ylgj.pojo.Setmeal;
import com.ylgj.service.MemberService;
import com.ylgj.service.OrderService;
import com.ylgj.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/findAll")
    public Result findAll() {
        List<Order> orders = orderService.list();
        Map<Integer, Member> memberMap = new HashMap<>();
        for (Member m : memberService.list()) memberMap.put(m.getId(), m);
        Map<Integer, Setmeal> setmealMap = new HashMap<>();
        for (Setmeal s : setmealService.list()) setmealMap.put(s.getId(), s);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Order order : orders) {
            Member member = memberMap.get(order.getMemberId());
            Setmeal setmeal = setmealMap.get(order.getSetmealId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", order.getId());
            item.put("orderDate", order.getOrderDate() != null ? order.getOrderDate().toString() : "");
            item.put("orderType", order.getOrderType());
            item.put("orderStatus", order.getOrderStatus());
            item.put("setmealId", order.getSetmealId());
            // 会员信息
            Map<String, Object> memberInfo = new LinkedHashMap<>();
            memberInfo.put("id", member != null ? member.getId() : null);
            memberInfo.put("name", member != null ? member.getName() : "未知");
            memberInfo.put("phoneNumber", member != null ? member.getPhoneNumber() : "");
            memberInfo.put("sex", member != null ? member.getSex() : "");
            item.put("member", memberInfo);
            // 套餐信息
            Map<String, Object> setmealInfo = new LinkedHashMap<>();
            setmealInfo.put("id", setmeal != null ? setmeal.getId() : null);
            setmealInfo.put("name", setmeal != null ? setmeal.getName() : "未知套餐");
            setmealInfo.put("price", setmeal != null ? setmeal.getPrice() : 0);
            item.put("setmeal", setmealInfo);
            list.add(item);
        }
        return new Result(true, "查询预约信息成功", list);
    }

    /**
     * 根据订单ID查询预约详情（包含会员和套餐信息）
     */
    @GetMapping("/findById/{id}")
    public Result findById(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return new Result(false, "订单不存在");
        }
        Member member = memberService.getById(order.getMemberId());
        Setmeal setmeal = setmealService.getById(order.getSetmealId());

        java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("id", order.getId());
        data.put("member", member != null ? member.getName() : "未知");
        data.put("setmeal", setmeal != null ? setmeal.getName() : "未知套餐");
        data.put("orderDate", order.getOrderDate() != null ? order.getOrderDate().toString() : "");
        data.put("orderType", order.getOrderType() != null ? order.getOrderType() : "");
        data.put("orderStatus", order.getOrderStatus() != null ? order.getOrderStatus() : "");
        return new Result(true, "查询成功", data);
    }

    /**
     * 根据手机号查询该会员的所有预约记录（移动端报告查询用）
     */
    @GetMapping("/findByPhone")
    public Result findByPhone(@RequestParam String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return new Result(false, "请输入手机号");
        }
        // 查找会员
        QueryWrapper<Member> memberWrapper = new QueryWrapper<>();
        memberWrapper.eq("phoneNumber", phone.trim());
        Member member = memberService.getOne(memberWrapper);
        if (member == null) {
            return new Result(false, "未找到该手机号对应的会员，请先预约");
        }
        // 查询该会员的所有订单
        QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("member_id", member.getId());
        orderWrapper.orderByDesc("orderDate");
        java.util.List<Order> orders = orderService.list(orderWrapper);

        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        for (Order order : orders) {
            Setmeal setmeal = setmealService.getById(order.getSetmealId());
            java.util.Map<String, Object> item = new java.util.LinkedHashMap<>();
            item.put("id", order.getId());
            item.put("memberName", member.getName());
            item.put("phone", member.getPhoneNumber());
            item.put("setmealName", setmeal != null ? setmeal.getName() : "未知套餐");
            item.put("setmealPrice", setmeal != null ? setmeal.getPrice() : 0);
            item.put("orderDate", order.getOrderDate() != null ? order.getOrderDate().toString() : "");
            item.put("orderType", order.getOrderType() != null ? order.getOrderType() : "");
            item.put("orderStatus", order.getOrderStatus() != null ? order.getOrderStatus() : "");
            list.add(item);
        }
        return new Result(true, "查询成功", list);
    }

    /**
     * 移动端提交预约（自动创建会员 + 创建订单）
     * 前端传参: name, sex, telephone, idCard, setmealId, orderDate
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String, String> params) {
        try {
            String name = params.get("name");
            String sex = params.get("sex");
            String telephone = params.get("telephone");
            String idCard = params.get("idCard");
            String setmealIdStr = params.get("setmealId");
            String orderDateStr = params.get("orderDate");

            // 参数校验
            if (name == null || name.trim().isEmpty()) {
                return new Result(false, "请输入体检人姓名");
            }
            if (telephone == null || !telephone.matches("^1[3-9]\\d{9}$")) {
                return new Result(false, "请输入正确的11位手机号");
            }
            if (idCard == null || !idCard.matches("^\\d{17}[\\dXx]$")) {
                return new Result(false, "请输入正确的18位身份证号");
            }
            if (setmealIdStr == null || setmealIdStr.isEmpty()) {
                return new Result(false, "缺少套餐信息");
            }
            if (orderDateStr == null || orderDateStr.isEmpty()) {
                return new Result(false, "请选择体检日期");
            }

            Integer setmealId = Integer.parseInt(setmealIdStr);
            LocalDate orderDate = LocalDate.parse(orderDateStr);

            // 查找或创建会员（按手机号查找）
            QueryWrapper<Member> wrapper = new QueryWrapper<>();
            wrapper.eq("phoneNumber", telephone);
            Member member = memberService.getOne(wrapper);

            if (member == null) {
                // 新建会员
                member = new Member();
                member.setName(name.trim());
                member.setSex(sex);
                member.setPhoneNumber(telephone);
                member.setIdCard(idCard);
                member.setRegTime(LocalDate.now());
                memberService.save(member);
            }

            // 创建订单
            Order order = new Order();
            order.setMemberId(member.getId());
            order.setSetmealId(setmealId);
            order.setOrderDate(orderDate);
            order.setOrderType("微信预约");
            order.setOrderStatus("未到诊");
            orderService.save(order);

            return new Result(true, "预约成功", order.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "预约失败：" + e.getMessage());
        }
    }

    /**
     * 修改预约信息
     */
    @PostMapping("/update")
    public Result update(@RequestBody Map<String, String> params) {
        try {
            String orderIdStr = params.get("orderId");
            String orderDateStr = params.get("orderDate");
            String orderStatus = params.get("orderStatus");

            if (orderIdStr == null || orderIdStr.isEmpty()) {
                return new Result(false, "缺少订单ID");
            }
            Integer orderId = Integer.parseInt(orderIdStr);
            Order order = orderService.getById(orderId);
            if (order == null) {
                return new Result(false, "订单不存在");
            }
            if (orderDateStr != null && !orderDateStr.isEmpty()) {
                order.setOrderDate(LocalDate.parse(orderDateStr));
            }
            if (orderStatus != null && !orderStatus.isEmpty()) {
                order.setOrderStatus(orderStatus);
            }
            orderService.updateById(order);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            return new Result(false, "修改失败：" + e.getMessage());
        }
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel")
    public Result cancel(@RequestBody Map<String, String> params) {
        try {
            String orderIdStr = params.get("orderId");
            if (orderIdStr == null || orderIdStr.isEmpty()) {
                return new Result(false, "缺少订单ID");
            }
            Integer orderId = Integer.parseInt(orderIdStr);
            Order order = orderService.getById(orderId);
            if (order == null) {
                return new Result(false, "订单不存在");
            }
            order.setOrderStatus("已取消");
            orderService.updateById(order);
            return new Result(true, "取消成功");
        } catch (Exception e) {
            return new Result(false, "取消失败：" + e.getMessage());
        }
    }
}
