package tcp;

import util.Callable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCP_server implements Callable {
    private final static int port = 5000;
    private boolean running = true;
    private ArrayList<TCP_client> clients = new ArrayList<>();

    public void run() {
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server TCP running");

            while(this.running) {
                Socket socket = server.accept();
                System.out.println("New client connected");

                TCP_client client = new TCP_client(socket, this);
                this.clients.add(client);
                client.start();


                this.onNewPlayer();

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("CONNECTED");
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.running = false;
        }
    }

    @Override
    public synchronized void onStart() {
        System.out.println("start event");
        for(TCP_client client : clients) {
            client.send("START");
        }
    }

    private void onNewPlayer() {
        System.out.println("new player event");

        for(TCP_client client : clients) {
            client.send("PLAYER");
        }
    }
}
