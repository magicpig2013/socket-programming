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

    private String inputLine = "";
    private Integer wordLength = 0;
    private Integer numGuess = 0;


    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());
    }

    public void sendMessage(String msg) throws IOException {
        out.writeInt(msg.length());
        out.writeUTF(msg);
    }

    public void readMessage() throws IOException {
        flag = in.readInt();
        System.out.println(flag);
        if (flag != 0) {
            inputLine = in.readUTF();
            System.out.println("Server: " + inputLine);
        } else {
            wordLength = in.readInt();
            numGuess = in.readInt();
            inputLine = in.readUTF();
            System.out.println("wordlength: " + wordLength);
            System.out.println("numGuess: " + numGuess);
            System.out.println("Server: " + inputLine.substring(0,wordLength));
        }
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    static public void main(String[] args) throws IOException, InterruptedException {
        MyClient client = new MyClient();
        client.startConnection(IP_ADDR, PORT);
        while (client.flag != -1) {
            client.readMessage();
            String myanswer = new BufferedReader(new InputStreamReader(System.in)).readLine();
            client.sendMessage(myanswer);
        }
        // Thread.sleep(30000);
        // response = client.sendMessage(".");
        // System.out.println("Response: " + response);
    }
}