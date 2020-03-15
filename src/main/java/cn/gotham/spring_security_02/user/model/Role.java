package cn.gotham.spring_security_02.user.model;

import cn.gotham.spring_security_02.user.enumeration.Authority;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 角色模型
 * @author tanchong
 * Create Date: 2020/3/8
 */
@Document("role")
public class Role {

    @Id
    private ObjectId id;

    @Field("role_name")
    private String roleName;

    private List<Authority> authorityList;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<Authority> getAuthorityList() {
        return authorityList;
    }

    public void setAuthorityList(List<Authority> authorityList) {
        this.authorityList = authorityList;
    }
}
