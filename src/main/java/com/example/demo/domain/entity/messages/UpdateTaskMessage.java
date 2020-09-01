package com.example.demo.domain.entity.messages;

import com.example.demo.domain.entity.Task;
import com.example.demo.domain.repository.TaskRepository;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.Period;
import java.util.Date;

public class UpdateTaskMessage extends TaskMessage {
    private Long databaseId;
    private Date actionDate;
    private Date nextRemindDate;
    private String remindPeriod;

    public UpdateTaskMessage(String message) {
        // \\u 1 -a 20200824-14:49
        String[] commands = message.split(" ");
        String databaseId = message.split(" ")[1];
        try {
            this.databaseId = Long.valueOf(databaseId);
        } catch (Exception e) {

        }
        for (int i = 2; i < commands.length; ) {
            if (!isCommand(commands[i])) {
                i++;
                continue;
            }
            Field field = Field.getField(commands[i++].substring(1));
            StringBuilder text = new StringBuilder();
            do {
                text.append(" ").append(commands[i++]);
            } while (i < commands.length && !isCommand(commands[i]));

            setField(field, text.toString().trim());
        }

    }

    private boolean isCommand(String s) {
        return s.startsWith("-");
    }

    private void setField(Field field, String text) {
        switch (field) {
            case ACTION_DATE: {
                this.actionDate = parseDatetime(text);
            }
            break;
            case REMIND_PERIOD: {
                this.remindPeriod = text;
            }
            break;
            case NEXT_REMIND_DATE: {
                this.nextRemindDate = parseDatetime(text);
            }
            case MESSAGE: {
                this.message = text;
            }
            break;
        }
    }

    @Override
    public Task proceedTask(Update update, TaskRepository taskRepository) {
        Task task;
        if (databaseId != null) {
            task = taskRepository.findById(databaseId).orElseThrow(EntityNotFoundException::new);
        } else {
            task = getTaskFromReply(update, taskRepository);
        }
        if (this.actionDate != null) {
            task.setActionDate(actionDate);
        }
        if (remindPeriod != null) {
            Duration previousPeriod = Duration.parse(task.getRemindPeriod());
            task.setRemindPeriod(this.remindPeriod);

            Duration next = Duration.parse(task.getRemindPeriod());
            Date nextRemindDate = Date.from(task.getNextRemindDate().toInstant().minus(previousPeriod).plus(next));
            task.setNextRemindDate(nextRemindDate);
        }
        if (this.message != null) {
            task.setClientMessage(this.message);
        }

        Task t = taskRepository.save(task);
        return t;
    }

    @Override
    public String formatMessage(Task task, Update update) {
        return String.format("Task: %s  Updated:", task.toString());
    }

    private Period parsePeriod(String s) {
        return Period.parse(s);
    }

    @Getter
    private enum Field {
        ACTION_DATE("a"), NEXT_REMIND_DATE("n"), REMIND_PERIOD("r"), MESSAGE("m");
        private String command;

        Field(String command) {
            this.command = command;
        }

        public static Field getField(String command) {
            for (Field f : Field.values()) {
                if (f.getCommand().equalsIgnoreCase(command)) {
                    return f;
                }
            }
            throw new RuntimeException("Field doesn't exists:" + command);
        }
    }

}
