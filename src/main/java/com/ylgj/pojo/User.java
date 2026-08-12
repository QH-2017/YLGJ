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
@TableName("t_user")
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("birthday")
    private LocalDate birthday;

    @TableField("gender")
    private String gender;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("remark")
    private String remark;

    @TableField("station")
    private String station;

    @TableField("telephone")
    private String telephone;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
