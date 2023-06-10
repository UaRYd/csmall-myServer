package cn.tedu.csmall.passport.security;

import cn.tedu.csmall.passport.mapper.AdminMapper;
import cn.tedu.csmall.passport.pojo.vo.AdminLoginInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminMapper adminMapper;

    public UserDetailsServiceImpl() {
        log.info("创建 Service 对象：UserDetailsServiceImpl");
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.debug("Spring Security 框架自动调用了 UserDetailsServiceImpl.loadUserByUsername() 方法，用户名：{}", s);

        AdminLoginInfoVO loginInfoVO = adminMapper.getLoginInfoByUsername(s);

        if (loginInfoVO == null) {
            log.debug("此用户名没有匹配的用户数据，将返回 null");
            return null;
        }

        log.debug("用户名匹配成功！准备返回此用户名匹配的 UserDetails类型的对象。");
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        List<String> permissions = loginInfoVO.getPermissions();
        for (String permission:
                permissions) {
            GrantedAuthority authority = new SimpleGrantedAuthority(permission);
            authorities.add(authority);
        }

        AdminDetails userDetails = new AdminDetails(
                loginInfoVO.getId(), loginInfoVO.getUsername(), loginInfoVO.getPassword(),
                loginInfoVO.getEnable() == 1, authorities);
//        UserDetails userDetails = User.builder()
//                .username(loginInfoVO.getUsername())
//                .password(loginInfoVO.getPassword())
//                .disabled(loginInfoVO.getEnable() == 0) // 账号状态是否禁用
//                .accountLocked(false)
//                .accountExpired(false)
//                .credentialsExpired(false)
//                .authorities("这是一个临时使用的山寨的权限!!!!!")
//                .build();


        log.debug("即将向 Spring Security 返回 UserDetails 类型的对象：{}", userDetails);
        return userDetails;
    }
}
