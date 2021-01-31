import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCP_main {
    public static void main(String[] args) {
        //Server -> à placer dans un thread
        try (Socket socket = new Socket("127.0.0.1", 5000)) {
            //Permet d'envoyer un message
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("READY");

            while(true) {
                //Permet de lire un message
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = in.readLine();
                System.out.println(message);
                //si msg == truc -> requete HTTP
            }
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
