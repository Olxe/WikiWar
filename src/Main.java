import tcp.TCP_server;

class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();

        TCP_server.getInstance().run();
    }
}