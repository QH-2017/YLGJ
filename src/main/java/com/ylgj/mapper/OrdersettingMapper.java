package com.ylgj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylgj.pojo.Ordersetting;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;

/**
 * 预约条件（每日可预约排班）Mapper。
 *
 * @author lixin
 * @since 2026-07-04
 */
public interface OrdersettingMapper extends BaseMapper<Ordersetting> {

    /**
     * 原子累加某日已预约人数（条件更新防超卖）：
     * 仅当累加后仍不超过可预约人数 number 时才更新成功。
     *
     * @return 影响行数：1=占号成功；0=名额已满或该日无排班
     */
    @Update("UPDATE t_ordersetting SET reservations = COALESCE(reservations,0) + #{delta} " +
            "WHERE orderDate = #{date} AND COALESCE(reservations,0) + #{delta} <= COALESCE(number,0)")
    int incrReservations(@Param("date") LocalDate date, @Param("delta") int delta);

    /**
     * 释放某日预约名额（取消 / 改期回退，下限保护不为负）。
     *
     * @return 影响行数
     */
    @Update("UPDATE t_ordersetting SET reservations = reservations - 1 " +
            "WHERE orderDate = #{date} AND COALESCE(reservations,0) > 0")
    int decrReservations(@Param("date") LocalDate date);
}
