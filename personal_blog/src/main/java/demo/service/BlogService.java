package demo.service;

import demo.model.Blog;
import demo.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BlogService {

    // 把所有的博客都放到一个page页面中
    Page<Blog> listBlog(Pageable pageable);

    // 获取所有被允许推荐的博客（list中根据更新时间排序，但list中的个数只有默认值size个）
    public List<Blog> listRecommendBlogTop(Integer size);

    // 后台管理员查看博客
    // 博客分页查询，不是搜索框查询（根据博客title、typeid、recommend组合查询）
    Page<Blog> listBlog(Pageable pageable, BlogQuery blog);

    // 我的博客
    Page<Blog> myBlog(Pageable pageable,BlogQuery blog,Long id);

    // 根据id获取Blog
    Blog getBlog(Long id);

    // 添加博客
    Blog saveBlog(Blog blog);

    // 更新博客
    public Blog updateBlog(Long id,Blog blog);

    // 删除博客
    void deleteBlog(Long id);

    // 根据id获取blog并且将markdown格式转换为html格式
    Blog getBlogAndConvert(Long id);

    // 博客主页搜索（根据博客标题和内容进行模糊查询）
    Page<Blog> listBlog(String query,Pageable pageable);

    // 根据tagid查询所对应的博客
    Page<Blog> listBlog(Long tagId,Pageable pageable);

    // 博客按年归档
    public Map<String,List<Blog>> archiveBlog();

    // 获取博客的总个数
    public Long countBlog();



}
