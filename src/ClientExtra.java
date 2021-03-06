import java.io.EOFException;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ClientExtra {
    private DataOutputStream output;//sending message to server
    private DataInputStream input;// receiving message from server
    private String serverIP;
    private static int portNumber;
    private Socket connection;
    private String message = "";
    private boolean connectionEnd = false;


    public ClientExtra(String host) {
        serverIP = host;
    }

    public static void main(String args[]) {
        String s = args[0];//sever IP
        portNumber = Integer.parseInt(args[1]);
        ClientExtra client = new ClientExtra(s);//local host
        client.startRunning();
    }

    public void startRunning() {
        try {
            connectToServer();
            whileChatting();
        } catch(EOFException eofException) {
            System.out.println("\n Client terminated connection");
        } catch(IOException ioException) {
            ioException.printStackTrace();
        } finally {
            closeCrap();//close socket
        }
    }

    private void connectToServer() throws IOException {
        connection = new Socket(InetAddress.getByName(serverIP), portNumber);// port # make socket
        input = new DataInputStream(connection.getInputStream());
        output = new DataOutputStream(connection.getOutputStream());
        // System.out.println("Connected to: " + connection.getInetAddress().getHostName());
    }

    //while chatting with server
    private void whileChatting() throws IOException {
        //ask user to start the game
        Scanner userInput = new Scanner(System.in);
        while (!connectionEnd) {
            readMessage();
            if (connectionEnd) {
                break;
            }
            String guessLetter = userInput.nextLine();
            guessLetter = guessLetter.toLowerCase();
            sendMessage(guessLetter.length() + guessLetter);
        }
    }

    private void readMessage() throws IOException{
        if (connectionEnd) {
            return;
        }
        message = input.readUTF();
        int realMsgFlag= (int)message.charAt(0);
        if(realMsgFlag == 126) {//msgFlag == 0
            int wordLength = Integer.parseInt(message.substring(1, 2));
            int errorCount = Integer.parseInt(message.substring(2, 3));
            String guessingResult = message.substring(3, (3 + wordLength));
            String errorTracking = message.substring(3 + wordLength);
            String currentanswer = "";
            for (int i = 0; i < wordLength; i++) {
                if (i != wordLength) {
                    currentanswer += guessingResult.charAt(i);
                    currentanswer += " ";
                } else {
                    currentanswer += guessingResult.charAt(i);
                }
            }
            System.out.println(currentanswer);
            System.out.println("Incorrect Guesses:" + errorTracking + "\n");
            readMessage();
        } else {
            String data = message.substring(1, realMsgFlag + 1);
            System.out.println(data);
            if (realMsgFlag == 8 || realMsgFlag == 11) {// lose or win
                connectionEnd = true;
                readMessage();
            } else if (realMsgFlag == 17) { // letter to guess
            } else if (realMsgFlag == 46) {// overload
                connectionEnd = true;
            } else {
                readMessage();
            }
        }
    }

    //send message to server
    private void sendMessage(String message) {
        try {
            output.writeUTF(message);
        } catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }

    //close the streams and sockets
    private void closeCrap() {
        // System.out.println("\n Close");
        try{
            output.close();
            input.close();
            connection.close();
        } catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
