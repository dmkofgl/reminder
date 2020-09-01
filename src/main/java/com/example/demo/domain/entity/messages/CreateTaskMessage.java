package com.example.demo.domain.entity.messages;

import com.example.demo.domain.entity.Task;
import com.example.demo.domain.repository.TaskRepository;
import lombok.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTaskMessage extends TaskMessage {

    private Date actionDate = new Date();
    private Date nextRemindDate = new Date();
    private String remindPeriod = "PT1M";

    public CreateTaskMessage(String message) {
        // \\u 1 -a 20200824-14:49
        String[] commands = message.split(" ");

        for (int i = 1; i < commands.length; ) {
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
        Task task = new Task();
        Message message = update.getMessage();

        task.setChatId(message.getChatId());
        task.setActionDate(this.actionDate);
        task.setRemindPeriod(this.remindPeriod);
        task.setNextRemindDate(this.nextRemindDate);
        task.setClientMessage(this.message);

        return taskRepository.save(task);

    }

    @Override
    public String formatMessage(Task task, Update update) {
        if (task == null) {
            throw new RuntimeException("try to format null task");
        }
        return String.format("Принято: \n %s", task.toString());
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
