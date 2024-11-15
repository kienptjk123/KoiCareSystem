package com.swpproject.koi_care_system.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String address;
    private String phone;
    private String recipientName;
    private String note;
    private String status;
    private List<OrderItemDto> items;
}