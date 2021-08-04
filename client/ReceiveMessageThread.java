import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.*;
import java.io.*;
import java.util.Base64;

public class ReceiveMessageThread {

    public static Socket Client() throws IOException{
        Socket socket = new Socket("localhost",8080);
        ClientThread inputThread = new ClientThread(socket);
        inputThread.startThread();
        return socket;
    }

    public static byte[] convertInitVector(String vector,int size){
        byte[] realVector = new byte[size];
        for(int i=0;i<size;i++){
            realVector[i] = Byte.parseByte(vector.split(" ")[i]);
        }
        return realVector;
    }

    public static void sendMessage(String message,Socket socket) throws IOException{
        PrintWriter dataOut = new PrintWriter(socket.getOutputStream());
        Client.dataOut = dataOut;
        message = configureMessage(message);
        dataOut.println(message);
        dataOut.flush();
    }

    public static SecretKey convertStringToKey(String stringKey,String method){
        byte[] decodedKey = Base64.getDecoder().decode(stringKey);
        SecretKey key = new SecretKeySpec(decodedKey,0,decodedKey.length,method);
        return key;
    }

    public static String configureMessage(String message){
        return String.format("%s\t%s", Client.userName,message);
    }

    static class ClientThread implements Runnable{
        private Socket clientSocket;
        private Thread thread;
        public ClientThread(Socket clientSocket) {
            //System.out.println("Client thread started and waits for messages");
            this.clientSocket = clientSocket;
        }

        public void startThread(){
            if(thread == null){
                thread = new Thread(this);
                Client.clientThread = thread;
                thread.start();
            }
        }

        @Override
        public void run() {
            try {
                InputStreamReader dataIn = new InputStreamReader(this.clientSocket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(dataIn);

                multipleMessageLoop:
                while(true){
                    try {
                        String message = bufferedReader.readLine();

                        //control radio buttons
                        if (message.split("\t").length == 1) {
                            if(message.split(" ").length == 2){
                                //keys
                                String[] array = message.split(" ");
                                String key = array[0];
                                String method = array[1];
                                if(method.equals("AES")){
                                    Client.AESKey = convertStringToKey(key,"AES");
                                }else if(method.equals("DES")){
                                    Client.DESKey = convertStringToKey(key,"DES");
                                }else{
                                    //System.out.println("error while send keys");
                                }
                            }else{
                                if(message.split(" ").length == 8){
                                    //DES vector
                                    Client.initVectorDES = convertInitVector(message,8);
                                }else if(message.split(" ").length == 16){
                                    //AES vector
                                    Client.initVectorAES = convertInitVector(message,16);
                                }else{
                                    //System.out.println("init vector gonderilirken problem olmus!!");
                                }
                            }

                        } else {
                            String userName = message.split("\t")[0];
                            String encryptedMessage = message.split("\t")[1];
                            Client.printChatBox(encryptedMessage);
                            String realMessage="";
                            //decryption operation
                            try {
                                Client.controlRadioButtons();
                                if(Client.method.equals("AES")){
                                    realMessage = MyCrypt.decryption(String.format("%s/%s/PKCS5PADDING", Client.method, Client.mode), Client.AESKey,encryptedMessage, Client.initVectorAES);
                                }else if(Client.method.equals("DES")){
                                    realMessage = MyCrypt.decryption(String.format("%s/%s/PKCS5PADDING", Client.method, Client.mode), Client.DESKey,encryptedMessage, Client.initVectorDES);
                                }
                            } catch (Exception e) {
                                //System.out.println("Error while decryption operation");
                                e.printStackTrace();
                            }
                                Client.printChatBox(String.format("%s> %s",userName,realMessage));
                        }


                    }catch (SocketException e){
                        //System.out.println("Socket closed, thread will stop");
                        break multipleMessageLoop;
                    }
                }
                dataIn.close();
                clientSocket.close();
            } catch (IOException e) {
                //System.out.println("error inside client receiving operation");
                e.printStackTrace();
            }
        }
    }

}
