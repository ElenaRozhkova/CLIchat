package com.db.chat;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHelper implements ServerInterface, Runnable {
    private Client client;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerHelper(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(
                    new OutputStreamWriter(
                            new BufferedOutputStream(
                                    socket.getOutputStream())));
            in = new BufferedReader(
                    new InputStreamReader(
                            new BufferedInputStream(
                                    socket.getInputStream())));
        } catch (Exception e) {
            System.out.println("server helper constructor error");
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("close exception");
            e.printStackTrace();
        }
    }

    @Override
    public void receive(Message message) {
        out.println(serializeMessage(message));
        out.flush();
    }

    @Override
    public void send(Message message) {
        switch (message.getType()) {
            case MESSAGE:
                client.receive(message);
                break;
            case HISTORY:
                break;
            case ERROR:
                break;
        }
    }

    @Override
    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        while(!Thread.interrupted()) {
            try {
                String textMessage = in.readLine();
                pool.execute(() -> {
                    send(deserializeMessage(textMessage));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pool.shutdownNow();
        System.out.println("serverhelper finished");
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
