package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.SplitWise;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 4096;
    private static CommandExecutor commandExecutor;

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            initializeServer(serverSocketChannel);
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }

    private static void initializeServer(ServerSocketChannel serverSocketChannel)
            throws IOException {
        commandExecutor = new CommandExecutor(new SplitWise());
        serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        Runtime.getRuntime().addShutdownHook(new Thread(Server::shutdown));

        handleConnections(selector, buffer, commandExecutor);
    }

    private static void handleConnections(Selector selector, ByteBuffer buffer, CommandExecutor commandExecutor)
            throws IOException {
        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isReadable()) {
                    handleReadableKey(key, buffer, commandExecutor);
                } else if (key.isAcceptable()) {
                    handleAcceptableKey(key, selector);
                }
                keyIterator.remove();
            }
        }
    }

    private static void handleReadableKey(SelectionKey key, ByteBuffer buffer, CommandExecutor commandExecutor)
            throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        buffer.clear();
        int r;
        try {
            r = sc.read(buffer);
        } catch (IOException e) {
            System.out.println("Client has closed the connection");
            key.cancel();
            sc.close();
            return;
        }
        if (r < 0) {
            System.out.println("Client has closed the connection");
            key.cancel();
            sc.close();
            return;
        }
        buffer.flip();

        String receivedInformation = new String(buffer.array(), 0, buffer.limit());
        String answer = commandExecutor.execute(sc, CommandCreator.newCommand(receivedInformation));

        buffer.clear();
        buffer.put(answer.getBytes());
        buffer.flip();
        sc.write(buffer);
    }

    private static void handleAcceptableKey(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private static void shutdown() {
        commandExecutor.saveAll();
        System.out.println("Server shutting down. Saving information...");
    }
}
