package f;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика для клиента");
        }
    }

    private void readMessages() throws IOException {
        while (true) {
            String s = in.readUTF();
            System.out.println("From " + name + ": " + s);
            if (s.equals("/end")) {
                return;
            }
            myServer.broadcastMsg(name + ": " + s + "\n");
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authentication() throws IOException {
        AuthService authService = myServer.getAuthService();
        while (true) {
            String s = in.readUTF();
            if (s.startsWith("/auth")) {
                String[] parts = s.split("\\s");
                String login = parts[1];
                String password = parts[2];
                if (authService.authenticate(login, password)) {
                    String nickname = authService.getNickname(login);
                    if (myServer.isNickBusy(nickname)) {
                        sendMsg("Данный ник занят\n");
                        continue;
                    }
                    this.name = nickname;
                    myServer.subscribe(this);
                    return;
                }
            }
        }

    }

    private void closeConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + "вышел из чата");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}