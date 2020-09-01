package com.example.demo;

import com.example.demo.domain.entity.Task;
import com.example.demo.domain.repository.TaskRepository;
import com.example.demo.domain.telegramm.PollingBot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandReminder {

    private TaskRepository taskRepository;
    private PollingBot bot;

    public CommandReminder(TaskRepository taskRepository, PollingBot bot) {
        this.taskRepository = taskRepository;
        this.bot = bot;
    }

    public void run() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(() -> {
            List<Task> tasks = taskRepository.findByCompleted(false);
            for (int i = 0; i < tasks.size(); i++) {
                Task t = tasks.get(i);
                boolean isSent = false;
                if (isRemindDatetime(t)) {
                    Duration period = Duration.parse(t.getRemindPeriod());
                    t.setNextRemindDate(Date.from(t.getNextRemindDate().toInstant().plus(period)));
                    taskRepository.save(t);
                    bot.sendTask(t);
                    isSent = true;
                }
                if (isActionDatetime(t) && !isSent) {
                    bot.sendTask(t);
                    isSent = true;
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private boolean isRemindDatetime(Task task) {
        Long diff = getDifferenceMinutes(task.getNextRemindDate());
        Long minutesBeforeRemind = -1L;
        return diff > minutesBeforeRemind;
    }

    private boolean isActionDatetime(Task task) {
        Long minutesBeforeRemind = 0L;
        Long diff = getDifferenceMinutes(task.getActionDate());
        return diff.equals(minutesBeforeRemind);
    }

    private Long getDifferenceMinutes(Date date) {
        LocalDateTime now = LocalDateTime.now();
        Temporal temporal = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Long diff = ChronoUnit.MINUTES.between(temporal, now);

        return diff;
    }
}
