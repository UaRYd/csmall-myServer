package cn.tedu.csmall.passport.pojo.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class AdminUpdateInfoParam implements Serializable {

    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private String description;
    private Integer enable;
}
