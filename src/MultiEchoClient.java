import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiEchoClient {
    private static InetAddress host;
    private static final int PORT = 1235;

    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException uhEx) {
            System.out.println("\nHost ID not found!\n");
            System.exit(1);
        }
        sendMessages();

        /*

        startHeartBeat();

        */
    }

/*
    private static synchronized void startHeartBeat(){
        try{
            System.out.println("\ntest");
            Socket socket = new Socket(InetAddress.getByName("192.168.0.14"), 1238);
            System.out.println(socket.getInetAddress().getHostAddress()+socket.getPort());
            Timer t = new Timer();

            t.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    PrintWriter heartbeat = null;

                    try {
                        heartbeat = new PrintWriter(socket.getOutputStream(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert heartbeat != null;

                    heartbeat.println("IMAV");
                }
            }, 0, 60000);
        }catch (Exception e){
            System.out.println(""+e);
        }
    }
*/
int i = 0;

    private static void sendMessages() {
        Socket socket = null;
        Boolean accepted = false;

        try {
            String message = "", respons;
            String username = "";
            do {
                if (username.equals("")) {
                    System.out.println("Please enter username (Username is max 12 chars long, only " + "letters, digits, ‘-‘ and ‘_’ allowed.\n");
                    Scanner username_input = new Scanner(System.in);
                    String username_string = username_input.next();

                    socket = new Socket(InetAddress.getByName("192.168.0.14"), PORT);
                    if (username_string.length() <= 12) {
                        System.out.println(username_string + " is bellow 12 characters");
                        if (Pattern.matches("[a-zA-Z0-9]+", username_string)) {

                            System.out.println(username_string + " is allowed!");
                            username = username_string;
                            String ip = socket.getLocalAddress().toString();
                            String port = Integer.toString(socket.getLocalPort());
                            PrintWriter usernameOutput = new PrintWriter(socket.getOutputStream(), true);
                            String joinOutput = "JOIN "+username+", "+ip+":"+port;
                            usernameOutput.println(joinOutput);

                            Scanner j_OK = new Scanner(socket.getInputStream());
                            String joinInput = j_OK.next();
                            if(joinInput.equals("J_OK")){
                               accepted = true;
                            }
                            if(joinInput.matches("J_ER (.*):(.*)")){
                                accepted = false;
                            }
                        }
                        else{
                            System.out.println(username_string + " is not allowed!");
                        }
                    }
                } else {
                    if(accepted){

                        System.out.println("Type message and press enter to send (Max 255 characters)");

                    Scanner userEntry = new Scanner(System.in);

                    Scanner messageInput = new Scanner(socket.getInputStream());
                    PrintWriter messageOutput = new PrintWriter(socket.getOutputStream(), true);

                    String userInput = userEntry.nextLine();
                    if(userInput.length()<=255) {
                        message = "DATA " + username + ": " + userInput;
                        System.out.println(message);

                        if (message.matches("DATA (.*): (.*)")) {
                            messageOutput.println(message);

                            respons = messageInput.nextLine();
                            if (respons.matches("DATA (.*): (.*)")) {
                                System.out.println("\nSERVER> " + respons);
                            }
                        }
                    }
                    else{
                        System.out.println("Message To Long");
                    }
                    }
                    else {
                        System.out.println("Client Not Accepted To Server");
                        message = "QUIT";
                        sendMessages();
                    }
                }

            }
            while (!message.equals("QUIT")) ;
        }
        catch(IOException ioEx)
        {
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
