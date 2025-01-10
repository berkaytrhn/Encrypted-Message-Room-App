package tr.com.realrioden.message_room_application.server;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ServerThread implements Runnable{
    private Thread thread;
    private Socket socket;
    private Threads threads;
    public static String currentMode;
    public static String currentMethod;




    public ServerThread(Socket socket,Threads threads) throws IOException, InterruptedException {
        this.socket = socket;
        this.threads = threads;

        //sending init vectors, successful
        sendReceivedMessage(ServerOld.stringInitVector);



        //sending secret keys ,successful
        //KEY AES
        sendReceivedMessage(String.format("%s %s %s",ServerOld.stringKey,ServerOld.arguments.get("method"), ServerOld.arguments.get("mode")));
    }

    public void startCommunicationThread(){
        if(thread == null){
            thread = new Thread(this);
            thread.start();
        }
    }

    public void sendReceivedMessage(String message) throws IOException{
        PrintWriter dataOut = new PrintWriter(this.socket.getOutputStream());
        dataOut.println(message);
        dataOut.flush();
    }

    public static String convertKeyToString(SecretKey key){
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        return encodedKey;
    }

    public String parseReceivedMessage(String message) throws IOException {
        String[] array = message.split("\t");
        String userName = array[0];
        String realMessage = array[1];
        ServerOutputHandler.writeLogFile(message,false);
        return String.format("%s\t%s",userName,realMessage);
    }

    @Override
    public void run() {
        try {
            InputStreamReader dataIn = new InputStreamReader(this.socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(dataIn);

            multipleMessageLoop:
            while (true) {
                String message = bufferedReader.readLine();

                if(message != null){
                    message = parseReceivedMessage(message);
                    this.threads.threadResponse(message);
                }
                if (message == null) {
                    threads.getAllThreads().remove(this);
                    this.thread.interrupt();
                    break multipleMessageLoop;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Threads getThreads() {
        return threads;
    }

    public void setThreads(Threads threads) {
        this.threads = threads;
    }
}
