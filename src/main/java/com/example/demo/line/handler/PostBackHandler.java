package com.example.demo.line.handler;

import com.example.demo.line.entity.Event;

import java.util.Optional;

public class PostBackHandler implements  EventHandler{

    @Override
    public void handle(Optional<Event> event) {
        System.out.println("data : " + event.get().getPostBack().getData());
        System.out.println("userId : " + event.get().getSource().getUserId());
    }
}
