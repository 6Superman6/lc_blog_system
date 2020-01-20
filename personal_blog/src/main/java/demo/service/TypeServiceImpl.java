package demo.service;

import demo.NotFoundException;
import demo.mapper.TypeRepository;
import demo.model.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeRepository typeRepository;

    @Override
    public List<Type> listTypeTop(Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC,"blogs.size");
        Pageable pageable = new PageRequest(0,size,sort);
        return typeRepository.findTop(pageable);  // 获取所有的type标签
    }

    @Override
    public List<Type> listType() {
        return typeRepository.findAll();
    }

    @Override
    public Page<Type> listType(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Override
    public Type getType(Long id) {
        return typeRepository.findOne(id);
    }

    @Override
    public void deleteType(Long id) {
        typeRepository.delete(id);
    }

    @Override
    public Type getTypeByName(String name) {
        return typeRepository.findByName(name);
    }

    @Override
    public Type saveType(Type type) {
        Type type1 = null;
        type1 = typeRepository.save(type);
        return type1;
    }

    @Override
    public Type updateType(Long id, Type type) {
        Type t = typeRepository.findOne(id);
        if(t==null)
        {
            throw new NotFoundException("不存在该类型");
        }
        BeanUtils.copyProperties(type,t);
        return typeRepository.save(t);
    }


}
