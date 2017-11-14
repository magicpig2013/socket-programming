import java.io.*;
import java.net.Socket;

public class TicTacClientX {
    public static final String IP_ADDR = "127.0.0.1";
    public static final int PORT = 13579;
    private Socket socket;
    DataInputStream input;
    DataOutputStream out;

    public TicTacClientX() throws Exception{
        socket = new Socket(IP_ADDR, PORT);
        input = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void play() throws Exception{
        String s;
        while (true) {
            while (input.available() > 0) {
                s = input.readUTF();
                System.out.println(s);
                if (s.equals("MESSAGE Your move")) {
                    String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
                    out.writeUTF(str);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        System.out.println("Client starting...");
        System.out.println("When client receives string \"exit\", client stops\n");
        while (true) {
            TicTacClientX client = new TicTacClientX();
            client.play();
            break;
        }
    }
}
