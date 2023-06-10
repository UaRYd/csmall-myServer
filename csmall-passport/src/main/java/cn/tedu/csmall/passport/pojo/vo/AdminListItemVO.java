package cn.tedu.csmall.passport.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class AdminListItemVO implements Serializable {
    private Long id;
    private String nickname;
    private String description;
    private String avatar;
}
