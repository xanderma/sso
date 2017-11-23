package cn.supermartin.export.service;

import cn.supermartin.export.domin.User;

import java.util.List;


public interface AuthenticationRpcService {

    /**
     * 验证是否已经登录
     *
     * @param token
     * @return
     */
    User validate(String token);

    /**
     * 退出登录
     *
     * @param token
     * @return
     */
    void logout(String token);
}
