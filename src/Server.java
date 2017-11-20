import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.net.SocketException;

public class Server {

    private ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        String[] dist;
        int port;
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
        Server server=new Server();
        server.start(port,dist);
    }

}

class HangmanGame {
    private static class Player extends Thread {
        private Player opponent;
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
    }
}
