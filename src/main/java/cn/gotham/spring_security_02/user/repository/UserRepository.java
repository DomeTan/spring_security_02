package cn.gotham.spring_security_02.user.repository;

import cn.gotham.spring_security_02.user.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


/**
 * @author tanchong
 * Create Date: 2020/3/8
 */
public interface UserRepository extends MongoRepository<User, ObjectId> {

    Optional<User> findByUsername(String username);
}
