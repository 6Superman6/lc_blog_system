package demo.service;

import demo.NotFoundException;
import demo.mapper.TagRepository;
import demo.model.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<Tag> listTagTop(Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC,"blogs.size");
        Pageable pageable = new PageRequest(0,size,sort);
        return tagRepository.findTop(pageable);
    }

    @Override
    public Page<Tag> listTag(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public Tag getTag(Long id) {
        return tagRepository.findOne(id);
    }

    @Override
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    public Tag saveTag(Tag tag) {
        Tag tag1 = null;
        tag1 = tagRepository.save(tag);
        return tag1;
    }

    @Override
    public Tag updatTag(Long id, Tag tag) {
        Tag tag1 = null;
        tag1 = tagRepository.findOne(id);  // 先看看该id存不存在
        if(tag1==null)
        {
            throw new NotFoundException("不存在该标签");
        }
        BeanUtils.copyProperties(tag,tag1); // 属性复制
        return tagRepository.save(tag1);
    }

    @Override
    public void deleteTag(Long id) {
        tagRepository.delete(id);
    }

    @Override
    public List<Tag> listTag() {
        return tagRepository.findAll();
    }

    private List<Long> sToList(String ids){
//        System.out.println("ids: "+ids);
        List<Long> list = new ArrayList<>();
        if(!"".equals(ids)&&ids!=null)
        {
            String[] s = ids.split(",");
            for(int i=0,t= s.length;i<t;++i)
            {
                list.add(new Long(s[i]));
            }
        }
        return list;
    }

    // 根据多个id查询多个Tag标签，都放在一个集合里面
    @Override
    public List<Tag> listTag(String ids) {
        return tagRepository.findAll(sToList(ids));
    }
}
