package com.example.demo.domain.entity.messages;

import com.example.demo.domain.entity.Task;
import com.example.demo.domain.repository.TaskRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public abstract class TaskMessage {
    protected String message;

    public abstract Task proceedTask(Update update, TaskRepository taskRepository);

    protected Date parseDatetime(String s) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-hh:mm");

        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public abstract String formatMessage(Task task, Update update);

    protected Task getTaskFromReply(Update update, TaskRepository taskRepository) {
        Message message = update.getMessage();

        Message replayed = message.getReplyToMessage();
        int ind = replayed.getText().indexOf(")");
        if (ind <= 0) {
            throw new RuntimeException("unsuitable reply");
        }
        Long messageId = Long.valueOf(replayed.getText().substring(0, ind));
        Optional<Task> optionalTask = taskRepository.findByDatabaseId(messageId);
        Task task = optionalTask.orElseThrow(() -> new EntityNotFoundException("Task doesn't exist"));
        return task;
    }
}
