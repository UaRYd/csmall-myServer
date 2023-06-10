package cn.tedu.csmall.product.config;

import cn.tedu.csmall.product.filter.JwtAuthorizationFilter;
import cn.tedu.csmall.product.web.JsonResult;
import cn.tedu.csmall.product.web.ServiceCode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 开启全局的基于方法的安全检查
public class  SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public SecurityConfiguration() {
        log.debug("创建配置类对象：SecurityConfiguration");
    }


    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 开店Spring Security自劳的CorsFilter，以解决垮城问题
        http.cors();

        // STATELESS：无状态的，完全不使用 Session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 将 JWT 过滤器添加到 Spring Security 框架的某些过滤器之前
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        // 处理未通过认证时访问受保护的资源时拒绝访问
        http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                String message = "您当前未登陆，请先登陆！";
                log.warn(message);

                JsonResult jsonResult = JsonResult.fail(ServiceCode.ERR_UNAUTHORIZED, message);
                System.out.println(jsonResult);
                String jsonString = JSON.toJSONString(jsonResult);

                response.setContentType("application/json; charset=utf-8");

                PrintWriter printWriter = response.getWriter();
                printWriter.println(jsonString);
                printWriter.close();
            }
        });

        http.csrf().disable();

        // 白名单
        String[] urls = {
                "/doc.html",
                "/**/*.css",
                "/**/*.js",
                "/swagger-resources",
                "/v2/api-docs",
        };
        // 配置授权访问
        http.authorizeRequests() // 开始对请求进行授权

                .mvcMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()

                .mvcMatchers(urls) // 匹配某些请求
                .permitAll() // 许可，即不需要通过认证就可以访问

                .anyRequest() // 任何请求
                .authenticated();// 要求已经完成认证的
//        super.configure(http);
        //http.formLogin();
    }
}
