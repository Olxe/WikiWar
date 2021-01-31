package tcp;

import util.Callable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCP_client extends Thread {
    private final Socket socket;
    private final Callable callable;

    public TCP_client(Socket socket, Callable callable) {
        this.socket = socket;
        this.callable = callable;
    }

    @Override
    public void run() {
        while(true) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = in.readLine();
                System.out.println(message);

                if(message.equals("READY")) {
                    this.callable.onStart();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String message) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
