import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import javax.crypto.SecretKey;

public class Server {
    public static String stringInitVectorAES;
    public static String stringInitVectorDES;
    public static SecretKey AESKey;
    public static SecretKey DESKey;
    public static String stringAESKey;
    public static String stringDESKey;
    public static byte[] vectorAES;
    public static byte[] vectorDES;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        Threads threads = new Threads();


        //creating keys and init vectors
        Server.AESKey = MyCrypt.generateKey("AES",256);
        Server.DESKey = MyCrypt.generateKey("DES",56);
        byte[] initVectorAES = MyCrypt.createInitializationVector(16);
        byte[] initVectorDES = MyCrypt.createInitializationVector(8);
        Server.vectorAES = initVectorAES;
        Server.vectorDES = initVectorDES;
        Server.stringInitVectorAES = convertInitVector(initVectorAES);
        Server.stringInitVectorDES = convertInitVector(initVectorDES);
        Server.stringAESKey = ServerThread.convertKeyToString(Server.AESKey);
        Server.stringDESKey = ServerThread.convertKeyToString(Server.DESKey);


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
