import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.net.SocketException;


public class MyServer {
    private ServerSocket serverSocket;
    private int numConnect = 0;

    public void start(int port, String[] dist) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            Socket currentSocket = serverSocket.accept();
            numConnect = Thread.activeCount();
            if (numConnect < 4) {
                ClientHandler current = new ClientHandler(currentSocket, dist);
                System.out.println("Get connected from " + "127.0.0.1" + ":" + port);
                current.start();
            } else {
                ClientDup current = new ClientDup(currentSocket, dist);
                current.start();
            }
            System.out.println("Connect Number: " + Thread.activeCount());
        }
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
            // System.out.println("read msg:" + msg);
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
            // System.out.println(result);
        }

        public boolean gameStart() throws IOException {
            try{
                while (true) {
                    readMsg();
                    if (msg == 0) {
                        return true;
                    }
                }
            } catch(SocketException e) {
                gameEnd();
                return false;
            }
        }

        public void gameInit() throws IOException {
            int randomNum = ThreadLocalRandom.current().nextInt(0, dist.length);
            answer = dist[randomNum];
            for (int i = 0; i < answer.length(); i++) {
                result += '_';
            }
            sendResult(result);
        }

        public void gameEnd() throws IOException {
            in.close();
            out.close();
            clientSocket.close();
        }

        public void gameHold() throws IOException {
            while (incorrectGuess.length() < 6) {
                sendMsg("Letter to guess:");
                readMsg();
                if (inputLine.equals("")) {
                    sendMsg("Error! Please guess a letter.");
                    continue;
                }
                char letter = inputLine.charAt(0);
                if (msg != 1 || !((letter >= 'a' && letter <='z') || (letter >= 'A' && letter <='Z'))) {
                    sendMsg("Error! Please guess a letter.");
                } else if (incorrectGuess.indexOf(letter) != -1 || result.indexOf(letter) != -1) {
                    sendMsg("Error! Letter " + letter + "has been guessed before, please guess another letter.");
                } else {
                    boolean win = true;
                    generateResult(letter);
                    if (incorrectGuess.length() >= 6) {
                        sendMsg("You Lose :(");
                        sendMsg("Game Over!");
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
                        sendMsg("Game Over!");
                        break;
                    } else {
                        sendResult(result);
                    }
                }
            }
        }

        public void setStream() {
            try {
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
            } catch (EOFException e) {
                // ... this is fine
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            try{
                setStream();
                if (gameStart()){
                    gameInit();
                    gameHold();
                }
                gameEnd();
            } catch (EOFException e) {
                // ... this is fine
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientDup extends ClientHandler {
        public ClientDup(Socket socket, String[] dist){super(socket,dist);}
        public void run(){
            try{
                setStream();
                // Ask for game start
                if (gameStart()){
                    sendMsg("Server overloaded. Try again after one minute.");
                }
                gameEnd();
            } catch (EOFException e) {
                // ... this is fine
            } catch(IOException e) {
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
