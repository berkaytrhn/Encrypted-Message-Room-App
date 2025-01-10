package tr.com.realrioden.message_room_application.server;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ServerController {

	
	
	
	@MessageMapping("/sendMessage")
	public void sendMessageToRoom(Message message) {
		// Multi Client Support
		
		// get forward endpoint from message


		// send to target client 
	}
}
