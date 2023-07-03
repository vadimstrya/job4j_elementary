package ru.job4j.pooh;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ProducerClient {
    public static void main(String[] args) throws Exception {
        var socket = new Socket("127.0.0.1", 9000);
        var input = new Scanner(System.in);
        try (var out = new PrintWriter(socket.getOutputStream(), true)) {
            while (true) {
                var text = input.nextLine();
                out.println(text);
            }
        }
    }
}
