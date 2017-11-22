import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.net.SocketException;

public class ServerExtra {
    private ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        String[] dist;
        int port;

        //build the dictionary for all the game
        if (args.length == 1) {// hard code the dictionary
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
        } else {// import file to build the dictionary
            port = Integer.parseInt(args[0]);
            FileReader fr = new FileReader("src/"+args[1]);
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

        //start the server
        ServerExtra server=new ServerExtra();
        server.start(port,dist);
    }

    public void start(int port, String[] dist) throws IOException {
        serverSocket = new ServerSocket(port);

        //keep listening the requests
        while (true) {
            HangmanGame game = new HangmanGame(dist);
            HangmanGame.Player playerOne = game.new Player(serverSocket.accept());
            playerOne.start();
            // Check whether the player choose single mode or double mode
            while (true) {
                if (game.playMode ==1 ) {

                    break;
                } else if (game.playMode == 2) {
                    break;
                }
            }
            HangmanGame.Player playerTwo = game.new Player(serverSocket.accept());
            playerOne.setOpponent(playerTwo);
            playerTwo.setOpponent(playerOne);
            game.currentPlayer = playerOne;
            playerOne.start();
            playerTwo.start();
        }
    }

}

public class HangmanGame {
    Player currentPlayer;
    int playMode;
    String[] dist;
    String answer = "";
    String result = "";

    public HangmanGame(String[] dist) {
        this.dist = dist;
        this.playMode = 0;
        int randomNum = ThreadLocalRandom.current().nextInt(0, dist.length);
        this.answer = dist[randomNum];
        for (int i = 0; i < this.answer.length(); i++) {
            this.result += '_';
        }
    }

    public void setPlayMode(int mode) {
        playMode = mode;
    }


    class Player extends Thread {
        private Player opponent = null;
        private Socket playerSocket;
        private DataOutputStream out;
        private DataInputStream in;

        private String answer = "";
        private String result = "";

        private Integer msg = 0;
        private String inputLine = "";
        private String incorrectGuess = "";

        public Player(Socket socket) {
            this.playerSocket = socket;
        }

        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        public void setStream() {
            try {
                in = new DataInputStream(playerSocket.getInputStream());
                out = new DataOutputStream(playerSocket.getOutputStream());
            } catch (EOFException e) {
                // ... this is fine
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        public int gameStart() throws IOException {
            try{
                while (true) {
                    readMsg();
                    if (msg == 0) {
                        return 1;
                    } else if (msg == 2) {
                        return 2;
                    }
                }
            } catch(SocketException e) {
                playerEnd();
                return 0;
            }
        }

        public void playerEnd() throws IOException {
            in.close();
            out.close();
            playerSocket.close();
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
        }

        public void run(){
            try{
                setStream();
                if (gameStart() == 1) {
                    gameInit();
                    gameHold();
                } else if (gameStart() == 2) {

                }
                playerEnd();
            } catch (EOFException e) {
                // ... this is fine
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
