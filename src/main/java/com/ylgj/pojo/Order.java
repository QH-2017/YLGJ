package com.ylgj.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lixin
 * @since 2026-07-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_order")
public class Order extends Model<Order> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 员会id
     */
    @TableField("member_id")
    private Integer memberId;

    /**
     * 约预日期
     */
    @TableField("orderDate")
    private LocalDate orderDate;

    /**
     * 约预类型 电话预约/微信预约
     */
    @TableField("orderType")
    private String orderType;

    /**
     * 预约状态（是否到诊）
     */
    @TableField("orderStatus")
    private String orderStatus;

    /**
     * 餐套id
     */
    @TableField("setmeal_id")
    private Integer setmealId;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
