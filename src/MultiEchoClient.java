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

    private static void sendMessages() {
        Socket socket = null;

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
                            String ip = socket.getInetAddress().toString();
                            String port = Integer.toString(socket.getLocalPort());
                            PrintWriter usernameOutput = new PrintWriter(socket.getOutputStream(), true);
                            String joinOutput = "JOIN "+username+", "+ip+":"+port;
                            usernameOutput.println(joinOutput);
                        }
                        else{
                            System.out.println(username_string + " is not allowed!");
                        }
                    }else
                        {System.out.println(username_string + " is above 12 characters");}
                } else {
                    Scanner userEntry = new Scanner(System.in);

                    Scanner networkInput = new Scanner(socket.getInputStream());
                    PrintWriter networkOutput = new PrintWriter(socket.getOutputStream(), true);

                    System.out.println("Enter message ('QUIT' to exit): ");
                    message = userEntry.nextLine();

                    networkOutput.println(message);

                    respons = networkInput.nextLine();

                        System.out.println("\nSERVER> " + respons);
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
