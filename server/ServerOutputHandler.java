import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

public class ServerOutputHandler {

    public static void writeLogFile(String message,boolean isFirstLoop) throws IOException {
        //performs writing to log file and console
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("log.txt",true));



        //for writing IV and Key
        if(isFirstLoop){
            String AESKeyBase64;
            String DESKeyBase64;
            String AESVectorBase64;
            String DESVectorBase64;

            String AESKey;
            String DESKey;
            byte[] AESVector;
            byte[] DESVector;

            AESKey = Server.stringAESKey;
            DESKey = Server.stringDESKey;
            AESVector = Server.vectorAES;
            DESVector = Server.vectorDES;

            //convert to base64
            AESKeyBase64 = Base64.getEncoder().encodeToString(AESKey.getBytes());
            DESKeyBase64 = Base64.getEncoder().encodeToString(DESKey.getBytes());
            AESVectorBase64 = Base64.getEncoder().encodeToString(AESVector);
            DESVectorBase64 = Base64.getEncoder().encodeToString(DESVector);

            bufferedWriter.write(String.format("%s %s\n","AESKey",AESKeyBase64));
            bufferedWriter.write(String.format("%s %s\n","DESKey",DESKeyBase64));
            bufferedWriter.write(String.format("%s %s\n","AESIV",AESVectorBase64));
            bufferedWriter.write(String.format("%s %s\n","DESIV",DESVectorBase64));

            System.out.println("AESKey "+AESKeyBase64);
            System.out.println("DESKey "+DESKeyBase64);
            System.out.println("AESIV "+AESVectorBase64);
            System.out.println("DESIV "+DESVectorBase64);
            bufferedWriter.flush();
            bufferedWriter.close();
        }else{
            //writing message part
            String[] messageArray = message.split("\t");
            String userName = messageArray[0];
            String realMessage = messageArray[1];
            String messageBase64 = Base64.getEncoder().encodeToString(realMessage.getBytes());
            bufferedWriter.write(String.format("%s> %s\n",userName,messageBase64));
            System.out.println(String.format("%s> %s",userName,messageBase64));

            bufferedWriter.flush();
            bufferedWriter.close();
        }



    }
}
