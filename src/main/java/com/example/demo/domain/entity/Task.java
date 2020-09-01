package com.example.demo.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long databaseId;
    private Long chatId;
    private String clientMessage="";
    private int messageDate;
    @Column(columnDefinition = "boolean default false")
    private boolean completed=false;
    private Date actionDate=new Date();
    private Date nextRemindDate=new Date();
    private String remindPeriod="PT1M";

    @Override
    public String toString() {
        return "Task{" +
                "databaseId=" + databaseId +
                ", message='" + clientMessage + '\'' +
                ", messageDate=" + messageDate +
                ", actionDate=" + actionDate +
                ", nextRemindDate=" + nextRemindDate +
                ", remindPeriod='" + remindPeriod + '\'' +
                '}';
    }
}
