package com.ylgj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ylgj.pojo.Ordersetting;

import java.time.LocalDate;

/**
 * 预约条件（每日可预约排班）服务：支撑下单时的名额校验与并发防超卖。
 */
public interface OrdersettingService extends IService<Ordersetting> {

    /**
     * 查询指定日期的排班（orderDate 唯一，最多一条）。
     */
    Ordersetting getByDate(LocalDate orderDate);

    /**
     * 占号：原子累加该日已预约人数。
     *
     * @return true=占号成功；false=名额已满（或该日无排班记录）
     */
    boolean tryReserve(LocalDate orderDate);

    /**
     * 释放名额：取消 / 改期时回退该日已预约人数。
     */
    void releaseReserve(LocalDate orderDate);
}
