package com.finlink.flow.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 流水分页响应 VO
 *
 * @author 稚名不带撇
 */
@Data
public class FlowPageVO {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<FlowVO> records;
}
