import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MultiEchoServer {
    private static ServerSocket serverSocket;
    private static final int PORT = 1235;
    private static ArrayList<ClientObject> clientList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ioEx) {
            System.out.println("\nUnable to set up port!");
            System.exit(1);
        }
        do {
            System.out.println("SERVER || main");
            Socket client = serverSocket.accept();
            String joinInput = new Scanner(client.getInputStream()).nextLine();
            System.out.println("\n Protocol check @ JOIN <USERNAME>, <IP>:<PORT> | "+joinInput.matches("JOIN(.*),(.*):(.*)")+"\n");

            if(joinInput.matches("JOIN(.*),(.*):(.*)")) {

                String[] parts1 = joinInput.split("(?=,)", 2);
                String part1 = parts1[0].substring(5);
                String part2 = parts1[1].substring(2);
                System.out.println(" Username: "+part1);
                String[] parts2 = part2.split("(?=:)", 5);
                String part3 = parts2[0].substring(1);
                System.out.println(" IP: "+part3);
                String part4 = parts2[1].substring(1);
                System.out.println(" PORT: "+part4);

                System.out.println("\n Client : " + part3);
                System.out.println(" Username : " + part1+"\n");
                ClientObject clientObject = new ClientObject();
                clientObject.setUsername(part1);
                clientObject.setIp(part3);
                clientObject.setPort(part4);
                clientObject.setI(0);
                clientList.add(clientObject);

                System.out.println(clientList.size());
                if(clientList.size() == 1) {
                    PrintWriter j_OK = new PrintWriter(client.getOutputStream(), true);
                    j_OK.println("J_OK");
                    System.out.println("FIRST!");
                }
                for(int i = 0; i < clientList.size()-1;){
                        if (part1.equals(clientList.get(i).getUsername())) {
                            PrintWriter j_ER = new PrintWriter(client.getOutputStream(), true);
                            j_ER.println("J_ER DUN: Duplicate Username");
                            System.out.println("WOOPS!");
                            clientList.remove(clientList.size()-1);
                            break;
                        }
                        else{i++;}
                            if(i == clientList.size()-1){
                            PrintWriter j_OK = new PrintWriter(client.getOutputStream(), true);
                            j_OK.println("J_OK");
                            System.out.println("YAY!");
                        }
                }


                System.out.println("\n New Client Accepted. \n");
                System.out.println("▬▬▬▬▬▬▬▬▬▬▬▬▬ஜ۩۞۩ஜ▬▬▬▬▬▬▬▬▬▬▬▬▬");

                for (int i = 0; i < clientList.size(); ) {
                    System.out.println(i + " " + clientList.get(i).getUsername() + " " + clientList.get(i).getIp() + ":" + clientList.get(i).getPort());
                    i++;
                }
                System.out.println("▬▬▬▬▬▬▬▬▬▬▬▬▬ஜ۩۞۩ஜ▬▬▬▬▬▬▬▬▬▬▬▬▬");
                ClientHandler handler = new ClientHandler(client);
                handler.start();
            } else {
                PrintWriter j_ER = new PrintWriter(client.getOutputStream(), true);
                j_ER.println("J_ER CNA: Client Not Accepted");
                client.close();
            }
        } while (true);
    }
}

class ChatServerThread extends Thread {
    private Socket socket = null;
    private int ID = -1;
    private DataInputStream streamIn = null;

    public ChatServerThread(Socket _socket) {
        socket = _socket;
        ID = socket.getPort();
    }
}

class ClientHandler extends Thread {
    private Socket client;
    private Scanner input;
    private PrintWriter output;

    public ClientHandler(Socket socket) {
        client = socket;
        System.out.println("SERVER || ClientHandler");
        try {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public void run() {
        String recieved;

        System.out.println("SERVER || run");
        do {
            recieved = input.nextLine();
            if (recieved.matches("DATA (.*): (.*)")) {
                String[] parts1 = recieved.split("(?=:)", 2);
                String part1 = parts1[0].substring(5);
                String part2 = parts1[1].substring(2);
                output.println("DATA "+part1+": "+part2);
            }
        }
        while (!recieved.equals("QUIT"));

        try {
            if (client != null) {
                System.out.println("Closing down Connection...");
                client.close();
            }
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect!");
        }
    }

}

/*

class IMAV extends Thread{
    private Socket client;
    private Scanner input;
    private PrintWriter output;
    private int seconds = 60;

   public IMAV(ServerSocket serverSocket, Socket socket) {
        System.out.println("1");
        do {

            System.out.println("2");
            try {
                client = serverSocket.accept();
                input = new Scanner(client.getInputStream());

                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Are you alive?");
                        if(input.next().equals("IMAV")){
                            System.out.println("yes");
                            System.out.println("test"+input.next());
                        }
                    }
                }, 0, 1000);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("3");

        } while (!input.next().equals("QUIT"));


    }
}

*/