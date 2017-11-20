import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;


public class MyClient {
    public static final String IP_ADDR = "127.0.0.1";
    public static final int PORT = 6666;
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;

    private Integer flag = 0;
    private boolean connectionEnd = false;

    private String inputLine = "";
    private Integer wordLength = 0;
    private Integer numGuess = 0;


    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());
    }

    public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg.length()+ msg);
    }

    public void readMessage() throws IOException {
        String readMsg = in.readUTF();
        if (readMsg.charAt(0) == '~') {
            flag = 0;
        } else {
            flag = (int)readMsg.charAt(0);
        }
        System.out.println("flag:" + flag);
        if (flag != 0) {
            inputLine = readMsg.substring(1);
            System.out.println("Server: " + inputLine);
            if (flag == 8 || flag ==9) {
                connectionEnd = true;
            }
        } else {
            wordLength = Character.getNumericValue(readMsg.charAt(1));
            numGuess = Character.getNumericValue(readMsg.charAt(2));
            inputLine = readMsg.substring(3);
            System.out.println("wordlength: " + wordLength);
            System.out.println("numGuess: " + numGuess);
            System.out.println("Server: " + inputLine.substring(0,wordLength));
            System.out.println("Incorrect guess: " + inputLine.substring(wordLength));
            readMessage();
        }
    }

    public void stopConnection() throws IOException {
        System.out.println("Now is in the end");
        in.close();
        out.close();
        clientSocket.close();
    }

    static public void main(String[] args) throws IOException, InterruptedException {
        MyClient client = new MyClient();
        client.startConnection(IP_ADDR, PORT);
        client.out.writeUTF("0");
        while (!client.connectionEnd) {
            client.readMessage();
            if (client.connectionEnd) {
                break;
            }
            String myanswer = new BufferedReader(new InputStreamReader(System.in)).readLine();
            client.sendMessage(myanswer);
        }
        client.stopConnection();
        // Thread.sleep(30000);
        // response = client.sendMessage(".");
        // System.out.println("Response: " + response);
    }
}