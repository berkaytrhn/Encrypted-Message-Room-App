package tr.com.realrioden.message_room_application.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import tr.com.realrioden.message_room_application.server.Message;

@Controller
public class ServerWebscoketController {

	
	@MessageMapping("/sendMessage")
	public void sendMessageToRoom(Message message) {
		// Multi Client Support
		
		// get forward endpoint from message


		// send to target client 
	}
}
