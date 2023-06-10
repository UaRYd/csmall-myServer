package cn.tedu.csmall.passport.service;

import cn.tedu.csmall.passport.pojo.param.AdminAddNewParam;
import cn.tedu.csmall.passport.pojo.param.AdminLoginInfoParam;
import cn.tedu.csmall.passport.pojo.param.AdminUpdateInfoParam;
import cn.tedu.csmall.passport.pojo.vo.AdminListItemVO;
import cn.tedu.csmall.passport.pojo.vo.AdminLoginInfoVO;
import cn.tedu.csmall.passport.pojo.vo.PageData;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IAdminService {

    String login(AdminLoginInfoParam adminLoginInfoParam);

    void addNew(AdminAddNewParam adminAddNewParam);

    AdminLoginInfoVO getLoginInfoByUsername(String username);

    PageData<AdminListItemVO> list(Integer pageNum);

    void delete(Integer id);

    void updatePassword(String password, Long id);

    void updateInfo(AdminUpdateInfoParam adminUpdateInfoParam, Long id);

    void selectById(Long id);
}
