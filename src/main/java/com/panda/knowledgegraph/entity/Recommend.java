package com.panda.knowledgegraph.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Recommend {

    private Long id;

    private String recommendName;

    private String userInputStructure;

    private String recommendResult;

    private Integer userInputNodeCount;

    private Integer startId;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
