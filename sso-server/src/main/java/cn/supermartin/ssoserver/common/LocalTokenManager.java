package cn.supermartin.ssoserver.common;

import cn.supermartin.export.domin.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class LocalTokenManager extends TokenManager {

    private static Logger LOGGER = LoggerFactory.getLogger(LocalTokenManager.class);

    // 令牌存储结构
    private final ConcurrentHashMap<String, DummyUser> tokenMap = new ConcurrentHashMap<String, DummyUser>();

    @Override
    public void verifyExpired() {
        Date now = new Date();
        for (Entry<String, DummyUser> entry : tokenMap.entrySet()) {
            String token = entry.getKey();
            DummyUser dummyUser = entry.getValue();
            // 当前时间大于过期时间
            if (now.compareTo(dummyUser.expired) > 0) {
                // 已过期，清除对应token
                if (now.compareTo(dummyUser.expired) > 0) {
                    tokenMap.remove(token);
                    LOGGER.debug("token : " + token + "已失效");
                }
            }
        }
    }

    public void addToken(String token, User user) {
        DummyUser dummyUser = new DummyUser();
        dummyUser.user = user;
        extendExpiredTime(dummyUser);
        tokenMap.put(token, dummyUser);
    }

    public User validate(String token) {
        DummyUser dummyUser = tokenMap.get(token);
        if (dummyUser == null) {
            return null;
        }
        extendExpiredTime(dummyUser);
        return dummyUser.user;
    }

    public void remove(String token) {
        tokenMap.remove(token);
    }

    /**
     * 扩展过期时间
     *
     * @param dummyUser
     */
    private void extendExpiredTime(DummyUser dummyUser) {
        dummyUser.expired = new Date(new Date().getTime() + tokenTimeout * 1000);
    }

    // 复合结构体，含 user与过期时间expried两个成员
    private class DummyUser {
        private User user;
        private Date expired; // 过期时间
    }
}
