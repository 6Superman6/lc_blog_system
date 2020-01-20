package demo.service;

import demo.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {

    // 查询所有的标签（按更新时间排序）
    List<Tag> listTagTop(Integer size);

    // 直接查询所有的标签，查询结果都放到一个page页面中
    Page<Tag> listTag(Pageable pageable);

    // 根据id获取tag标签
    Tag getTag(Long id);

    // 根据tag名称获取获取Tag
    Tag getTagByName(String name);

    // 添加Tag
    Tag saveTag(Tag tag);

    // 更新Tag
    Tag updatTag(Long id,Tag tag);

    // 删除Tag
    void deleteTag(Long id);

    // findAll
    List<Tag> listTag();

    // 根据id获取指定的Tag标签（ids代表多个tagid，所以需要返回多个tag标签）
    List<Tag> listTag(String ids);

}
