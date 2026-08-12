package com.ylgj.service.impl;

import com.ylgj.pojo.Order;
import com.ylgj.mapper.OrderMapper;
import com.ylgj.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lixin
 * @since 2026-07-04
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
