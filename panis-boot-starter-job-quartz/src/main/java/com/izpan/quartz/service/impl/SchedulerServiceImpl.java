package com.izpan.quartz.service.impl;

import com.izpan.quartz.domain.SchedulerSetup;
import com.izpan.quartz.exception.SchedulerServiceException;
import com.izpan.quartz.service.ISchedulerService;
import org.apache.commons.lang3.ObjectUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

/**
 * Job Scheduler Service 实现类
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.quartz.service.impl.SchedulerServiceImpl
 * @CreateTime 2024/5/17 - 23:56
 */
@Service
public class SchedulerServiceImpl implements ISchedulerService {

    private Scheduler scheduler;

    @Override
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public boolean add(SchedulerSetup setup) {
        try {
            JobDetail jobDetail = buildJobDetail(setup);
            CronTrigger cronTrigger = buildCronTrigger(setup);
            // 调度作业
            scheduler.scheduleJob(jobDetail, cronTrigger);
            scheduler.start();
            return scheduler.isStarted();
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to add job '%s' in group '%s'".formatted(setup.getJobName(), setup.getJobGroup()), e);
        }
    }

    @Override
    public boolean update(SchedulerSetup setup) {
        try {
            JobDetail oldJobDetail = scheduler.getJobDetail(new JobKey(setup.getJobName(), setup.getJobGroup()));
            JobBuilder newJobBuilder = oldJobDetail.getJobBuilder()
                    .withDescription(setup.getDescription());
            if (ObjectUtils.isNotEmpty(setup.getJobDataMap())) {
                newJobBuilder.setJobData(setup.getJobDataMap());
            }
            TriggerKey triggerKey = new TriggerKey(setup.getTriggerName(), setup.getTriggerGroup());
            CronTrigger cronTrigger = buildCronTrigger(setup);

            // 重启触发器
            scheduler.rescheduleJob(triggerKey, cronTrigger);
            // 添加任务，覆盖原有任务
            scheduler.addJob(newJobBuilder.build(), true);
            scheduler.start();
            return scheduler.isStarted();
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to update job '%s' in group '%s'".formatted(setup.getJobName(), setup.getJobGroup()), e);
        }
    }

    @Override
    public void pause(JobKey jobKey) {
        try {
            // 暂停指定的作业
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to pause job '%s' in group '%s'".formatted(jobKey.getName(), jobKey.getGroup()), e);
        }
    }

    @Override
    public void pause(String jobName, String jobGroup) {
        pause(new JobKey(jobName, jobGroup));
    }

    @Override
    public void pauseGroup(String groupName) {
        try {
            // 暂停指定的作业组
            scheduler.pauseJobs(GroupMatcher.jobGroupEquals(groupName));
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to pause job in group '%s'".formatted(groupName), e);
        }
    }

    @Override
    public void resume(JobKey jobKey) {
        try {
            // 恢复被暂停的作业
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to resume job '%s' in group '%s'".formatted(jobKey.getName(), jobKey.getGroup()), e);
        }
    }

    @Override
    public void resume(String jobName, String jobGroup) {
        resume(new JobKey(jobName, jobGroup));
    }

    @Override
    public void resumeGroup(String groupName) {
        try {
            // 恢复被暂停的作业组
            scheduler.resumeJobs(GroupMatcher.jobGroupEquals(groupName));
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to resume job in group '%s'".formatted(groupName), e);
        }
    }

    @Override
    public boolean delete(JobKey jobKey, TriggerKey triggerKey) {
        try {
            // 安全删除任务: 1. 暂停触发器 2.取消触发器 3.删除作业
            this.pauseTrigger(triggerKey);
            this.unscheduleJob(triggerKey);
            return scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to delete job '%s' in group '%s'".formatted(jobKey.getName(), jobKey.getGroup()), e);
        }
    }

    @Override
    public boolean delete(String jobName, String jobGroup, String triggerName, String triggerGroup) {
        return delete(new JobKey(jobName, jobGroup), new TriggerKey(triggerName, triggerGroup));
    }

    @Override
    public void immediate(JobKey jobKey) {
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to immediate job '%s' in group '%s'".formatted(jobKey.getName(), jobKey.getGroup()), e);
        }
    }

    @Override
    public void immediate(String jobName, String jobGroup) {
        immediate(new JobKey(jobName, jobGroup));
    }


    @Override
    public void pauseTrigger(TriggerKey triggerKey) {
        try {
            // 暂停指定的触发器
            scheduler.pauseTrigger(triggerKey);
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to pause trigger '%s' in group '%s'".formatted(triggerKey.getName(), triggerKey.getGroup()), e);
        }
    }

    @Override
    public void pauseTrigger(String triggerName, String triggerGroup) {
        pauseTrigger(new TriggerKey(triggerName, triggerGroup));
    }

    @Override
    public void pauseTriggerGroup(String groupName) {
        try {
            // 暂停指定的触发器组
            scheduler.pauseTriggers(GroupMatcher.triggerGroupEquals(groupName));
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to pause trigger in group '%s'".formatted(groupName), e);
        }
    }

    @Override
    public void resumeTrigger(TriggerKey triggerKey) {
        try {
            // 恢复指定的触发器
            scheduler.resumeTrigger(triggerKey);
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to resume trigger '%s' in group '%s'".formatted(triggerKey.getName(), triggerKey.getGroup()), e);
        }
    }

    @Override
    public void resumeTrigger(String triggerName, String triggerGroup) {
        resumeTrigger(new TriggerKey(triggerName, triggerGroup));
    }

    @Override
    public void resumeTriggerGroup(String groupName) {
        try {
            // 恢复指定的触发器组
            scheduler.resumeTriggers(GroupMatcher.triggerGroupEquals(groupName));
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to resume trigger in group '%s'".formatted(groupName), e);
        }
    }

    @Override
    public boolean unscheduleJob(TriggerKey triggerKey) {
        try {
            // 取消指定的作业调度
            return scheduler.unscheduleJob(triggerKey);
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to unschedule job '%s' in group '%s'".formatted(triggerKey.getName(), triggerKey.getGroup()), e);
        }
    }


    @Override
    public boolean unscheduleJob(String triggerName, String triggerGroup) {
        return unscheduleJob(new TriggerKey(triggerName, triggerGroup));
    }

    @Override
    public boolean unscheduleJobBatch(List<TriggerKey> triggerKeys) {
        try {
            // 批量取消指定的作业调度
            return scheduler.unscheduleJobs(triggerKeys);
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to unschedule job '%s'".formatted(triggerKeys), e);
        }
    }

    @Override
    public boolean checkState(String triggerName, String triggerGroup) {
        return checkState(new TriggerKey(triggerName, triggerGroup));
    }

    @Override
    public boolean checkState(String triggerName, String triggerGroup, Trigger.TriggerState state) {
        return checkState(new TriggerKey(triggerName, triggerGroup), state);
    }

    @Override
    public boolean checkState(TriggerKey triggerKey) {
        return checkState(triggerKey, Trigger.TriggerState.NORMAL);
    }

    @Override
    public boolean checkState(TriggerKey triggerKey, Trigger.TriggerState state) {
        try {
            // 获取触发器状态
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            return state == triggerState;
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to check trigger state '%s' in group '%s'".formatted(triggerKey.getName(), triggerKey.getGroup()), e);
        }
    }

    @Override
    public boolean checkStateGroup(String triggerGroup) {
        return checkStateGroup(triggerGroup, Trigger.TriggerState.NORMAL);
    }

    @Override
    public boolean checkStateGroup(String triggerGroup, Trigger.TriggerState state) {
        try {
            // 获取指定组中所有触发器的键
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));
            return triggerKeys.stream()
                    .allMatch(triggerKey -> {
                        try {
                            return scheduler.getTriggerState(triggerKey) == state;
                        } catch (SchedulerException e) {
                            return false;
                        }
                    });
        } catch (SchedulerException e) {
            throw new SchedulerServiceException("Failed to check trigger state in group '%s'".formatted(triggerGroup), e);
        }
    }


    /**
     * 构建作业详细信息
     *
     * @param setup {@linkplain SchedulerSetup} 作业设置
     * @return {@link JobDetail }
     * @author payne.zhuang <payne.zhuang@gmail.com>
     * @CreateTime 2024-05-22 - 03:38:42
     */
    private JobDetail buildJobDetail(SchedulerSetup setup) {
        try {
            // 加载并实例化指定的作业类
            Class<?> className = Class.forName(setup.getJobClassName());
            QuartzJobBean jobBean = (QuartzJobBean) className.getDeclaredConstructor().newInstance();
            // 创建作业详细信息
            JobBuilder jobBuilder = JobBuilder.newJob(jobBean.getClass())
                    .withIdentity(setup.getJobName(), setup.getJobGroup())
                    .storeDurably()
                    .withDescription(setup.getDescription());
            if (ObjectUtils.isNotEmpty(setup.getJobDataMap())) {
                jobBuilder.setJobData(setup.getJobDataMap());
            }
            return jobBuilder.build();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new SchedulerServiceException("Failed to build job detail for job '%s' in group '%s'".formatted(setup.getJobName(), setup.getJobGroup()), e);
        }
    }

    /**
     * 构建 Cron 触发器
     *
     * @param setup {@linkplain SchedulerSetup} 作业设置
     * @return {@link CronTrigger }
     * @author payne.zhuang <payne.zhuang@gmail.com>
     * @CreateTime 2024-05-22 - 03:39:08
     */
    private CronTrigger buildCronTrigger(SchedulerSetup setup) {
        // 创建并配置一个新的Cron触发器
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                .withIdentity(setup.getTriggerName(), setup.getTriggerGroup())
                .startNow()
                .withDescription(setup.getTriggerDescription())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .repeatForever()
                )
                .build();
        TriggerBuilder<CronTrigger> cronTriggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(setup.getTriggerName(), setup.getTriggerGroup())
                .startNow()
                .withDescription(setup.getTriggerDescription())
                .withSchedule(CronScheduleBuilder
                        .cronSchedule(setup.getCronExpression().trim())
                        // 服务恢复后，将直接把nextFireTime更新至当前时间之后的下一次的执行时间，那么之前漏掉的任务将会被抛弃掉。适用于大部分场景。
                        .withMisfireHandlingInstructionDoNothing()
                );
        if (ObjectUtils.isNotEmpty(setup.getTriggerDataMap())) {
            cronTriggerBuilder.usingJobData(setup.getTriggerDataMap());
        }
        return cronTriggerBuilder.build();
    }
}