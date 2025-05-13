package com.chenjiajin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


@Data
@ToString
@TableName(value = "logtest")
public class Logtest implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;

    private String signatureName;

    private String methodName;

    private String args;


}