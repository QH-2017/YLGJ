package com.ylgj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ylgj.mapper.OrdersettingMapper;
import com.ylgj.pojo.Ordersetting;
import com.ylgj.service.OrdersettingService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 预约条件服务实现。
 */
@Service
public class OrdersettingServiceImpl extends ServiceImpl<OrdersettingMapper, Ordersetting>
        implements OrdersettingService {

    @Override
    public Ordersetting getByDate(LocalDate orderDate) {
        return getOne(new QueryWrapper<Ordersetting>().eq("orderDate", orderDate));
    }

    @Override
    public boolean tryReserve(LocalDate orderDate) {
        // 条件更新：reservations + 1 <= number 才成功，原子防超卖
        return getBaseMapper().incrReservations(orderDate, 1) > 0;
    }

    @Override
    public void releaseReserve(LocalDate orderDate) {
        getBaseMapper().decrReservations(orderDate);
    }
}
