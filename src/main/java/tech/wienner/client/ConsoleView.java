package tech.wienner.client;

import tech.wienner.common.Message;

public class ConsoleView implements View {
    @Override
    public void display(Message message) {
        System.out.println(message.toString());
    }
}