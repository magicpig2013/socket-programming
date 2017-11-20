import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;


public class MyServer {
    private ServerSocket serverSocket;

    public void start(int port, String[] dist) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true)
            new ClientHandler(serverSocket.accept(), dist).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private String[] dist;
        private DataInputStream in;
        private DataOutputStream out;

        private String answer = "";
        private String result = "";

        private Integer msg = 0;
        private String inputLine = "";
        private String incorrectGuess = "";


        public ClientHandler(Socket socket, String[] dist) {
            this.clientSocket = socket;
            this.dist = dist;
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

        public void gameInit() throws IOException {
            int randomNum = ThreadLocalRandom.current().nextInt(0, dist.length);
            answer = dist[randomNum];
            for (int i = 0; i < answer.length(); i++) {
                result += '_';
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
                sendMsg("Letter to guess:");
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
                    gameInit();
                    gameHold();
                    gameEnd();
                }
            }  catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String[] dist;
        int port = 2017;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
            dist = new String[15];
            dist[0] = "you";
            dist[1] = "see";
            dist[2] = "sun";
            dist[3] = "tree";
            dist[4] = "wind";
            dist[5] = "love";
            dist[6] = "water";
            dist[7] = "trade";
            dist[8] = "fever";
            dist[9] = "struct";
            dist[10] = "string";
            dist[11] = "object";
            dist[12] = "integer";
            dist[13] = "ethurem";
            dist[14] = "bitcoin";
        } else {
            port = Integer.parseInt(args[0]);
            FileReader fr = new FileReader(args[1]);
            BufferedReader br = new BufferedReader(fr);
            String currentLine = br.readLine();
            String[] parts = currentLine.split(" ");
            int wordLength = Integer.parseInt(parts[0]);
            int wordNumber = Integer.parseInt(parts[1]);
            dist = new String[wordNumber];
            for (int i = 0; i < wordNumber; i++) {
                dist[i] = br.readLine();
            }
        }
        MyServer server=new MyServer();
        server.start(port,dist);
    }

}
