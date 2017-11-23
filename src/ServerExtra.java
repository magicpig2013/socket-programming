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
            HangmanGame.Player playerOne = game.new Player(serverSocket.accept(),"player 1");
            HangmanGame.Player playerTwo = game.new Player(serverSocket.accept(),"player 2");
            playerOne.setOpponent(playerTwo);
            playerTwo.setOpponent(playerOne);
            game.currentPlayer = playerOne;
            playerOne.start();
            playerTwo.start();
        }
    }
}

class HangmanGame {
    Player currentPlayer;
    int playMode;
    String[] dist;
    String answer = "";
    String result = "";
    private String incorrectGuess = "";
    private boolean changeFlag = false;
    // When one client sent result, reverse this flag
    // If only one client has sent result, then this flag will be true
    private boolean resultSentFlag = false;

    public HangmanGame(String[] dist) {
        this.dist = dist;
        this.playMode = 0;
        int randomNum = ThreadLocalRandom.current().nextInt(0, dist.length);
        this.answer = dist[randomNum];
        for (int i = 0; i < this.answer.length(); i++) {
            this.result += '_';
        }
    }

    public void sendWel(Player player) throws IOException {
        if (player == currentPlayer) {
            player.sendMsg("It's your turn"); // length = 14
        } else {
            player.sendMsg("Waiting for " + player.opponent.name + " to guess");// length = 29
        }
    }

    public void gameInit(Player player) throws IOException {
        if (player.opponent == null) {
            player.sendMsg("Waiting for the other player...");
            while(true) {
                if (player.opponent != null) {
                    break;
                }
            }
        }
        player.sendMsg("You are " + player.name);
        player.sendMsg("Both players is connected");
        player.sendMsg("Game start ^_^");
    }

    public synchronized void playerGuess(Player player) throws IOException{
        if (player == currentPlayer) {
            player.opponent.sendMsg("Waiting for " + player.name + " to guess");
            player.sendMsg("It's your turn");
            while(true) {
                player.sendMsg("Letter to guess: ");
                player.readMsg(); // read the client letter
                if (player.inputLine.equals("")) {
                    player.sendMsg("Error! Please guess a letter.");
                    continue;
                }
                char letter = player.inputLine.charAt(0);
                if (player.msg != 1 || !((letter >= 'a' && letter <='z') || (letter >= 'A' && letter <='Z'))) {
                    player.sendMsg("Error! Please guess a letter.");
                } else if (incorrectGuess.indexOf(letter) != -1 || result.indexOf(letter) != -1) {
                    player.sendMsg("Error! Letter " + letter + "has been guessed before, please guess another letter.");
                } else {
                    player.generateResult(letter); // correct input. Now change the result.
                    changeFlag = true;
                    break;
                }
            }
            playerSendResult(player);
            playerSendResult(player.opponent);
            currentPlayer = currentPlayer.opponent;
        }
    }

    public void playerSendResult(Player player) throws IOException{
        boolean win = true;
        player.sendResult(result);
        //resultSentFlag = !resultSentFlag;
        if (incorrectGuess.length() >= 6) {
            player.sendMsg("You Lose :(");
            player.sendMsg("Game Over!");
            return;
        }
        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '_') {
                win = false;
                break;
            }
        }
        if (win) {
            player.sendMsg("You Win!");
            player.sendMsg("Game Over!");
        }
    }

    public void takeTurn() {
        if (changeFlag && !resultSentFlag) {
            changeFlag = false;
        }
    }

    class Player extends Thread {
        public Player opponent = null;
        public String name = "";

        private Socket playerSocket;
        private DataOutputStream out;
        private DataInputStream in;

        private int msg = 0;
        private String inputLine = "";

        public Player(Socket socket, String name) {
            this.playerSocket = socket;
            this.name = name;
        }

        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        public void setPlayerName(String playerName) {
            this.name = playerName;
        }

        public void opponentChoice(char letter) {
            while (incorrectGuess.length() < 6) {

            }
        }

        public void run(){
            try{
                setStream();
                gameInit(this);
                gameHold();
                playerEnd();
            } catch (EOFException e) {
                // ... this is fine
            } catch(IOException e) {
                e.printStackTrace();
            }
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

        public boolean gameStart() throws IOException {
            try{
                while (true) {
                    readMsg();
                    if (msg == 0) {
                        return true;
                    }
                }
            } catch(SocketException e) {
                playerEnd();
                return false;
            }
        }

        public void gameHold() throws IOException {
//            sendWel(this);
            while (incorrectGuess.length() < 6) {
                playerGuess(this);
//                while(true) {
//                    if (changeFlag == true) {
//                        playerSendResult(this);
//                        break;
//                    }
//                }
//                takeTurn();
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


    }
}
