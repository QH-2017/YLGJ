package com.ylgj.service.impl;

import com.ylgj.commons.BizException;
import com.ylgj.mapper.OrderMapper;
import com.ylgj.pojo.Member;
import com.ylgj.pojo.Order;
import com.ylgj.pojo.Ordersetting;
import com.ylgj.pojo.Setmeal;
import com.ylgj.service.MemberService;
import com.ylgj.service.OrdersettingService;
import com.ylgj.service.SetmealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link OrderServiceImpl} 预约下单 / 改期 / 取消逻辑单元测试（Mockito 模拟 Mapper 与关联 Service）。
 */
class OrderServiceImplTest {

    @Mock
    private MemberService memberService;
    @Mock
    private SetmealService setmealService;
    @Mock
    private OrdersettingService ordersettingService;
    @Mock
    private OrderMapper orderMapper;

    private OrderServiceImpl orderService;

    private final LocalDate today = LocalDate.now();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderServiceImpl();
        // 注入依赖（模拟 Spring 字段注入）
        ReflectionTestUtils.setField(orderService, "memberService", memberService);
        ReflectionTestUtils.setField(orderService, "setmealService", setmealService);
        ReflectionTestUtils.setField(orderService, "baseMapper", orderMapper);
        ReflectionTestUtils.setField(orderService, "ordersettingService", ordersettingService);
    }

    private Setmeal stubSetmeal(Integer id) {
        Setmeal setmeal = new Setmeal().setId(id).setName("入职体检").setPrice(300.0);
        when(setmealService.getById(id)).thenReturn(setmeal);
        return setmeal;
    }

    @Test
    void submitOrder_newMember_createsMemberAndOrder() {
        stubSetmeal(1);
        when(memberService.getOne(any())).thenReturn(null);

        Order order = orderService.submitOrder("张三", "男", "13800138000",
                "110101199001011234", 1, today);

        assertNotNull(order);
        assertEquals("微信预约", order.getOrderType());
        assertEquals("未到诊", order.getOrderStatus());
        // 新会员只创建一次
        verify(memberService).save(any(Member.class));
        // 订单落库
        verify(orderMapper).insert(any(Order.class));
    }

    @Test
    void submitOrder_existingMember_reusesWithoutDuplicate() {
        stubSetmeal(1);
        Member existing = new Member().setId(5).setName("张三").setPhoneNumber("13800138000");
        when(memberService.getOne(any())).thenReturn(existing);

        Order order = orderService.submitOrder("张三", "男", "13800138000",
                "110101199001011234", 1, today);

        assertEquals(Integer.valueOf(5), order.getMemberId(), "应复用已有会员ID");
        verify(memberService, never()).save(any(Member.class));
    }

    @Test
    void submitOrder_invalidPhone_rejected() {
        stubSetmeal(1);
        when(memberService.getOne(any())).thenReturn(null);

        BizException ex = assertThrows(BizException.class,
                () -> orderService.submitOrder("张三", "男", "123", "110101199001011234", 1, today));
        assertEquals("请输入正确的11位手机号", ex.getMessage());
        verify(orderMapper, never()).insert(any(Order.class));
    }

    @Test
    void submitOrder_setmealNotExist_rejected() {
        // 未 stub setmealService.getById → 返回 null
        BizException ex = assertThrows(BizException.class,
                () -> orderService.submitOrder("张三", "男", "13800138000",
                        "110101199001011234", 999, today));
        assertEquals("所选套餐不存在或已下架", ex.getMessage());
    }

    @Test
    void submitOrder_pastDate_rejected() {
        stubSetmeal(1);
        when(memberService.getOne(any())).thenReturn(null);

        assertThrows(BizException.class,
                () -> orderService.submitOrder("张三", "男", "13800138000",
                        "110101199001011234", 1, today.minusDays(1)));
    }

    @Test
    void cancelOrder_notExist_throws() {
        when(orderMapper.selectById(999)).thenReturn(null);

        BizException ex = assertThrows(BizException.class, () -> orderService.cancelOrder(999));
        assertEquals("订单不存在", ex.getMessage());
    }

    @Test
    void cancelOrder_existing_markCancelled() {
        Order order = new Order().setId(9).setOrderStatus("未到诊");
        when(orderMapper.selectById(9)).thenReturn(order);

        Order cancelled = orderService.cancelOrder(9);

        assertEquals("已取消", cancelled.getOrderStatus());
        verify(orderMapper).updateById(order);
    }

    @Test
    void updateOrder_changeDateAndStatus() {
        Order order = new Order().setId(9).setOrderDate(today).setOrderStatus("未到诊");
        when(orderMapper.selectById(9)).thenReturn(order);

        LocalDate newDate = today.plusDays(3);
        orderService.updateOrder(9, newDate, "已到诊");

        assertEquals(newDate, order.getOrderDate());
        assertEquals("已到诊", order.getOrderStatus());
        verify(orderMapper).updateById(order);
    }

    // ---------- 每日排班名额 / 防超卖 ----------

    @Test
    void submitOrder_settingFull_rejected() {
        stubSetmeal(1);
        when(memberService.getOne(any())).thenReturn(null);
        Ordersetting full = new Ordersetting().setOrderDate(today).setNumber(1).setReservations(1);
        when(ordersettingService.getByDate(today)).thenReturn(full);

        BizException ex = assertThrows(BizException.class,
                () -> orderService.submitOrder("张三", "男", "13800138000",
                        "110101199001011234", 1, today));
        assertEquals("当日预约名额已满，请选择其他日期", ex.getMessage());
        verify(ordersettingService, never()).tryReserve(any());
        verify(orderMapper, never()).insert(any(Order.class));
    }

    @Test
    void submitOrder_settingAvailable_consumesReservation() {
        stubSetmeal(1);
        when(memberService.getOne(any())).thenReturn(null);
        Ordersetting available = new Ordersetting().setOrderDate(today).setNumber(10).setReservations(5);
        when(ordersettingService.getByDate(today)).thenReturn(available);
        when(ordersettingService.tryReserve(today)).thenReturn(true);

        Order order = orderService.submitOrder("张三", "男", "13800138000",
                "110101199001011234", 1, today);

        assertNotNull(order);
        // 有排班且有余量 → 必须占号并落单
        verify(ordersettingService).tryReserve(today);
        verify(orderMapper).insert(any(Order.class));
    }

    @Test
    void submitOrder_settingRaceLost_rejected() {
        stubSetmeal(1);
        when(memberService.getOne(any())).thenReturn(null);
        Ordersetting nearFull = new Ordersetting().setOrderDate(today).setNumber(10).setReservations(9);
        when(ordersettingService.getByDate(today)).thenReturn(nearFull);
        // 并发下最后的号被抢走 → tryReserve 失败
        when(ordersettingService.tryReserve(today)).thenReturn(false);

        BizException ex = assertThrows(BizException.class,
                () -> orderService.submitOrder("张三", "男", "13800138000",
                        "110101199001011234", 1, today));
        assertEquals("当日预约名额已被抢完，请选择其他日期", ex.getMessage());
        verify(orderMapper, never()).insert(any(Order.class));
    }

    @Test
    void cancelOrder_releasesReservationOfThatDate() {
        Order order = new Order().setId(9).setOrderStatus("未到诊").setOrderDate(today);
        when(orderMapper.selectById(9)).thenReturn(order);

        orderService.cancelOrder(9);

        assertEquals("已取消", order.getOrderStatus());
        verify(orderMapper).updateById(order);
        verify(ordersettingService).releaseReserve(today);
    }
}
