import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Base64;

public class ReceiveMessageThread {

    public static Socket Client() throws IOException{
        Socket socket = new Socket(Client.arguments.get("server"), Integer.parseInt(Client.arguments.get("port")));
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
                            if(message.split(" ").length == 3){
                                //keys
                                String[] array = message.split(" ");
                                String key = array[0];
                                String method = array[1];
                                //init method and mode
                                Client.mode = array[2];
                                Client.method = array[1];

                                if(method.trim().equals("AES")){
                                    Client.key = convertStringToKey(key,"AES");
                                }else if(method.trim().equals("DES")){
                                    Client.key = convertStringToKey(key,"DES");
                                }else{
                                    //System.out.println("error while send keys");
                                }
                            }else{
                                if(message.split(" ").length == 8){
                                    //DES vector
                                    Client.initVector = convertInitVector(message,8);
                                }else if(message.split(" ").length == 16){
                                    //AES vector
                                    Client.initVector = convertInitVector(message,16);
                                }else{
                                    //System.out.println("init vector gonderilirken problem olmus!!");
                                }
                            }

                        } else {
                            String userName = message.split("\t")[0];
                            String encryptedMessage = message.split("\t")[1];
                            //Client.printChatBox(encryptedMessage);
                            String realMessage="";
                            //decryption operation
                            try {
                                //Client.controlRadioButtons();
                                realMessage = ClientMyCrypt.decryption(String.format("%s/%s/PKCS5PADDING", Client.method, Client.mode), Client.key, encryptedMessage, Client.initVector);
//                                else if(Client.method.equals("DES")){
//                                    realMessage = MyCrypt.decryption(String.format("%s/%s/PKCS5PADDING", Client.method, Client.mode), Client.key,encryptedMessage, Client.initVector);
//                                }
                            } catch (Exception e) {
                                //System.out.println("Error while decryption operation");
                                e.printStackTrace();
                            }
                            Client.printChatBox(String.format("%s: %s",userName,realMessage));
                            //message received, set disable false
                            Client.updateSendMessageButton(false);
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
