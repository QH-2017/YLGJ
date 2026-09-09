package com.ylgj.service;

import com.ylgj.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;

/**
 * 订单服务：沉淀移动端预约下单、改期、取消等核心业务逻辑，
 * 事务边界统一在实现类内管理，Controller 只做参数解析与响应组装。
 *
 * @author lixin
 * @since 2026-07-04
 */
public interface OrderService extends IService<Order> {

    /**
     * 提交预约（自动按手机号查找或创建会员 + 创建订单）。
     *
     * @throws com.ylgj.commons.BizException 参数不合法 / 套餐不存在 / 订单已满等业务失败
     */
    Order submitOrder(String name, String sex, String telephone, String idCard,
                      Integer setmealId, LocalDate orderDate);

    /**
     * 修改预约（改期 / 改状态）。
     *
     * @throws com.ylgj.commons.BizException 订单不存在
     */
    Order updateOrder(Integer orderId, LocalDate orderDate, String orderStatus);

    /**
     * 取消预约。
     *
     * @throws com.ylgj.commons.BizException 订单不存在
     */
    Order cancelOrder(Integer orderId);
}
