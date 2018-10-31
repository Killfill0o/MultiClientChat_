import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;

public class MultiEchoClient {
    private static final int PORT = 1235;

    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket(InetAddress.getByName("192.168.0.14"), PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Heartbeat t1 = new Heartbeat("T1",socket);
        t1.start();

        /*RecievedMessage R1 = new RecievedMessage( "Thread-1");*/
        SendMessage S1 = new SendMessage("Thread-2",socket);
        /*R1.start();*/
        S1.run();
    }
}

class SendMessage implements Runnable {
        private Thread t;
        private String threadName;
        private Socket socket;
        private static final int PORT = 1235;

        SendMessage(String name, Socket s) {
            threadName = name;
            System.out.println("Creating " +  threadName );
            socket = s;
        }

        public void run() {
        boolean accepted = false;

        try {
            String message = "", respons;
            String username = "";

            do {
                if (username.equals("")) {
                    System.out.println("Please enter username (Username is max 12 chars long, only " + "letters, digits, ‘-‘ and ‘_’ allowed.\n");
                    Scanner username_input = new Scanner(System.in);
                    String username_string = username_input.next();

                    if (username_string.length() <= 12) {
                        System.out.println(username_string + " is bellow 12 characters");
                        if (Pattern.matches("[a-zA-Z0-9]+", username_string)) {

                            System.out.println(username_string + " is allowed!");
                            username = username_string;
                            String ip = socket.getLocalAddress().toString();
                            String port = Integer.toString(socket.getLocalPort());
                            PrintWriter usernameOutput = new PrintWriter(socket.getOutputStream(), true);
                            String joinOutput = "JOIN " + username + ", " + ip + ":" + port;
                            usernameOutput.println(joinOutput);

                            Scanner j_OK = new Scanner(socket.getInputStream());
                            String joinInput = j_OK.next();
                            if (joinInput.equals("J_OK")) {
                                accepted = true;
                            }
                            if (joinInput.matches("J_ER (.*):(.*)")) {
                                accepted = false;
                            }
                        } else {
                            System.out.println(username_string + " is not allowed!");
                        }
                    }
                } else {
                    if (accepted) {
                        System.out.println("Type message and press enter to send (Max 255 characters)");

                        Scanner userEntry = new Scanner(System.in);

                        Scanner messageInput = new Scanner(socket.getInputStream());
                        PrintWriter messageOutput = new PrintWriter(socket.getOutputStream(), true);

                        String userInput = userEntry.nextLine();
                        if (userInput.length() <= 255) {
                            message = "DATA " + username + ": " + userInput;
                            System.out.println(message);

                            if (message.matches("DATA (.*): (.*)")) {

                                messageOutput.println(message);

                                respons = messageInput.nextLine();
                                if (respons.matches("DATA (.*): (.*)")) {
                                    System.out.println("\nSERVER> " + respons);
                                }
                            }
                        } else {
                            System.out.println("Message To Long");
                        }
                    } else {
                        System.out.println("Client Not Accepted To Server");
                        message = "QUIT";
                    }
                }

            }
            while (!message.equals("QUIT"));
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            try {
                System.out.println("\nClosing connection...");
                socket.close();
            } catch (IOException ioEx) {
                System.out.println("Unable to disconnect!");

                System.exit(1);
            }
        }
    }
}
/*

class RecievedMessage implements Runnable {
    private Thread t;
    private String threadName;
    private static final int PORT = 1235;

    RecievedMessage(String name) {
        threadName = name;
        System.out.println("Creating " +  threadName );
    }

    public void run() {
        System.out.println("Running " +  threadName );
        String message = "", respons;
        try {
            Socket socket = new Socket(InetAddress.getByName("192.168.0.14"), PORT);
            do {
                    Scanner messageInput = new Scanner(socket.getInputStream());
                    respons = messageInput.nextLine();
                    if (respons.matches("DATA (.*): (.*)")) {
                        System.out.println("\nSERVER> " + respons);
                    }
            }
            while (!message.equals("QUIT"));
                System.out.println("Thread: " + threadName + ", ");
                // Let the thread sleep for a while.
                Thread.sleep(50);
            } catch (InterruptedException | IOException e1) {
            e1.printStackTrace();
        }
    }

    public void start () {
        System.out.println("Starting " +  threadName );
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }
}

*/


class Heartbeat extends Thread{
    String tname;
    Socket socket;
    String message;

    Heartbeat(String string, Socket s){
        tname = string;
        socket  = s;
    }

    public void run() {
        try {
            System.out.println(tname+" is running!");
            for(int i = 0; i < 31;){
                Thread.sleep(1000);
                if(i == 30){
                    /*System.out.println(tname+" is alive!");*/
                    PrintWriter imavOutput = new PrintWriter(socket.getOutputStream(), true);
                    message = "IMAV";
                    imavOutput.println(message);
                    i = 0;
                }
                i++;
            }
        }catch (Exception e){
        }
    }
}