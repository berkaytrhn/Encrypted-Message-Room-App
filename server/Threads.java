import java.io.IOException;
import java.util.ArrayList;

public class Threads {
    private ArrayList<ServerThread> allThreads;

    public Threads() {
        this.allThreads = new ArrayList<ServerThread>();
    }


    public void addThread(ServerThread serverThread){
        this.allThreads.add(serverThread);
    }

    public void threadResponse(String message)throws IOException {
        for (ServerThread thread:this.allThreads
             ) {
            thread.sendReceivedMessage(message);
        }
    }

    public ArrayList<ServerThread> getAllThreads() {
        return allThreads;
    }
}
