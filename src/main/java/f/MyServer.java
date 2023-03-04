package f;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final int PORT = 8189;

    private List<ClientHandler> clients;


    public MyServer() {
        try(ServerSocket server = new ServerSocket(PORT)){
            clients = new ArrayList<>();
            while (true){
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился: "+socket.getInetAddress());
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка в работе сервера");
        }
    }

    public synchronized void broadcastMsg(String msg){
        for (ClientHandler client : clients) {
            client.sendMsg(msg);
        }
    }

    public synchronized void unsubscribe(ClientHandler client){
        clients.remove(client);
    }

    public synchronized void subscribe(ClientHandler client){
        clients.add(client);
    }
}