package tr.com.realrioden.message_room_application.dto.db;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "chats")
@Builder
@Data
public class Chat {
	@Id
	private String id;
	private String name;
	private List<User> users;
}
