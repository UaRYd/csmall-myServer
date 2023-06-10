package cn.tedu.csmall.product.filter;

import cn.tedu.csmall.product.security.LoginPrincipal;
import cn.tedu.csmall.product.web.JsonResult;
import cn.tedu.csmall.product.web.ServiceCode;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * JWT 过滤器：接收 JWT，解析 JWT，将解析得到的数据创建为认证信息并存入到 SecurityContext
 */
@Component
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Value("${csmall.jwt.secret-key}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("JwtAuthorizationFilter 开始执行......");

        String jwt = request.getHeader("Authorization");
        log.debug("客户端携带的 JWT：{}", jwt);

        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response); // 执行后续过滤器
            return; // 结束过滤器环节
        }

        // 尝试解析 JWT
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody();
        } catch (MalformedJwtException e) {
            String message = "非法访问！";
            log.warn("程序运行过程中出现了 MalformedJwtException，将向客户端响应错误信息！");
            log.warn("错误信息：{}",  message);

            JsonResult jsonResult = JsonResult.fail(ServiceCode.ERR_UNAUTHORIZED, message);
            String jsonString = JSON.toJSONString(jsonResult);

            response.setContentType("application/json; charset=utf-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(jsonString);
            printWriter.close();
        } catch (SignatureException e) {
            String message = "非法访问！";
            log.warn("程序运行过程中出现了 SignatureException，将向客户端响应错误信息！");
            log.warn("错误信息：{}",  message);

            JsonResult jsonResult = JsonResult.fail(ServiceCode.ERR_UNAUTHORIZED, message);
            String jsonString = JSON.toJSONString(jsonResult);

            response.setContentType("application/json; charset=utf-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(jsonString);
            printWriter.close();
        } catch (ExpiredJwtException e) {
            String message = "您的登陆信息已经过期，请重新登陆！";
            log.warn("程序运行过程中出现了 ExpiredJwtException 错误，将向客户端响应错误信息！");
            log.warn("错误信息：{}", message);

            JsonResult jsonResult = JsonResult.fail(ServiceCode.ERR_UNAUTHORIZED, message);
            String jsonString = JSON.toJSONString(jsonResult);

            response.setContentType("application/json; charset=urf-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(jsonString);
            printWriter.close();
        } catch (Throwable e) {
            String message = "服务器忙，请稍后再试！【在开发过程中，如果看到此提示，应该检查服务器端的控制台，分析异常，并在全局异常处理器中补充处理对应异常的方法】";
            log.warn("程序运行过程中出现了 Throwable 错误，将向客户端响应错误信息！");
            log.warn("错误信息：{}", message);

            JsonResult jsonResult = JsonResult.fail(ServiceCode.ERR_UNKNOWN, message);
            String jsonString = JSON.toJSONString(jsonResult);

            response.setContentType("application/json; charset=urf-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(jsonString);
            printWriter.close();
        }

        Long id = claims.get("id", Long.class);
        String username = claims.get("username", String.class);
        String authoritiesJsonString = claims.get("authoritiesJsonString", String.class);

        // 创建认证信息
        Object principal = new LoginPrincipal().setId(id).setUsername(username);
        Object credentials = null;
        Collection<SimpleGrantedAuthority> authorities = JSON.parseArray(authoritiesJsonString, SimpleGrantedAuthority.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, credentials, authorities
        );

        // 将认证信息存入到 SecurityContext 中
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        // 放行
        filterChain.doFilter(request, response);
    }
}