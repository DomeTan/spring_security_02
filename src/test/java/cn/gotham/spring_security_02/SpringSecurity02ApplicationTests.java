package cn.gotham.spring_security_02;

import cn.gotham.spring_security_02.user.enumeration.Authority;
import cn.gotham.spring_security_02.user.model.Role;
import cn.gotham.spring_security_02.user.model.User;
import cn.gotham.spring_security_02.user.repository.RoleRepository;
import cn.gotham.spring_security_02.user.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootTest
class SpringSecurity02ApplicationTests {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        // 创建ADMIN 角色
        var adminRole = new Role();
        adminRole.setRoleName("ADMIN");
        var adminAuthorities = Arrays.stream(Authority.values())
                .filter(Objects::nonNull)
                .filter(authority -> authority.getDescription().equals("GOTHAM-管理员"))
                .collect(Collectors.toList());
        adminRole.setAuthorityList(adminAuthorities);
        roleRepository.insert(adminRole);
        // 创建USER 角色
        var userRole = new Role();
        userRole.setRoleName("USER");
        var userAuthorities = Arrays.stream(Authority.values())
                .filter(Objects::nonNull)
                .filter(authority -> authority.getDescription().equals("GOTHAM-用户"))
                .collect(Collectors.toList());
        userRole.setAuthorityList(userAuthorities);
        roleRepository.insert(userRole);
        // 创建所有权限角色(ADMIN+USER)
        var commonRole = new Role();
        commonRole.setRoleName("ADMIN+USER");
        var commonAuthorities = Arrays.stream(Authority.values())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        commonRole.setAuthorityList(commonAuthorities);
        roleRepository.insert(commonRole);
        // 再创建三个用户 分别为user admin common
        var user = new User();
        user.setUsername("user");
        user.setPassword(DigestUtils.md5Hex("123user"));
        user.setEmail("1097172038@qq.com");
        user.setRoles(List.of(userRole));
        userRepository.insert(user);
        var admin = new User();
        admin.setUsername("admin");
        admin.setPassword(DigestUtils.md5Hex("123admin"));
        admin.setEmail("1097172038@qq.com");
        admin.setRoles(List.of(adminRole));
        userRepository.insert(admin);
        var common = new User();
        common.setUsername("common");
        common.setPassword(DigestUtils.md5Hex("123common"));
        common.setEmail("1097172038@qq.com");
        common.setRoles(List.of(commonRole));
        userRepository.insert(common);
    }

}
