package com.ylgj.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("t_menu")
public class Menu extends Model<Menu> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("linkUrl")
    private String linkUrl;

    @TableField("path")
    private String path;

    @TableField("priority")
    private Integer priority;

    @TableField("icon")
    private String icon;

    @TableField("description")
    private String description;

    @TableField("parentMenuId")
    private Integer parentMenuId;

    @TableField("level")
    private Integer level;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
