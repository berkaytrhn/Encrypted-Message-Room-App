package tr.com.realrioden.message_room_application.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tr.com.realrioden.message_room_application.dto.db.Chat;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
	public Optional<Chat> findById(String id);	

}
