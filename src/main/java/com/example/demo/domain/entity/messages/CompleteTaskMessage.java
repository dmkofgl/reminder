package com.example.demo.domain.entity.messages;

import com.example.demo.domain.entity.Task;
import com.example.demo.domain.repository.TaskRepository;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.EntityNotFoundException;

public class CompleteTaskMessage extends TaskMessage {
    private Long databaseId;

    public CompleteTaskMessage(String message) {
        String[] commands = message.split(" ");
        String databaseId = message.split(" ")[1];
        try {
            this.databaseId = Long.valueOf(databaseId);
        } catch (Exception e) {

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
        task.setCompleted(true);
        return taskRepository.save(task);
    }

    @Override
    public String formatMessage(Task task, Update update) {
        if (task.isCompleted()) {
            return String.format("Task: %d competed.", task.getDatabaseId());
        }
        return String.format("Task: %d  not competed.", task.getDatabaseId());
    }
}
