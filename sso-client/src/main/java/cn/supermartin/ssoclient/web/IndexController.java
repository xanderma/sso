package cn.supermartin.ssoclient.web;

import cn.supermartin.export.common.CookieUtils;
import cn.supermartin.export.domin.User;
import cn.supermartin.export.filter.SsoFilter;
import cn.supermartin.export.service.AuthenticationRpcService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author maxiaoding
 * @date 2017/11/12 下午3:32
 * @description:
 */
@Controller
public class IndexController {
    @Resource
    protected AuthenticationRpcService authenticationRpcService;

    @RequestMapping("/")
    public String index_(HttpServletRequest request, ModelMap modelMap) {
        String token = CookieUtils.getCookie(request, SsoFilter.SSO_TOKEN_NAME);
        User user = authenticationRpcService.validate(token);
        modelMap.addAttribute("username", user.getUsername());
        return "index";
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest request, ModelMap modelMap) {
        return index_(request, modelMap);
    }

}
