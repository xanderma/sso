package cn.supermartin.ssoserver.web;

import cn.supermartin.export.domin.User;
import cn.supermartin.export.common.CookieUtils;
import cn.supermartin.export.filter.SsoFilter;
import cn.supermartin.ssoserver.common.TokenManager;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

/**
 * @author maxiaoding
 * @date 2017/11/12 下午3:32
 * @description:
 */
@Controller
public class LoginController {

    @Resource
    private TokenManager tokenManager;

    @RequestMapping(value = "/login")
    public String login(String backUrl, HttpServletRequest request) throws UnsupportedEncodingException {
        String token = CookieUtils.getCookie(request, SsoFilter.SSO_TOKEN_NAME);
        if (token == null) {
            return loginPath(backUrl, request);
        } else {
            User user = tokenManager.validate(token);
            if (user != null) {
                //跳转到原请求
                backUrl = URLDecoder.decode(backUrl, "utf-8");
                return "redirect:" + goBackUrl(backUrl, token);
            } else {
                return loginPath(backUrl, request);
            }
        }
    }

    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    public String login(String backUrl, String username, String userpwd,
                        HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        //检验用户名和密码
//        if (!"abc".equals(username) || !"abc".equals(userpwd)) {
//            request.setAttribute("errorMessage", "用户名或密码错误");
//            return loginPath(backUrl, request);
//        }
        if (StringUtils.isEmpty(backUrl)) {
            return loginPath(backUrl, request);
        }

        User user = new User(username);
        String token = CookieUtils.getCookie(request, SsoFilter.SSO_TOKEN_NAME);
        if (StringUtils.isEmpty(token) || tokenManager.validate(token) == null) {
            //没有登录
            token = createUUIDId();
            tokenManager.addToken(token, user);
            CookieUtils.addCookie(SsoFilter.SSO_TOKEN_NAME, token, SsoFilter.tokenTimeout, request, response);
        } else {
            tokenManager.addToken(token, user);
        }

        //跳转到原请求
        backUrl = URLDecoder.decode(backUrl, "utf-8");
        return "redirect:" + goBackUrl(backUrl, token);
    }

    private String loginPath(String backUrl, HttpServletRequest request) {
        request.setAttribute("backUrl", backUrl);
        return "login";
    }

    private String goBackUrl(String backUrl, String token) {
        StringBuilder sbf = new StringBuilder(backUrl);
        if (backUrl.indexOf("?") > 0) {
            sbf.append("&");
        } else {
            sbf.append("?");
        }
        sbf.append(SsoFilter.SSO_PARAM_NAME).append("=").append(token);
        return sbf.toString();
    }


    public static String createUUIDId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }
}
