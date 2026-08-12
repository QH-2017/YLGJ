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
@TableName("t_ordersetting")
public class Ordersetting extends Model<Ordersetting> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 约预日期
     */
    @TableField("orderDate")
    private LocalDate orderDate;

    /**
     * 可预约人数
     */
    @TableField("number")
    private Integer number;

    /**
     * 已预约人数
     */
    @TableField("reservations")
    private Integer reservations;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
