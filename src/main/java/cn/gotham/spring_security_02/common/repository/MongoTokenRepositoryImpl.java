package cn.gotham.spring_security_02.common.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.util.Date;

/**
 * @author tanchong
 * Create Date: 2020/3/15
 */
public class MongoTokenRepositoryImpl implements PersistentTokenRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoTokenRepositoryImpl.class);
    private static final String PERSISTENT_COLLETCTION = "persistent_logins";

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        removeUserTokens(token.getUsername());
        mongoTemplate.insert(token,PERSISTENT_COLLETCTION);
        LOGGER.info("创建用户 [ {} ] TOKEN",token.getUsername());
    }

    /**
     * 更新用户TOKEN
     * @param series
     * @param tokenValue
     * @param lastUsed
     */
    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        var query = new Query(Criteria.where("series").is(series));
        Update update = new Update();
        update.set("tokenValue",tokenValue);
        update.set("date",lastUsed);
        mongoTemplate.updateFirst(query,update,PERSISTENT_COLLETCTION);
        LOGGER.info("更新用户TOKEN [{}]",series);
    }

    /**
     * 获取用户TOKEN
     * @param seriesId
     * @return
     */
    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        var query = new Query(Criteria.where("series").is(seriesId));
        var persistentRememberMeToken = mongoTemplate.findOne(query, PersistentRememberMeToken.class, PERSISTENT_COLLETCTION);
        LOGGER.info("获取用户TOKEN [ {} ]",seriesId);
        return persistentRememberMeToken;
    }

    /**
     * 移除用户TOKEN
     * @param username 用户名称
     */
    @Override
    public void removeUserTokens(String username) {
        var query = new Query(Criteria.where("username").is(username));
        mongoTemplate.remove(query,PersistentRememberMeToken.class,PERSISTENT_COLLETCTION);
        LOGGER.info("移除用户 [ {} ] TOKEN",username);
    }
}
