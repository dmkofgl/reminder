package com.example.demo.domain.entity.messages;

import com.example.demo.domain.Commands;

public class TaskMessageFactory {
    public static TaskMessage getTaskMessage(Commands commands, String message) {

        switch (commands) {
            case CREATE_TASK: {
                return new CreateTaskMessage(message);
            }
            case UPDATE_REMIND_DATE: {
                return new UpdateTaskMessage(message);
            }
            case TASK_COMLETE: {
                return new CompleteTaskMessage(message);
            }
            case GET_INFO: {
                return new CreateTaskMessage(message);
            }
        }
        throw new RuntimeException("There is no suit command");
    }
}
