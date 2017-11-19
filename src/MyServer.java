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


        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void sendMsg(String message) throws IOException{
            int msgLen = message.length();
            char flag = (char)msgLen;
            String send = flag + message;
            out.writeUTF(send);
        }

        public void readMsg() throws IOException {
            String readResult = in.readUTF();
            msg =  Character.getNumericValue(readResult.charAt(0));
            inputLine = readResult.substring(1);
            System.out.println("read msg:" + msg);
        }

        public void sendResult(String result) throws IOException {
            String send = "";
            send += "~";
            send += answer.length();
            send += incorrectGuess.length();
            out.writeUTF(send + result + incorrectGuess);
        }

        public void generateResult(char letter) throws IOException {
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
                if (msg == 0) {
                    return true;
                }
            }
        }

        public void gameEnd() throws IOException {
            sendMsg("Game Over!");
            in.close();
            out.close();
            clientSocket.close();
        }

        public void gameHold() throws IOException {
            while (incorrectGuess.length() < 6) {
                readMsg();
                char letter = inputLine.charAt(0);
                if (msg != 1 || !((letter >= 'a' && letter <='z') || (letter >= 'A' && letter <='Z'))) {
                    sendMsg("Error! Please guess a letter.");
                } else {
                    boolean win = true;
                    generateResult(letter);
                    if (incorrectGuess.length() >= 6) {
                        sendMsg("You Lose :(");
                        break;
                    }
                    for (int i = 0; i < result.length(); i++) {
                        if (result.charAt(i) == '_') {
                            win = false;
                            break;
                        }
                    }
                    if (win) {
                        sendMsg("You Win!");
                        break;
                    } else {
                        sendResult(result);
                    }

                }
            }
        }

        public void run() {
            try{
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                // Ask for game start
                if (gameStart()){
                    sendMsg("Letter to guess:");
                    gameHold();
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
