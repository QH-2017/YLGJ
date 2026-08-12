package com.ylgj.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
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
@TableName("t_setmeal_checkgroup")
public class SetmealCheckgroup extends Model<SetmealCheckgroup> {

    private static final long serialVersionUID = 1L;

    private Integer setmealId;

    private Integer checkgroupId;

    @Override
    public Serializable pkVal() {
        return this.checkgroupId;
    }
}
