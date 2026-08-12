package com.ylgj.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("t_checkgroup_checkitem")
public class CheckgroupCheckitem extends Model<CheckgroupCheckitem> {

    private static final long serialVersionUID = 1L;

    @TableField("checkgroup_id")
    private Integer checkgroupId;

    @TableField("checkitem_id")
    private Integer checkitemId;

    @Override
    public Serializable pkVal() {
        return this.checkitemId;
    }
}
