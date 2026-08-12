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
@TableName("t_setmeal")
public class Setmeal extends Model<Setmeal> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("code")
    private String code;

    @TableField("help_code")
    private String helpCode;

    @TableField("sex")
    private String sex;

    @TableField("age")
    private String age;

    @TableField("price")
    private Double price;

    @TableField("remark")
    private String remark;

    @TableField("attention")
    private String attention;

    @TableField("img")
    private String img;

    /** 前端传入的检查组ID列表 */
    @TableField(exist = false)
    private List<Integer> checkgroupIds;

    /** 关联的检查组信息 */
    @TableField(exist = false)
    private List<SetmealCheckgroup> setmealCheckGroups;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
