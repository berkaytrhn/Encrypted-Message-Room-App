package tr.com.realrioden.message_room_application.server;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import javax.crypto.SecretKey;

public class Server {
    public static String stringInitVector;
    public static SecretKey Key;
    public static String stringKey;
    public static byte[] vector; //init vector

    public static HashMap<String,String> arguments;

    public static void main(String[] args) throws Exception {
        try {
            String configPath = args[0];
            arguments = Server.argumentParser(configPath);
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("An error occurred, possible solutions: 'running with config file argument' or 'checking config file contents.'");
            return;
        }
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(arguments.get("port")));
        Threads threads = new Threads();


        //creating keys and init vectors

        Server.Key = MyCrypt.generateKey(arguments.get("method"),Integer.parseInt(arguments.get("key_size")));
        byte[] initVector = MyCrypt.createInitializationVector(Integer.parseInt(arguments.get("method_coefficient"))*8);
        Server.vector = initVector;
        Server.stringInitVector = convertInitVector(vector);
        Server.stringKey = ServerThread.convertKeyToString(Server.Key);


        //write keys and vectors
        ServerOutputHandler.writeLogFile("None",true);

        //loop for multiple clients
        while(true){
            Socket socket = serverSocket.accept();

            //thread starts here
            ServerThread tempThread = new ServerThread(socket,threads);
            tempThread.startCommunicationThread();
            threads.addThread(tempThread);

        }
    }

    public static HashMap<String, String> argumentParser(String path) throws IOException {
        String content = MyFileReader.fileReader(path);

       HashMap<String,String> args = new HashMap<String,String>();
        for(String line: content.split("\n")){
            if (line.charAt(0) == '#'){
                // comment line
                continue;
            }
            String[] lineArray = line.split("=");
            args.put(lineArray[0], lineArray[1]);
        }
        return args;
    }

    public static String convertInitVector(byte[] initVector){
        String realInitVector = "";
        for(int i=0;i<initVector.length;i++){
            if(i == (initVector.length-1)){
                realInitVector += initVector[i];
            }else{
                realInitVector += (initVector[i]+" ");
            }
        }
        return realInitVector;
    }

}
