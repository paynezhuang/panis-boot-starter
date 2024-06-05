package com.izpan.quartz.service;

import com.izpan.quartz.domain.SchedulerSetup;
import org.quartz.*;

import java.util.List;

/**
 * Job Scheduler Service 接口类
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.quartz.service.ISchedulerService
 * @CreateTime 2024/5/17 - 23:55
 */

public interface ISchedulerService {


    /**
     * 设置调度器
     *
     * @param scheduler 调度器
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-25 - 09:05:10
     */
    void setScheduler(Scheduler scheduler);

    /**
     * 添加定时任务
     *
     * @param setup 调度任务设置对象
     * @return {@linkplain Boolean} 是否添加成功
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:40:32
     */
    boolean add(SchedulerSetup setup);

    /**
     * 更新定时任务
     *
     * @param setup 调度任务设置对象
     * @return boolean
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-22 - 02:56:56
     */
    boolean update(SchedulerSetup setup);

    /**
     * 暂停任务
     *
     * @param jobKey 任务对象
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:41:04
     */
    void pause(JobKey jobKey);

    /**
     * 暂停任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组名称
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:41:04
     */
    void pause(String jobName, String jobGroup);

    /**
     * 按组暂停任务
     *
     * @param groupName 组名称
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:41:20
     */
    void pauseGroup(String groupName);

    /**
     * 恢复任务
     *
     * @param jobKey 任务对象
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:41:33
     */
    void resume(JobKey jobKey);

    /**
     * 恢复任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组名称
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:47:41
     */
    void resume(String jobName, String jobGroup);

    /**
     * 按组恢复任务
     *
     * @param groupName 组名称
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:41:39
     */
    void resumeGroup(String groupName);

    /**
     * 删除任务
     *
     * @param jobKey 任务对象
     * @return {@linkplain Boolean} 是否删除成功
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:41:50
     */
    boolean delete(JobKey jobKey, TriggerKey triggerKey);

    /**
     * 删除任务
     *
     * @param jobName      任务名称
     * @param jobGroup     任务组名称
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器组别
     * @return {@linkplain Boolean} 是否删除成功
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:49:07
     */
    boolean delete(String jobName, String jobGroup, String triggerName, String triggerGroup);

    /**
     * 立即执行任务
     *
     * @param jobKey 任务对象
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-23 - 10:44:51
     */
    void immediate(JobKey jobKey);

    /**
     * 立即执行任务
     *
     * @param jobName  作业名称
     * @param jobGroup 作业组别
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-23 - 10:45:03
     */
    void immediate(String jobName, String jobGroup);

    /**
     * 暂停触发器
     *
     * @param triggerKey 触发器 Key
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:42:01
     */
    void pauseTrigger(TriggerKey triggerKey);

    /**
     * 暂停触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器组别
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:49:31
     */
    void pauseTrigger(String triggerName, String triggerGroup);

    /**
     * 按组暂停触发器
     *
     * @param groupName 触发器组别
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:42:09
     */
    void pauseTriggerGroup(String groupName);

    /**
     * 恢复触发器
     *
     * @param triggerKey 触发器 Key
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:42:17
     */
    void resumeTrigger(TriggerKey triggerKey);

    /**
     * 恢复触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器组别
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:50:24
     */
    void resumeTrigger(String triggerName, String triggerGroup);

    /**
     * 按组恢复触发器
     *
     * @param groupName 组名称
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 04:42:25
     */
    void resumeTriggerGroup(String groupName);

    /**
     * 取消触发器
     *
     * @param triggerKey 触发器 Key
     * @return {@linkplain Boolean} 是否删除成功
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 05:14:58
     */
    boolean unscheduleJob(TriggerKey triggerKey);

    /**
     * 取消触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器组别
     * @return boolean
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-20 - 05:36:03
     */
    boolean unscheduleJob(String triggerName, String triggerGroup);

    /**
     * 批量取消触发器
     *
     * @param triggerKeys 触发器集合
     * @return {@linkplain Boolean} 是否取消成功
     * @CreateTime 2024-05-20 - 05:15:05
     */
    boolean unscheduleJobBatch(List<TriggerKey> triggerKeys);

    /**
     * 检查触发器状态
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器组别
     * @return boolean
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-21 - 12:14:48
     */
    boolean checkState(String triggerName, String triggerGroup);

    /**
     * 检查触发器状态
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器组别
     * @param state        {@linkplain Trigger.TriggerState } 状态
     * @return boolean
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-21 - 12:14:48
     */
    boolean checkState(String triggerName, String triggerGroup, Trigger.TriggerState state);

    /**
     * 检查触发器状态
     *
     * @param triggerKey 触发器 Key
     * @return boolean
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-21 - 12:15:19
     */
    boolean checkState(TriggerKey triggerKey);

    /**
     * 检查触发器状态
     *
     * @param triggerKey 触发器 Key
     * @param state      {@linkplain Trigger.TriggerState } 状态
     * @return boolean
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-21 - 12:15:19
     */
    boolean checkState(TriggerKey triggerKey, Trigger.TriggerState state);

    /**
     * 检查触发器组状态
     *
     * @param triggerGroup 触发器组别
     * @return {@linkplain Boolean} 是否全部暂停成功
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-21 - 12:28:35
     */
    boolean checkStateGroup(String triggerGroup);

    /**
     * 检查触发器组状态
     *
     * @param triggerGroup 触发器组别
     * @param state        {@linkplain Trigger.TriggerState } 状态
     * @return {@linkplain Boolean} 是否全部暂停成功
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-21 - 12:28:35
     */
    boolean checkStateGroup(String triggerGroup, Trigger.TriggerState state);
}
