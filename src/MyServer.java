import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class MyServer {
    public static final int PORT = 6666;
    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true)
            new ClientHandler(serverSocket.accept()).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private DataInputStream in;
        private DataOutputStream out;

        private String answer = "abcd";
        private String result = "____";

        private Integer msg = 0;
        private String inputLine = "";
        private String incorrectGuess = "";

        private String msg1 = "Ready to start game? (y/n):";
        private String msg2 = "Plesae input y or n";
        private String msg3 = "You Win!";
        private String msg4 = "See you!";
        private String msg5 = "Letter to guess:";
        private String msg6 = "Error! Please guess a letter.";

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void sendMsg(String message) throws IOException{
            out.writeInt(message.length());
            out.writeUTF(message);
        }

        public void readMsg() throws IOException {
            msg = in.readInt();
            inputLine = in.readUTF();
        }

        public void sendResult(String result) throws IOException {
            out.writeInt(0);
            out.writeInt(answer.length());
            out.writeInt(incorrectGuess.length());
            out.writeUTF(result + incorrectGuess);
        }

        public void generateResult(Character letter) throws IOException {
            boolean find = false;
            String current = "";
            for (int i = 0; i < answer.length(); i++) {
                if (result.charAt(i) != '_') {
                    current += result.charAt(i);
                } else if (letter == answer.charAt(i)) {
                    current += answer.charAt(i);
                    find = true;
                } else {
                    current += '_';
                }
            }
            if (!find) {
                incorrectGuess += letter;
            } else {
                result = current;
            }
            System.out.println(result);
        }

        public boolean gameStart() throws IOException {
            while (true) {
                readMsg();
                System.out.println(msg);
                System.out.println(inputLine);
                if (inputLine.equals("y") || inputLine.equals("Y")) {
                    return true;
                } else if (inputLine.equals("n") || inputLine.equals("N")) {
                    return false;
                } else {
                    sendMsg(msg2);
                }
            }
        }

        public void gameEnd() throws IOException {
            sendMsg(msg4);
            in.close();
            out.close();
            clientSocket.close();
        }

        public void gameHold() throws IOException {
            while (incorrectGuess.length() < 6) {
                readMsg();
                if (msg != 1) {
                    sendMsg(msg6);
                } else {
                    generateResult(inputLine.charAt(0));
                    sendResult(result);
                }
            }

        }

        public void run() {
            try{
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                // Ask for game start
                sendMsg(msg1);
                if (!gameStart()) {
                    gameEnd();
                } else {
                    sendMsg(msg5);
                    gameHold();
                    sendMsg(msg3);
                    gameEnd();
                }
            }  catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws IOException {
        MyServer server=new MyServer();
        server.start(PORT);
        System.out.println("Starting...");
    }


}
