package cn.tedu.csmall.passport.service.impl;

import cn.tedu.csmall.passport.ex.ServiceException;
import cn.tedu.csmall.passport.mapper.AdminMapper;
import cn.tedu.csmall.passport.mapper.AdminRoleMapper;
import cn.tedu.csmall.passport.pojo.entity.Admin;
import cn.tedu.csmall.passport.pojo.entity.AdminRole;
import cn.tedu.csmall.passport.pojo.param.AdminAddNewParam;
import cn.tedu.csmall.passport.pojo.param.AdminLoginInfoParam;
import cn.tedu.csmall.passport.pojo.param.AdminUpdateInfoParam;
import cn.tedu.csmall.passport.pojo.vo.AdminListItemVO;
import cn.tedu.csmall.passport.pojo.vo.AdminLoginInfoVO;
import cn.tedu.csmall.passport.pojo.vo.AdminUpdateInfoVO;
import cn.tedu.csmall.passport.pojo.vo.PageData;
import cn.tedu.csmall.passport.security.AdminDetails;
import cn.tedu.csmall.passport.service.IAdminService;
import cn.tedu.csmall.passport.util.PageInfoToPageDataConverter;
import cn.tedu.csmall.passport.web.ServiceCode;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AdminServiceImpl implements IAdminService {

    @Value("${csmall.jwt.duration-in-minute}")
    private Long durationInMinute;
    @Value("${csmall.jwt.secret-key}")
    private String secretKey;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AdminRoleMapper adminRoleMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public PageData<AdminListItemVO> list(Integer pageNum) {
        Integer pageSize = 5;
        return list(pageNum, pageSize);
    }

    private PageData<AdminListItemVO> list(Integer pageNum, Integer pageSize) {
        log.debug("开始处理【查询管理员列表】的业务，页码：{}，每页记录数：{}", pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<AdminListItemVO> list = adminMapper.list();
        PageInfo<AdminListItemVO> pageInfo = new PageInfo<>(list);
        PageData<AdminListItemVO> pageData = PageInfoToPageDataConverter.convert(pageInfo);
        log.debug("查询完成，即将返回：{}", pageData);
        return pageData;
    }

    @Override
    public String login(AdminLoginInfoParam adminLoginInfoParam) {
        log.debug("开始处理【管理员登陆】的业务，参数{}", adminLoginInfoParam);
        // 创建认证时所需的参数对象
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                adminLoginInfoParam.getUsername(),
                adminLoginInfoParam.getPassword()
        );
        // 执行认证，并获取认证结果
        // authenticationManager 会调用 UserDetailsServiceImpl 对象的方法
        Authentication authenticateResult = authenticationManager.authenticate(authentication);
        log.debug("验证登陆完成！认证结果：{}", authenticateResult);

        // 从认证结果中取出所需的数据
        AdminDetails adminDetails = (AdminDetails) authenticateResult.getPrincipal();
        Collection<GrantedAuthority> authorities = adminDetails.getAuthorities();

        // 生成 JWT
        Map<String, Object> headerParam = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        headerParam.put("alg", "HS256");
        headerParam.put("typ", "JWT");
        claims.put("id", adminDetails.getId());
        claims.put("username", adminDetails.getUsername());
        claims.put("authoritiesJsonString", JSON.toJSONString(authorities) );
        String jwt = Jwts.builder()
                // Header
                .setHeaderParams(headerParam)
                // Payload
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + durationInMinute * 60 * 1000))
                // Verify signature
                .signWith(SignatureAlgorithm.HS256, secretKey)
                // 生成
                .compact();
        log.debug("生成了此管理员的信息对应的 JWT：{}", jwt);
        return jwt;

        //
        // 将认证结果存入到 SecurityContext 中
        // SecurityContext securityContext = SecurityContextHolder.getContext();
        // securityContext.setAuthentication(authenticateResult);
    }

    @Override
    public void addNew(AdminAddNewParam adminAddNewParam) {
        log.debug("开始处理【添加管理员】的业务，参数：{}", adminAddNewParam);
        // 检查管理员用户名是否被占用，如果被占用，则抛出异常
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", adminAddNewParam.getUsername());
        int countByUsername = adminMapper.selectCount(queryWrapper);
        log.debug("根据管理员用户名统计匹配的管理员数量，结果：{}", countByUsername);
        if (countByUsername > 0) {
            String message = "添加管理员失败，用户名已经被占用！";
            log.warn(message);
            throw new ServiceException(ServiceCode.ERR_CONFLICT, message);
        }




        // TODO 检查管理员手机号码是否被占用，如果被占用，则抛出异常
        // TODO 检查管理员电子邮箱是否被占用，如果被占用，则抛出异常
        // 对密码进行加密
        String encryptedPassword = passwordEncoder.encode(adminAddNewParam.getPassword());
        adminAddNewParam.setPassword(encryptedPassword);

        // 将管理员数据写入到数据库中
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminAddNewParam, admin);
        admin.setLastLoginIp(null);
        admin.setLoginCount(0);
        admin.setGmtLastLogin(null);
        admin.setGmtCreate(LocalDateTime.now());
        admin.setGmtModified(LocalDateTime.now());
        int rows = adminMapper.insert(admin);
        if (rows != 1) {
            String message = "添加管理员失败，服务器忙，请稍后再试！";
            log.warn(message);
            throw new ServiceException(ServiceCode.ERR_INSERT, message);
        }
        log.debug("将新的管理员数据插入到数据库，完成！");

        // 将管理员与角色的关联数据写入到数据库中
        Long[] roleIds = adminAddNewParam.getRoleIds();
        AdminRole[] adminRoleList = new AdminRole[roleIds.length];
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < adminRoleList.length; i++) {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(admin.getId());
            adminRole.setRoleId(roleIds[i]);
            adminRole.setGmtCreate(now);
            adminRole.setGmtModified(now);
            adminRoleList[i] = adminRole;
        }
        rows = adminRoleMapper.insertBatch(adminRoleList);
        if (rows != adminRoleList.length) {
            String message = "添加管理员失败，服务器忙，请稍后再试！";
            log.warn(message);
            throw new ServiceException(ServiceCode.ERR_INSERT, message);
        }
        log.debug("将新的管理员与角色的关联数据插入到数据库，完成！");
    }

    @Override
    public AdminLoginInfoVO getLoginInfoByUsername(String username) {
        AdminLoginInfoVO adminLoginInfoVO = adminMapper.getLoginInfoByUsername(username);
        return adminLoginInfoVO;
    }

    @Override
    public void delete(Integer id) {
        adminMapper.deleteById(id);
    }

    @Override
    public void updatePassword(String password, Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new ServiceException(ServiceCode.ERR_NOT_FOUND, "账号不存在！");
        }
        admin.setPassword(passwordEncoder.encode(password));
        adminMapper.updateById(admin);
//        adminMapper.updateById();
    }

    @Override
    public void updateInfo(AdminUpdateInfoParam adminUpdateInfoParam, Long id) {
        log.debug("开始处理【更改管理员信息】的业务，参数：{}", adminUpdateInfoParam);
/*************************************************************************************************/
        QueryWrapper<Admin> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("username", adminUpdateInfoParam.getUsername());
        int countByUsername = adminMapper.selectCount(queryWrapper1);
        log.debug("根据管理员用户名统计匹配的管理员数量，结果：{}", countByUsername);

        if (countByUsername == 0) {
            String message = "更改管理员信息失败，用户名不存在！";
            log.warn(message);
            throw new ServiceException(ServiceCode.ERR_NOT_FOUND, message);
        }
/*************************************************************************************************/
        // 从数据库中查找与欲修改的用户名相同的匹配项
        Admin admin = adminMapper.selectById(id);

        // 从数据库中查找与提交修改信息手机号相同的匹配项
        QueryWrapper<Admin> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("phone", adminUpdateInfoParam.getPhone());
        int countByPhone = adminMapper.selectCount(queryWrapper2);

        log.debug("根据管理员手机号统计匹配的管理员数量，结果：{}", countByPhone);

        if (countByPhone == 0 ? false : !adminUpdateInfoParam.getPhone().equals(admin.getPhone())) {
            String message = "更改管理员信息失败，与其他已有管理员手机号码相同！";
            log.warn(message);
            throw new ServiceException(ServiceCode.ERR_CONFLICT, message);
        }

/*************************************************************************************************/
        QueryWrapper<Admin> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("email", adminUpdateInfoParam.getEmail());
        int countByEmail = adminMapper.selectCount(queryWrapper3);

        log.debug("根据管理员邮箱地址统计匹配的管理员数量，结果：{}", countByEmail);

        if (countByEmail == 0 ? false : !adminUpdateInfoParam.getEmail().equals(admin.getEmail())) {
            String message = "更改管理员信息失败，与其他已有管理员邮箱地址相同！";
            log.warn(message);
            throw new ServiceException(ServiceCode.ERR_CONFLICT, message);
        }

/*************************************************************************************************/
        Admin admin_tmp = new Admin();
        BeanUtils.copyProperties(adminUpdateInfoParam, admin_tmp);
        admin_tmp.setId(id);
        adminMapper.updateById(admin_tmp);
    }

    @Override
    public void selectById(Long id) {
        AdminUpdateInfoVO adminUpdateInfoVO = (AdminUpdateInfoVO) adminMapper.selectById(id);
    }
}