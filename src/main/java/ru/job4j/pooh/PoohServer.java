package ru.job4j.pooh;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PoohServer {
    private final QueueSchema queueSchema = new QueueSchema();
    private final TopicSchema topicSchema = new TopicSchema();

    private void runSchemas() {
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(queueSchema);
    }

    private void runServer() {
        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(9000)) {
            System.out.println("Pooh is ready ...");
            while (!server.isClosed()) {
                Socket socket = server.accept();
                pool.execute(() -> {
                    try (OutputStream out = socket.getOutputStream();
                         var input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        while (true) {
                            var details = input.readLine().split(";");
                            var action = details[0];
                            var name = details[1];
                            var text = details[2];
                            if (action.startsWith("intro")) {
                                if (name.equals("consumer")) {
                                    queueSchema.addReceiver(
                                            new SocketReceiver(text, new PrintWriter(out))
                                    );
                                }
                            }
                            if (action.startsWith("message")) {
                                queueSchema.publish(new Message(name, text));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        var pooh = new PoohServer();
        pooh.runSchemas();
        pooh.runServer();
    }
}