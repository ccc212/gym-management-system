package com.gymsys.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public enum CompetitionStatusEnum {

    NOT_STARTED(0,"未开始"),

    SIGN_UP_DEADLINE(1,"报名已截止"),

    IN_PROGRESS(2,"正在进行"),

    ENDED(3,"已结束");

    private final Integer code;

    private final String desc;

    /**
     * 根据当前时间返回状态
     */
    public static Integer getStatusByTime(LocalDateTime signUpDeadline, LocalDateTime startTime, LocalDateTime endTime) {
        if (LocalDateTime.now().isBefore(signUpDeadline)) {
            return CompetitionStatusEnum.NOT_STARTED.getCode();
        }
        else if (LocalDateTime.now().isBefore(startTime)) {
            return CompetitionStatusEnum.SIGN_UP_DEADLINE.getCode();
        }
        else if (LocalDateTime.now().isBefore(endTime)) {
            return CompetitionStatusEnum.IN_PROGRESS.getCode();
        }
        return CompetitionStatusEnum.ENDED.getCode();
    }

}
