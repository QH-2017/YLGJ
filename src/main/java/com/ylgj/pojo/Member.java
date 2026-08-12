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
@TableName("t_member")
public class Member extends Model<Member> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("fileNumber")
    private String fileNumber;

    @TableField("name")
    private String name;

    @TableField("sex")
    private String sex;

    @TableField("idCard")
    private String idCard;

    @TableField("phoneNumber")
    private String phoneNumber;

    @TableField("regTime")
    private LocalDate regTime;

    @TableField("password")
    private String password;

    @TableField("email")
    private String email;

    @TableField("birthday")
    private LocalDate birthday;

    @TableField("remark")
    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
