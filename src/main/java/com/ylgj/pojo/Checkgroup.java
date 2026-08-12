package com.ylgj.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.util.List;
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
@TableName("t_checkgroup")
public class Checkgroup extends Model<Checkgroup> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("code")
    private String code;

    @TableField("name")
    private String name;

    @TableField("help_code")
    private String helpCode;

    @TableField("sex")
    private String sex;

    @TableField("remark")
    private String remark;

    @TableField("attention")
    private String attention;

    /** 前端传入的检查项ID列表 */
    @TableField(exist = false)
    private List<Integer> checkitemIds;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
