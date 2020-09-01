package com.example.demo.domain.telegramm;

import com.example.demo.domain.Commands;
import com.example.demo.domain.entity.Task;
import com.example.demo.domain.entity.messages.TaskMessage;
import com.example.demo.domain.entity.messages.TaskMessageFactory;
import com.example.demo.domain.repository.TaskRepository;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class PollingBot extends TelegramLongPollingBot {
    private static Map<String, Commands> commands;
    private TaskRepository taskRepository;

    static {
        commands = new HashMap<>();
        commands.put("\\t", Commands.CREATE_TASK);
        commands.put("\\create", Commands.CREATE_TASK);
        commands.put("\\c", Commands.TASK_COMLETE);
        commands.put("\\complete", Commands.TASK_COMLETE);
        commands.put("complete", Commands.TASK_COMLETE);
        commands.put("update", Commands.UPDATE_REMIND_DATE);
        commands.put("\\u", Commands.UPDATE_REMIND_DATE);


    }

    public PollingBot(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        try {
            if (update.hasMessage() && message.hasText()) {
                String commandOperand = text.split(" ")[0];
                Commands command = commands.get(commandOperand);
                if (command != null) {

                    TaskMessage taskMessage = TaskMessageFactory.getTaskMessage(command, text);
                    Task t = taskMessage.proceedTask(update, taskRepository);
                    String taskResponse = taskMessage.formatMessage(t, update);
                    sendMessage(t.getChatId().toString(), taskResponse);
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            sendMessage(message.getChatId().toString(), "Something wrong:" + e.getLocalizedMessage());
        }
    }


    public void sendTask(Task task) {
        String message = task.getClientMessage();
        message += "\n Next remind: " + task.getNextRemindDate();
        sendMessage(task.getChatId().toString(), task.getDatabaseId() + ") " + message);
    }

    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage().setChatId(chatId).setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "Reminder_something_to_me_Bot";
    }

    public String getBotToken() {
        return "1266737624:AAG0I0TRKtwg_GJQIUXxzJf7lQl81czz0CM";

    }

}
