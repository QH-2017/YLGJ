package com.ylgj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylgj.commons.BizException;
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
     * 根据手机号模糊查询会员的预约记录（支持输入部分号码，管理端/移动端共用）
     * <p>
     * 返回结构同时包含：
     * - 嵌套 member / setmeal 对象（管理端 OrderList、移动端 checkrecord_list 使用）
     * - 平铺 memberName / setmealName / setmealPrice 字段（移动端 my-orders 使用，向后兼容）
     */
    @GetMapping("/findByPhone")
    public Result findByPhone(@RequestParam String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return new Result(false, "请输入手机号");
        }
        String keyword = phone.trim();
        // 按手机号模糊匹配会员（支持部分号码）
        QueryWrapper<Member> memberWrapper = new QueryWrapper<>();
        memberWrapper.like("phoneNumber", keyword);
        memberWrapper.orderByDesc("regTime");
        List<Member> members = memberService.list(memberWrapper);
        if (members.isEmpty()) {
            return new Result(false, "未找到该手机号对应的会员，请先预约");
        }
        // 收集会员ID
        Map<Integer, Member> memberMap = new HashMap<>();
        List<Integer> memberIds = new ArrayList<>();
        for (Member m : members) {
            memberMap.put(m.getId(), m);
            memberIds.add(m.getId());
        }
        // 查询这些会员的所有订单
        QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
        orderWrapper.in("member_id", memberIds);
        orderWrapper.orderByDesc("orderDate");
        List<Order> orders = orderService.list(orderWrapper);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Order order : orders) {
            Member member = memberMap.get(order.getMemberId());
            Setmeal setmeal = setmealService.getById(order.getSetmealId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", order.getId());
            item.put("orderDate", order.getOrderDate() != null ? order.getOrderDate().toString() : "");
            item.put("orderType", order.getOrderType() != null ? order.getOrderType() : "");
            item.put("orderStatus", order.getOrderStatus() != null ? order.getOrderStatus() : "");
            item.put("setmealId", order.getSetmealId());
            // 嵌套会员信息（管理端 / checkrecord_list 使用）
            Map<String, Object> memberInfo = new LinkedHashMap<>();
            memberInfo.put("id", member != null ? member.getId() : null);
            memberInfo.put("name", member != null ? member.getName() : "未知");
            memberInfo.put("phoneNumber", member != null ? member.getPhoneNumber() : "");
            memberInfo.put("sex", member != null ? member.getSex() : "");
            item.put("member", memberInfo);
            // 嵌套套餐信息
            Map<String, Object> setmealInfo = new LinkedHashMap<>();
            setmealInfo.put("id", setmeal != null ? setmeal.getId() : null);
            setmealInfo.put("name", setmeal != null ? setmeal.getName() : "未知套餐");
            setmealInfo.put("price", setmeal != null ? setmeal.getPrice() : 0);
            item.put("setmeal", setmealInfo);
            // 平铺字段（my-orders.html 使用，向后兼容）
            item.put("memberName", member != null ? member.getName() : "未知");
            item.put("phone", member != null ? member.getPhoneNumber() : "");
            item.put("setmealName", setmeal != null ? setmeal.getName() : "未知套餐");
            item.put("setmealPrice", setmeal != null ? setmeal.getPrice() : 0);
            list.add(item);
        }
        return new Result(true, "查询成功", list);
    }

    /**
     * 移动端提交预约（自动创建会员 + 创建订单）
     * 前端传参: name, sex, telephone, idCard, setmealId, orderDate
     * <p>参数解析完成后交给 {@link OrderService#submitOrder}，业务校验与事务（建会员 + 建订单）统一在 Service 层完成。</p>
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String, String> params) {
        String setmealIdStr = params.get("setmealId");
        String orderDateStr = params.get("orderDate");
        if (setmealIdStr == null || setmealIdStr.trim().isEmpty()) {
            throw new BizException("缺少套餐信息");
        }
        if (orderDateStr == null || orderDateStr.trim().isEmpty()) {
            throw new BizException("请选择体检日期");
        }
        Integer setmealId;
        LocalDate orderDate;
        try {
            setmealId = Integer.parseInt(setmealIdStr.trim());
            orderDate = LocalDate.parse(orderDateStr.trim());
        } catch (Exception e) {
            throw new BizException("套餐或体检日期格式不正确");
        }
        Order order = orderService.submitOrder(
                params.get("name"), params.get("sex"), params.get("telephone"),
                params.get("idCard"), setmealId, orderDate);
        return new Result(true, "预约成功", order.getId());
    }

    /**
     * 修改预约信息（改期 / 改状态）
     */
    @PostMapping("/update")
    public Result update(@RequestBody Map<String, String> params) {
        String orderIdStr = params.get("orderId");
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            throw new BizException("缺少订单ID");
        }
        Integer orderId = Integer.parseInt(orderIdStr.trim());
        String orderDateStr = params.get("orderDate");
        LocalDate orderDate = (orderDateStr == null || orderDateStr.isEmpty())
                ? null : LocalDate.parse(orderDateStr);
        orderService.updateOrder(orderId, orderDate, params.get("orderStatus"));
        return new Result(true, "修改成功");
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel")
    public Result cancel(@RequestBody Map<String, String> params) {
        String orderIdStr = params.get("orderId");
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            throw new BizException("缺少订单ID");
        }
        orderService.cancelOrder(Integer.parseInt(orderIdStr.trim()));
        return new Result(true, "取消成功");
    }
}
