package tr.com.realrioden.message_room_application.dto.db;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "users")
@Builder
@Data
public class User {
	@Id
	private String id;
	private String name;
}
