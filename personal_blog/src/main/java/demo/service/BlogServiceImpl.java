package demo.service;

import demo.NotFoundException;
import demo.mapper.BlogRepository;
import demo.model.Blog;
import demo.model.Type;
import demo.model.User;
import demo.utils.MarkdownUtils;
import demo.utils.MyBeanUtils;
import demo.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

@Service
@Transactional
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC,"updateTime");
        Pageable pageable = new PageRequest(0,size,sort);
        return blogRepository.findTop(pageable);
    }

    // 后台管理员查看博客
    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll(new Specification<Blog>() {  // Specification实现动态查询
            // root代表你要查询的对象是哪一个、criteriaQuery代表查询的条件容器、
            // criteriaBuilder代表设置具体某一个条件的表达式（两个条件相等或者是模糊查询）
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();   // 条件集合
                if(!"".equals(blog.getTitle())&&blog.getTitle()!=null)  // 条件：title不为空
                {   // 模糊查询
                    predicates.add(criteriaBuilder.like(root.<String>get("title"),"%"+blog.getTitle()+"%"));  // 加入type模糊查询的条件
                }
                if(blog.getTypeId()!=null)  // 条件：博客的typeid不为null
                {   // 两条件是否相等                  root.get("type")获取的是该博客所属类型Type类（即private Type type; ）
                    predicates.add(criteriaBuilder.equal(root.<Type>get("type").get("id"),blog.getTypeId()));// 加入博客所属类型Type的typeid是否相等的条件
                }
                if(blog.isRecommend())  // 推荐开启
                {   // 两条件是否相等     加入博客是否可以被推荐的条件
                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"),blog.isRecommend()));
                }// criteriaQuery.where和sql语句中的where表达的意思差不多
                // criteriaQuery.where需要放一个条件数组，所以需要将predicates集合转化为predicates数组
                criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        },pageable);
    }

    // 我的博客
    @Override
    public Page<Blog> myBlog(Pageable pageable, BlogQuery blog,Long id) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();       // 条件容器
                if(!"".equals(blog.getTitle())&&blog.getTitle()!=null)
                {   // 加入模糊查询的条件
                    predicates.add(criteriaBuilder.like(root.<String>get("title"),"%"+blog.getTitle()+"%"));
                }
                if(blog.getTypeId()!=null)
                {   // 加入typeid匹配的条件
                    predicates.add(criteriaBuilder.equal(root.<Type>get("type").get("id"),blog.getTypeId()));
                }
                if(blog.isRecommend())
                {   // 加入recommend推荐匹配的条件
                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"),blog.isRecommend()));
                }
                if (id!=null)
                {   // 加入所属用户id匹配的条件
                    predicates.add(criteriaBuilder.equal(root.<User>get("user").get("id"),id));
                }   // 加入条件数组（需要将条件集合进行转换）
                criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        },pageable);
    }

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.findOne(id);
    }

    @Override
    public Blog saveBlog(Blog blog) {
        if(blog.getId()==null)  // 说明是新添加的
        {
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);  // 设置游览量
        }
        else // 因为有保存博客的功能，所以说执行插入时可能不是新添加的
        {
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }

    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog blog1 = blogRepository.findOne(id);
        if(blog1==null)
        {
            throw new NotFoundException("该博客不存在");
        }
        // 将blog对象中不是空的属性复制到原数据blog1对象中
        BeanUtils.copyProperties(blog,blog1, MyBeanUtils.getNullPropertyNames(blog));
        // MyBeanUtils.getNullPropertyNames(blog) 拿到该blog对象中空的属性
        blog1.setUpdateTime(new Date());
        return blogRepository.save(blog1);
    }

    @Override
    public void deleteBlog(Long id) {
        blogRepository.delete(id);
    }

    @Override
    public Blog getBlogAndConvert(Long id) {
        Blog blog = null;
        blog = blogRepository.findOne(id);
        if(blog==null)
        {
            throw new NotFoundException("该博客不存在");
        }
        Blog b = new Blog();
        BeanUtils.copyProperties(blog,b); // 不改变原数据的格式
        String content = b.getContent();  // 内容
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));  // 将markdown格式转化为html格式
        blogRepository.updateViews(id); // 游览量+1
        return b;

    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query,pageable);
    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Join join = root.join("tags");
                return criteriaBuilder.equal(join.get("id"),tagId);
            }
        },pageable);
    }

    // 按年归档博客
    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        Map<String,List<Blog>> map = new HashMap<>();
        for (String year : years)
        {
            map.put(year,blogRepository.findByYear(year));
        }
        return map;
    }

    // 获取博客总个数
    @Override
    public Long countBlog() {
        return blogRepository.count();   // 获取博客的总个数
    }


}
