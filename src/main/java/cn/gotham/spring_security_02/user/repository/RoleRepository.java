package cn.gotham.spring_security_02.user.repository;

import cn.gotham.spring_security_02.user.model.Role;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author tanchong
 * Create Date: 2020/3/8
 */
public interface RoleRepository extends MongoRepository<Role, ObjectId> {
}
