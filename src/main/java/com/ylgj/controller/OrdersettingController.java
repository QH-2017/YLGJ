package com.ylgj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylgj.commons.MessageConstant;
import com.ylgj.commons.Result;
import com.ylgj.pojo.Ordersetting;
import com.ylgj.service.OrdersettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ordersetting")
public class OrdersettingController {

    @Autowired
    private OrdersettingService ordersettingService;

    @GetMapping("/findAll")
    public Result findAll() {
        List<Ordersetting> list = ordersettingService.list();
        return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS, list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Ordersetting ordersetting) {
        try {
            // 先查询该日期是否已存在
            QueryWrapper<Ordersetting> wrapper = new QueryWrapper<>();
            wrapper.eq("orderDate", ordersetting.getOrderDate());
            Ordersetting existing = ordersettingService.getOne(wrapper);
            if (existing != null) {
                // 已存在则更新可预约人数
                existing.setNumber(ordersetting.getNumber());
                ordersettingService.updateById(existing);
            } else {
                // 不存在则新增
                if (ordersetting.getReservations() == null) {
                    ordersetting.setReservations(0);
                }
                ordersettingService.save(ordersetting);
            }
            return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);
        }
    }

    @PostMapping("/editNumber")
    public Result editNumber(@RequestBody Ordersetting ordersetting) {
        try {
            ordersettingService.updateById(ordersetting);
            return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);
        }
    }
}
