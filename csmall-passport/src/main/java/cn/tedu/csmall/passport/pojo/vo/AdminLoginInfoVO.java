package cn.tedu.csmall.passport.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class AdminLoginInfoVO implements Serializable {
    private Long id;
    private String username;
    private String password;
    private Integer enable;
    private List<String> permissions;
}
