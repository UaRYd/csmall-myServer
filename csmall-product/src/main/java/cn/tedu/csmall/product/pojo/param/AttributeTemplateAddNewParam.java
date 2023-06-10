package cn.tedu.csmall.product.pojo.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class AttributeTemplateAddNewParam implements Serializable {

    private String name;
    private String pinyin;
    private String keywords;
    private Integer sort;

}
