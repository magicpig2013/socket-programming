import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {
    public static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Starting...\n");
        TestServer server = new TestServer();
        server.init();
    }

    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket client = serverSocket.accept();
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private class HandlerThread implements Runnable {
        private Socket socket;
        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        public void run() {
            try {
                // get input from client
                DataInputStream input = new DataInputStream(socket.getInputStream());
                String clientInputStr = input.readUTF();
                System.out.println("Client:" + clientInputStr);
                // produce output
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                // System.out.print("Enter what you eant to send:\t");
                // String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
                String s = clientInputStr.toUpperCase();
                out.writeUTF(s);
                out.close();
                input.close();
            } catch (Exception e) {
                System.out.println("Server run error: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        System.out.println("Server finally error:" + e.getMessage());
                    }
                }
            }
        }
    }
}