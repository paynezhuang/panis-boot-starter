package com.izpan.quartz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.JobDataMap;

/**
 * 调度任务 作业信息
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.quartz.domain.SchedulerSetup
 * @CreateTime 2024/5/20 - 11:05
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerSetup {

    /**
     * 任务名称(唯一)
     */
    private String jobName;

    /**
     * 任务组别
     */
    private String jobGroup;

    /**
     * 任务类名
     */
    private String jobClassName;

    /**
     * 触发器名称
     */
    private String triggerName;

    /**
     * 触发器组
     */
    private String triggerGroup;

    /**
     * cron表达式
     */
    private String cronExpression;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务参数
     */
    private JobDataMap jobDataMap;

    /**
     * 触发器描述
     */
    private String triggerDescription;

    /**
     * 触发器参数
     */
    private JobDataMap triggerDataMap;

}
