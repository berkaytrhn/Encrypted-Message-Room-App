package tr.com.realrioden.message_room_application.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tr.com.realrioden.message_room_application.dto.db.User;

@Repository
public interface UserRepository extends MongoRepository<User, String>{
	public Optional<User> findById(String id);	
}
