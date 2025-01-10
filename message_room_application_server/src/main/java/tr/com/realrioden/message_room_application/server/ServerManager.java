package tr.com.realrioden.message_room_application.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerManager {

	
	@Value("${key.size: 256}")
	private int keySize;
	
	@Value("${key.coefficient: 2}")
	private int keyCoefficient;
	
	// TODO: Add other configuration parameters
	
	
	
}
