package com.repair.dto;

import lombok.Data;

@Data
public class UpdateRepairStatusRequest {
    private Long adminId;
    private Integer status;
    private String remark;
}
