package com.izpan.quartz.exception;

import java.io.Serial;

/**
 * Scheduler Service 异常
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.quartz.exception.SchedulerServiceException
 * @CreateTime 2024/5/18 - 01:31
 */

public class SchedulerServiceException extends RuntimeException {
    
    @Serial
    private static final long serialVersionUID = 1785885235585243645L;

    public SchedulerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
