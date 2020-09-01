package com.example.demo;

import com.example.demo.domain.repository.TaskRepository;
import com.example.demo.domain.telegramm.PollingBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DemoApplication {
    @Autowired
    private TaskRepository taskRepository;

    public static void main(String[] args) {
        ApiContextInitializer.init();

        SpringApplication.run(DemoApplication.class, args);

    }

    @PostConstruct
    public void init() {

        PollingBot bot = new PollingBot(taskRepository);
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            botapi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        CommandReminder commandReminder = new CommandReminder(taskRepository, bot);
        commandReminder.run();

    }

}
