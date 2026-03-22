package com.repair.dto;

import lombok.Data;

@Data
public class CreateRepairRequest {
    private Long studentId;
    private String building;
    private String roomNo;
    private String deviceType;
    private String description;
    private Integer priority;
}
