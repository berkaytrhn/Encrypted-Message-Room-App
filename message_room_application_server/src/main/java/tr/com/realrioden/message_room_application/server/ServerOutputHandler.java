package tr.com.realrioden.message_room_application.server;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Base64;

public class ServerOutputHandler {

    public static void writeLogFile(String message,boolean isFirstLoop) throws IOException {
        //performs writing to log file and console
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ServerOld.arguments.get("encrypted_log"),true));
        BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(ServerOld.arguments.get("standard_log"), true));




        //for writing IV and Key
        if(isFirstLoop){
            String keyBase64;
            String vectorBase64;

            String key;
            byte[] vector;

            //Receive current timestamp
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            key = ServerOld.stringKey;
            vector = ServerOld.vector;

            //convert to base64
            keyBase64 = Base64.getEncoder().encodeToString(key.getBytes());
            vectorBase64 = Base64.getEncoder().encodeToString(vector);

            bufferedWriter.write(String.format("%s || STARTED SERVER ||\n", timestamp));
            bufferedWriter.write(String.format("%s || SERVER INFO || %s(%s) : %s\n", "Key", timestamp, ServerOld.arguments.get("method"),keyBase64));
            bufferedWriter.write(String.format("%s || SERVER INFO || %s(%s) : %s\n", "IV",  timestamp, ServerOld.arguments.get("method"), vectorBase64));

            bufferedWriter2.write(String.format("%s || STARTED SERVER ||\n", timestamp));
            bufferedWriter2.write(String.format("%s || SERVER INFO || %s(%s) : %s\n", "Key", timestamp, ServerOld.arguments.get("method"),ServerOld.stringKey));
            bufferedWriter2.write(String.format("%s || SERVER INFO || %s(%s) : %s\n", "IV", timestamp, ServerOld.arguments.get("method"), ServerOld.stringInitVector));

            System.out.println(String.format("%s || STARTED SERVER ||", timestamp));
            System.out.println("Key -> "+keyBase64);
            System.out.println("IV  -> "+vectorBase64);
            // base64 log
            bufferedWriter.flush();
            bufferedWriter.close();
            // standard log
            bufferedWriter2.flush();
            bufferedWriter2.close();
        }else{
            //writing message part

            //convert message to base64
            String[] messageArray = message.split("\t");
            String userName = messageArray[0];
            String realMessage = messageArray[1];
            String messageBase64 = Base64.getEncoder().encodeToString(realMessage.getBytes());

            //timestamp
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            bufferedWriter.write(String.format("%s || MESSAGE INFO || %s : %s\n", timestamp, userName, messageBase64));
            bufferedWriter2.write(String.format("%s || MESSAGE INFO || %s : %s\n", timestamp, userName, realMessage));

            System.out.println(String.format("%s || MESSAGE INFO || %s : %s(%s)", timestamp, userName, realMessage, messageBase64));

            bufferedWriter.flush();
            bufferedWriter.close();
            bufferedWriter2.flush();
            bufferedWriter2.close();
        }



    }
}
