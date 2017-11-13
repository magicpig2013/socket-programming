import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TicTacServer {
    public static final int PORT = 13579;

    public static void main(String arg[]) throws Exception{
        System.out.println("Starting...");
        TicTacServer server = new TicTacServer();
        server.init();
    }

    public void init() throws Exception {
        ServerSocket socket = new ServerSocket(PORT);
        try {
            while (true) {
                Game game = new Game();
                Game.Player playerX = game.new Player(socket.accept(), 'X');
                Game.Player playerO = game.new Player(socket.accept(), 'O');
                playerX.setOpponent(playerO);
                playerO.setOpponent(playerX);
                game.currentPlayer = playerX;
                playerX.start();
                playerO.start();
            }
        } catch(Exception e) {
            System.out.println("Error: " + e);
        } finally {
            socket.close();
        }
    }
}

class Game {
    private Player[] board = {null, null, null, null, null, null, null, null, null};

    Player currentPlayer;

    public boolean hasWinner() {
        return
                (board[0] != null && board[0] == board[1] && board[0] == board[2])
                        ||(board[3] != null && board[3] == board[4] && board[3] == board[5])
                        ||(board[6] != null && board[6] == board[7] && board[6] == board[8])
                        ||(board[0] != null && board[0] == board[3] && board[0] == board[6])
                        ||(board[1] != null && board[1] == board[4] && board[1] == board[7])
                        ||(board[2] != null && board[2] == board[5] && board[2] == board[8])
                        ||(board[0] != null && board[0] == board[4] && board[0] == board[8])
                        ||(board[2] != null && board[2] == board[4] && board[2] == board[6]);
    }

    public boolean boardFilledUp() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == null) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean legalMove(int location, Player player) {
        if (player == currentPlayer && board[location] == null) {
            board[location] = currentPlayer;
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerMoved(location);
            return true;
        }
        return false;
    }

    class Player extends Thread {
        char mark;
        Player opponent;
        Socket socket;
        DataInputStream input;
        DataOutputStream out;

        public Player(Socket socket, char mark) {
            this.socket = socket;
            this.mark = mark;
            try {
                input = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("WELCOME " + mark);
                out.writeUTF("MESSAGE Waiting for opponent to connect");
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            }
        }

        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        public void otherPlayerMoved(int location) {
            try {
                out.writeUTF("OPPONENT_MOVED " + location);
                out.writeUTF(hasWinner() ? "DEFEAT" : boardFilledUp() ? "TIE" : "");
            } catch(IOException e) {
                System.out.println("Error: " + e);
            }
        }

        public void run() {
            try {
                // The thread is only started after everyone connects.
                out.writeUTF("MESSAGE All players connected");

                // Tell the first player that it is her turn.
                if (mark == 'X') {
                    out.writeUTF("MESSAGE Your move");
                }

                // Repeatedly get commands from the client and process them.
                while (true) {
                    String command = input.readUTF();
                    if (command.startsWith("MOVE")) {
                        int location = Integer.parseInt(command.substring(5));
                        if (legalMove(location, this)) {
                            out.writeUTF("VALID_MOVE");
                            out.writeUTF(hasWinner() ? "VICTORY"
                                    : boardFilledUp() ? "TIE"
                                    : "");
                        } else {
                            out.writeUTF("MESSAGE ?");
                        }
                    } else if (command.startsWith("QUIT")) {
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            } finally {
                try {socket.close();} catch (IOException e) {}
            }
        }
    }
}
