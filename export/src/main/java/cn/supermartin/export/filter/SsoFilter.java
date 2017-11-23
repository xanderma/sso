package cn.supermartin.export.filter;

import cn.supermartin.export.common.CookieUtils;
import cn.supermartin.export.domin.User;
import cn.supermartin.export.service.AuthenticationRpcService;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class SsoFilter implements Filter {

    public static final String SSO_PARAM_NAME = "__vt_param__";
    public static final String SSO_TOKEN_NAME = "token";
    //令牌有效时间，默认30分钟 
    public static int tokenTimeout = 1800;


    private String ssoServerUrl;
    private String logoutUri;
    private AuthenticationRpcService authenticationRpcService;

    private SsoFilter() {
    }

    public SsoFilter(String ssoServerUrl, String logoutUri, int tokenTimeout, AuthenticationRpcService authenticationRpcService) {
        this.ssoServerUrl = ssoServerUrl;
        this.logoutUri = logoutUri;
        this.tokenTimeout = tokenTimeout;
        this.authenticationRpcService = authenticationRpcService;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (request.getRequestURI().equals(logoutUri)) {
            //退出登录
            authenticationRpcService.logout(CookieUtils.getCookie(request, SSO_TOKEN_NAME));
            redirectLogin(getLocalUrl(request), request, response);
        } else {
            //其他请求
            loginFilter(request, response, filterChain);
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 登录过滤
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    private void loginFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = CookieUtils.getCookie(request, SSO_TOKEN_NAME);
        if (StringUtils.isEmpty(token)) {
            if (getParameterToken(request, response) != null) {
                // 再跳转一次当前URL，以便去掉URL中token参数
                response.sendRedirect(request.getRequestURL().toString());
            } else {
                redirectLogin(request.getRequestURL().toString(), request, response);
            }
        } else if (authenticationRpcService.validate(token) != null) {
            chain.doFilter(request, response);
        } else {
            redirectLogin(request.getRequestURL().toString(), request, response);
        }
    }

    /**
     * 获取服务端回传token参数且验证
     *
     * @param request
     * @return
     */
    private String getParameterToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getParameter(SSO_PARAM_NAME);
        if (token != null) {
            User user = authenticationRpcService.validate(token);
            if (user != null) {
                CookieUtils.addCookie(SSO_TOKEN_NAME, token, tokenTimeout, request, response);
                return token;
            }
        }
        return null;
    }

    /**
     * 跳转到登录页
     *
     * @param backUrl
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void redirectLogin(String backUrl, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        CookieUtils.removeCookie(SSO_TOKEN_NAME, request, response);
        String ssoLoginUrl = new StringBuilder().append(ssoServerUrl).append("/login?backUrl=")
                .append(URLEncoder.encode(backUrl, "utf-8")).toString();
        response.sendRedirect(ssoLoginUrl);
    }

    /**
     * 获取当前上下文路径
     *
     * @param request
     * @return
     */
    private String getLocalUrl(HttpServletRequest request) {
        return new StringBuilder().append(request.getScheme()).append("://").append(request.getServerName())
                .append(":").append(request.getServerPort() == 80 ? "" : request.getServerPort())
                .append(request.getContextPath()).toString();
    }
}