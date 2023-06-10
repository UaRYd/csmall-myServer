package cn.tedu.csmall.product.service;

import cn.tedu.csmall.product.ex.ServiceException;
import cn.tedu.csmall.product.pojo.param.AlbumAddNewParam;
import cn.tedu.csmall.product.pojo.param.AlbumUpdateInfoParam;
import cn.tedu.csmall.product.pojo.vo.AlbumStandardVO;
import cn.tedu.csmall.product.pojo.vo.PageData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AlbumServiceTests {

    @Autowired
    IAlbumService service;

    @Test
    void addNew() {
        AlbumAddNewParam albumAddNewParam = new AlbumAddNewParam();
        albumAddNewParam.setName("测试数据-00003");
        albumAddNewParam.setDescription("测试数据简介-00003");
        albumAddNewParam.setSort(99);

        try {
            service.addNew(albumAddNewParam);
            System.out.println("添加成功！");
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        } catch (Throwable e) {
            System.out.println("添加失败！出现了某种异常！");
            e.printStackTrace();
        }
    }

    @Test
    void delete() {
        Long id = 13L;
        try {
            service.delete(id);
            System.out.println("删除数据完成！");
        } catch (ServiceException e) {
            System.out.println(e.getServiceCode().getValue());
            System.out.println(e.getMessage());
        }
    }

    @Test
    void updateInfoById() {
        Long id = 1L;
        AlbumUpdateInfoParam albumUpdateInfoParam = new AlbumUpdateInfoParam();
        albumUpdateInfoParam.setName("华为Mate10的相册");
        albumUpdateInfoParam.setDescription("测试数据简介-33333");
        albumUpdateInfoParam.setSort(93);

        try {
            service.updateInfoById(id, albumUpdateInfoParam);
            System.out.println("修改成功！");
        } catch (ServiceException e) {
            System.out.println(e.getServiceCode().getValue());
            System.out.println(e.getMessage());
        } catch (Throwable e) {
            System.out.println("修改失败！出现了某种异常！");
            e.printStackTrace();
        }
    }

    @Test
    void getStandardById() {
        Long id = 1L;
        try {
            Object queryResult = service.getStandardById(id);
            System.out.println("根据【ID=" + id + "】查询数据完成，结果：" + queryResult);
        } catch (ServiceException e) {
            System.out.println(e.getServiceCode().getValue());
            System.out.println(e.getMessage());
        }
    }

    @Test
    void list() {
        Integer pageNum = 1;
        Integer pageSize = 10;
        PageData<?> pageData = service.list(pageNum, pageSize);
        List<?> list = pageData.getList();
        System.out.println("集合类型：" + list.getClass().getName());
        System.out.println("查询列表完成，结果集中的数据量：" + list.size());
        System.out.println("总记录数：" + pageData.getTotal());
        System.out.println("当前页码：" + pageData.getCurrentPage());
        System.out.println("最大页码：" + pageData.getMaxPage());
        System.out.println("每页记录数：" + pageData.getPageSize());
        for (Object item : list) {
            System.out.println(item);
        }
    }

}
