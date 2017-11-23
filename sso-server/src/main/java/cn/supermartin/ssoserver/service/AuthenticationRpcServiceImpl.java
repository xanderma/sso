package cn.supermartin.ssoserver.service;

import cn.supermartin.export.domin.User;
import cn.supermartin.export.service.AuthenticationRpcService;
import cn.supermartin.ssoserver.common.TokenManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("authenticationRpcService")
public class AuthenticationRpcServiceImpl implements AuthenticationRpcService {

	@Resource
	private TokenManager tokenManager;

	@Override
	public  User validate(String token) {
		return tokenManager.validate(token);
	}

	@Override
	public void logout(String token) {
		tokenManager.remove(token);
	}

}
