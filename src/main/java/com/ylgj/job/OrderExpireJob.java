package com.ylgj.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylgj.pojo.Order;
import com.ylgj.service.OrderService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 体检订单过期定时任务
 * <p>
 * 每天凌晨 1 点执行：将"预约日期已过且仍未到诊"的订单自动更新为"已过期"状态，
 * 释放预约名额，保证数据准确性。
 */
@Component
public class OrderExpireJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(OrderExpireJob.class);

    /** 未到诊状态 */
    private static final String STATUS_NOT_VISITED = "未到诊";
    /** 已过期状态 */
    private static final String STATUS_EXPIRED = "已过期";

    @Autowired
    private OrderService orderService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LocalDate today = LocalDate.now();
        log.info("【定时任务】开始扫描过期体检订单，日期：{}", today);

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.lt("orderDate", today)
                .eq("orderStatus", STATUS_NOT_VISITED);
        List<Order> expiredOrders = orderService.list(wrapper);

        int count = 0;
        for (Order order : expiredOrders) {
            order.setOrderStatus(STATUS_EXPIRED);
            orderService.updateById(order);
            count++;
        }
        log.info("【定时任务】过期订单扫描完成，共更新 {} 条", count);
    }
}
