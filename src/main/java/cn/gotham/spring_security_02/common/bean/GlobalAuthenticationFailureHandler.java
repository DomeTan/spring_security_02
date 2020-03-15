package cn.gotham.spring_security_02.common.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * 校验失败全局处理器
 * @author tanchong
 * Create Date: 2020/3/15
 */
@Component
public class GlobalAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        response.setContentType("application/json; charset=UTF-8");
        var errorResponse = new ErrorResponse(exception.getMessage());
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");

        if (exception instanceof UsernameNotFoundException) {
            errorResponse.setError(exception.getMessage());
        } else if (exception instanceof BadCredentialsException) {
            errorResponse.setError("用户名密码错误,请重新输入!");
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getOutputStream().write(errorResponse.toString().getBytes());
    }

    @Bean
    public GlobalAuthenticationFailureHandler getGlobalAuthenticationFailureHandler() {
        return new GlobalAuthenticationFailureHandler();
    }
}
