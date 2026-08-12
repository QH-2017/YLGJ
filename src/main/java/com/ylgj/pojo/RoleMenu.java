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
@TableName("t_role_menu")
public class RoleMenu extends Model<RoleMenu> {

    private static final long serialVersionUID = 1L;

    private Integer roleId;

    private Integer menuId;

    @Override
    public Serializable pkVal() {
        return this.menuId;
    }
}
