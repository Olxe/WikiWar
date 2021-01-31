import tcp.TCP_server;

class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();

        TCP_server tcp_server = new TCP_server();
        tcp_server.run();
    }
}