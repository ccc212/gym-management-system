package com.gymsys.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingStatusEnum {
    PENDING(0, "预约中"),
    SUCCESS(1, "预约成功"),
    FAILED(2, "预约失败");

    private final Integer code;

    private final String desc;
}
