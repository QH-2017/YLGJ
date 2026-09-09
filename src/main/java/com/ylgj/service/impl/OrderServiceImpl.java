package com.ylgj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ylgj.commons.BizException;
import com.ylgj.mapper.OrderMapper;
import com.ylgj.pojo.Member;
import com.ylgj.pojo.Order;
import com.ylgj.pojo.Ordersetting;
import com.ylgj.pojo.Setmeal;
import com.ylgj.service.MemberService;
import com.ylgj.service.OrderService;
import com.ylgj.service.OrdersettingService;
import com.ylgj.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 订单服务实现：核心写操作（下单 / 改期 / 取消）统一收敛于此，
 * 通过 {@link @Transactional} 保证"建会员 + 建订单"两步写的原子性。
 *
 * @author lixin
 * @since 2026-07-04
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private MemberService memberService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private OrdersettingService ordersettingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order submitOrder(String name, String sex, String telephone, String idCard,
                             Integer setmealId, LocalDate orderDate) {
        // ---------- 参数业务校验 ----------
        if (name == null || name.trim().isEmpty()) {
            throw new BizException("请输入体检人姓名");
        }
        if (telephone == null || !telephone.matches("^1[3-9]\\d{9}$")) {
            throw new BizException("请输入正确的11位手机号");
        }
        if (idCard == null || !idCard.matches("^\\d{17}[\\dXx]$")) {
            throw new BizException("请输入正确的18位身份证号");
        }
        if (setmealId == null) {
            throw new BizException("缺少套餐信息");
        }
        if (orderDate == null) {
            throw new BizException("请选择体检日期");
        }
        if (orderDate.isBefore(LocalDate.now())) {
            throw new BizException("体检日期不能早于今天");
        }
        // 校验套餐存在，避免产生悬空引用
        Setmeal setmeal = setmealService.getById(setmealId);
        if (setmeal == null) {
            throw new BizException("所选套餐不存在或已下架");
        }

        // 当日名额校验 + 原子占号（有排班才限流，防止并发超卖；占号与建单同事务）
        reserveForDate(orderDate);

        // ---------- 查找或创建会员（按手机号） ----------
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("phoneNumber", telephone);
        Member member = memberService.getOne(wrapper);
        if (member == null) {
            member = new Member();
            member.setName(name.trim());
            member.setSex(sex);
            member.setPhoneNumber(telephone);
            member.setIdCard(idCard);
            member.setRegTime(LocalDate.now());
            memberService.save(member);
        }

        // ---------- 创建订单 ----------
        Order order = new Order();
        order.setMemberId(member.getId());
        order.setSetmealId(setmealId);
        order.setOrderDate(orderDate);
        order.setOrderType("微信预约");
        order.setOrderStatus("未到诊");
        save(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order updateOrder(Integer orderId, LocalDate orderDate, String orderStatus) {
        if (orderId == null) {
            throw new BizException("缺少订单ID");
        }
        Order order = getById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        // 改期：释放原日期名额，再为新日期占号（事务内保证一致）
        if (orderDate != null && !orderDate.equals(order.getOrderDate())) {
            if (orderDate.isBefore(LocalDate.now())) {
                throw new BizException("体检日期不能早于今天");
            }
            if (order.getOrderDate() != null) {
                ordersettingService.releaseReserve(order.getOrderDate());
            }
            reserveForDate(orderDate);
            order.setOrderDate(orderDate);
        }
        if (orderStatus != null && !orderStatus.isEmpty()) {
            order.setOrderStatus(orderStatus);
        }
        updateById(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order cancelOrder(Integer orderId) {
        if (orderId == null) {
            throw new BizException("缺少订单ID");
        }
        Order order = getById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        order.setOrderStatus("已取消");
        updateById(order);
        // 取消后释放当日预约名额（如有排班）
        if (order.getOrderDate() != null) {
            ordersettingService.releaseReserve(order.getOrderDate());
        }
        return order;
    }

    /**
     * 预约名额校验与占号：
     * <ul>
     *   <li>该日有排班（ordersetting）：已约满则拒绝；否则原子累加已预约数占号，防止并发超卖</li>
     *   <li>该日无排班：视为未开放 / 宽松模式放行（演示库通常仅对未来开放日建排班）</li>
     * </ul>
     * 占号与订单写入处于同一事务，下单失败时随事务回滚自动撤销。
     */
    private void reserveForDate(LocalDate orderDate) {
        Ordersetting setting = ordersettingService.getByDate(orderDate);
        if (setting == null) {
            return;
        }
        int number = setting.getNumber() == null ? 0 : setting.getNumber();
        int reserved = setting.getReservations() == null ? 0 : setting.getReservations();
        if (reserved >= number) {
            throw new BizException("当日预约名额已满，请选择其他日期");
        }
        if (!ordersettingService.tryReserve(orderDate)) {
            throw new BizException("当日预约名额已被抢完，请选择其他日期");
        }
    }
}
