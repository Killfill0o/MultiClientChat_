import java.net.SocketAddress;

public class ClientObject {
    public String getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(String serverSocket) {
        this.serverSocket = serverSocket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    private String serverSocket;
        private String username;
        private int i;


}
