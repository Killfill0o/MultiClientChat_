import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

            if(joinInput.matches("JOIN(.*),(.*):(.*)")) {

                    System.out.println("\n Protocol check @ JOIN <USERNAME>, <IP>:<PORT> | "+joinInput.matches("JOIN(.*),(.*):(.*)")+"\n");
                    String[] parts1 = joinInput.split("(?=,)", 2);
                    String part1 = parts1[0].substring(5);
                    String part2 = parts1[1].substring(2);
                    System.out.println("\n Username: " + part1);
                    String[] parts2 = part2.split("(?=:)", 5);
                    String part3 = parts2[0].substring(1);
                    System.out.println(" IP: " + part3);
                    String part4 = parts2[1].substring(1);
                    System.out.println(" PORT: " + part4 + "\n");

                    ClientObject clientObject = new ClientObject();
                    clientObject.setUsername(part1);
                    clientObject.setIp(part3);
                    clientObject.setPort(part4);
                    clientObject.setI(0);
                    clientList.add(clientObject);

                    if (clientList.size() == 1) {
                        PrintWriter j_OK = new PrintWriter(client.getOutputStream(), true);
                        j_OK.println("J_OK");
                    }
                    for (int i = 0; i < clientList.size() - 1; ) {
                        if (part1.equals(clientList.get(i).getUsername())) {
                            PrintWriter j_ER = new PrintWriter(client.getOutputStream(), true);
                            j_ER.println("J_ER DUN: Duplicate Username");
                            clientList.remove(clientList.size() - 1);
                            break;
                        } else {
                            i++;
                        }
                        if (i == clientList.size() - 1) {
                            PrintWriter j_OK = new PrintWriter(client.getOutputStream(), true);
                            j_OK.println("J_OK");
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

class ClientHandler extends Thread {
    private Socket client;
    private Scanner input;
    private PrintWriter output;

    public ClientHandler(Socket socket) throws IOException {
        client = socket;
        System.out.println("SERVER || ClientHandler");
        try {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException ioEx) {
            ioEx.printStackTrace();
        }

    }

    public void run() {
        String recieved;

        System.out.println("SERVER || run");

        do {
            recieved = input.nextLine();

            if (recieved.matches("DATA (.*): (.*)")||recieved.equals("IMAV")) {
                if(recieved.equals("IMAV")){
                    System.out.println(client.getInetAddress().toString()+":"+client.getPort()+" is alive!");
                    System.out.println("\n Protocol check @ IMAV | "+recieved.matches("IMAV")+"\n");
                }else{
                String[] parts1 = recieved.split("(?=:)", 2);
                String part1 = parts1[0].substring(5);
                String part2 = parts1[1].substring(2);
                output.println("DATA "+part1+": "+part2);
                System.out.println("SERVER > "+part1+": "+part2);
                }
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