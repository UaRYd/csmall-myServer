package cn.tedu.csmall.passport.controller;

import cn.tedu.csmall.passport.pojo.param.AdminAddNewParam;
import cn.tedu.csmall.passport.pojo.param.AdminLoginInfoParam;
import cn.tedu.csmall.passport.pojo.param.AdminUpdateInfoParam;
import cn.tedu.csmall.passport.pojo.vo.AdminListItemVO;
import cn.tedu.csmall.passport.pojo.vo.PageData;
import cn.tedu.csmall.passport.security.LoginPrincipal;
import cn.tedu.csmall.passport.service.IAdminService;
import cn.tedu.csmall.passport.web.JsonResult;
import cn.tedu.csmall.passport.web.ServiceCode;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Api(tags = "01. 管理员管理模块")
@Slf4j
@Validated
public class AdminController {

    @Autowired
    private IAdminService adminService;

    // http://localhost:9181/admin/login
    @PostMapping("/login")
    @ApiOperation("管理员登陆")
    @ApiOperationSupport(order = 50)
    public JsonResult login(AdminLoginInfoParam adminLoginInfoParam) {
        log.debug("开始处理【管理员登陆】的请求，参数{}", adminLoginInfoParam);
        String jwt = adminService.login(adminLoginInfoParam);
        return JsonResult.ok(jwt);
    }

    // http://localhost:8080/admin/add-new
    @PostMapping("/add-new")
    @ApiOperation("添加管理员")
    @ApiOperationSupport(order = 100)
    public JsonResult addNew(AdminAddNewParam adminAddNewParam) {
        log.debug("开始处理【添加管理员】的请求，参数：{}", adminAddNewParam);
        adminService.addNew(adminAddNewParam);
        return JsonResult.ok();
    }

    // http://localhost:9081/admin/list
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('/ams/admin/read')")
    @ApiOperation("查询管理员列表")
    @ApiOperationSupport(order = 420)
    public JsonResult list(Integer page) {
//        log.debug("当事人 ID：{}", loginPrincipal.getId());
//        log.debug("当事人用户名：{}", loginPrincipal.getUsername());
        PageData<AdminListItemVO> pageData = adminService.list(page);
        return JsonResult.ok(pageData);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('/ams/admin/delete')")
    @ApiOperation("删除管理员列表")
    @ApiOperationSupport(order = 520)
    public JsonResult delete(Integer id) {
        adminService.delete(id);
        return JsonResult.ok("删除管理员成功！");
    }

    @PostMapping("/updatePassword")
    @PreAuthorize("hasAuthority('/ams/admin/update')")
    @ApiOperation("修改管理员密码")
    @ApiOperationSupport(order = 620)
    public JsonResult updatePassword(String password, Long id) {
        adminService.updatePassword(password, id);
        return JsonResult.ok("修改管理员密码成功！");
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('/ams/admin/update')")
    @ApiOperation("修改管理员信息")
    @ApiOperationSupport(order = 620)
    public JsonResult updateInfo(AdminUpdateInfoParam adminUpdateInfoParam, Long id) {
        adminService.updateInfo(adminUpdateInfoParam, id);
        return JsonResult.ok("修改管理员信息成功！");
    }

    @PostMapping("/standard")
    @PreAuthorize("hasAuthority('/ams/admin/read')")
    @ApiOperation("修改管理员信息")
    @ApiOperationSupport(order = 620)
    public JsonResult selectById(Long id) {
        adminService.selectById(id);
        return JsonResult.ok("修改管理员信息成功！");
    }
}