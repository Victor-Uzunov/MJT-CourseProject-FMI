package bg.sofia.uni.fmi.mjt.splitwise.client;

import bg.sofia.uni.fmi.mjt.splitwise.exception.logger.ExceptionLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientNio {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 4096;
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    public static void main(String[] args) {
        boolean isRunning = true;
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            System.out.println("Connected to the server.");
            System.out.println("If you want to see all commands enter HELP");

            while (isRunning) {
                isRunning = handleInput(socketChannel, scanner);
            }
        } catch (IOException e) {
            ExceptionLogger.logException(e);
            System.out.println("Unable to connect to the server! Try again later.");
        }
    }

    private static boolean handleInput(SocketChannel socketChannel, Scanner scanner) throws IOException {
        System.out.print("Enter command:\n");
        String message = scanner.nextLine();

        if (message.equalsIgnoreCase("quit")) {
            System.out.println("Disconnecting from the server...");
            System.out.println("Disconnected from the server.");
            return false;
        } else {
            System.out.println("Sending message <" + message + "> to the server...");
            sendMessage(socketChannel, message);
            receiveAndDisplayReply(socketChannel);
            return true;
        }
    }

    private static void sendMessage(SocketChannel socketChannel, String message) throws IOException {
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
    }

    private static void receiveAndDisplayReply(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        socketChannel.read(buffer);
        buffer.flip();
        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        String reply = new String(byteArray, StandardCharsets.UTF_8);
        System.out.println("The server replied:\n" + reply);
    }
}
