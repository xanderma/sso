package cn.supermartin.ssoserver.common;


import cn.supermartin.export.domin. User;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class RedisTokenManager extends TokenManager {

	/**
	 * 是否需要扩展token过期时间
	 */
	private Set<String> tokenSet = new CopyOnWriteArraySet<String>();

	@Resource
	private RedisCache< User> redisCache;

	@Override
	public void addToken(String token,  User  user) {
		redisCache.set(token,  user, tokenTimeout * 1000);
	}

	@Override
	public  User validate(String token) {
		 User  user = redisCache.get(token);
		if ( user != null && !tokenSet.contains(token)) {
			tokenSet.add(token);
			addToken(token,  user);
		}
		return  user;
	}

	@Override
	public void remove(String token) {
		redisCache.delete(token);
	}

	@Override
	public void verifyExpired() {
		tokenSet.clear();
	}
}
