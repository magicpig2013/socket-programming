import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;



public class TestClient {
    public static final String IP_ADDR = "127.0.0.1";
    public static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Client starting...");
        System.out.println("When client receives string \"exit\", client stops\n");
        while (true) {
            Socket socket = null;
            try {
                socket = new Socket(IP_ADDR, PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                System.out.print("Enter your string: \t");
                String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
                out.writeUTF(str);

                String ret = input.readUTF();
                System.out.println("The result is : " + ret);
                if ("EXIT".equals(ret)) {
                    System.out.println("Client close");
                    Thread.sleep(500);
                    break;
                }

                out.close();
                input.close();
            } catch (Exception e) {
                System.out.println("Client run error:" + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        socket = null;
                        System.out.println("Client finally error:" + e.getMessage());
                    }
                }
            }
        }
    }

}
