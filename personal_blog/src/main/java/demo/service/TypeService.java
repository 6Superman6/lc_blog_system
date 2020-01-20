package demo.service;

import demo.model.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TypeService {

    // 获取所有的type类型
    List<Type> listTypeTop(Integer size);

    // 获取type列表
    List<Type> listType();
    // 获取type列表，把type信息都放到一个page页面中
    Page<Type> listType(Pageable pageable);

    // 通过id获取type信息
    Type getType(Long id);

   // 通过id删除一个type信息
    void deleteType(Long id);

    // 通过typeName获取Type
    Type getTypeByName(String name);

    // 添加一个新分类type
    Type saveType(Type type);

    // 更新Type
    public Type updateType(Long id,Type type);




}
