package demo.controller;

import demo.model.Blog;
import demo.model.Tag;
import demo.model.Type;
import demo.service.BlogService;
import demo.service.TagService;
import demo.service.TypeService;
import demo.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;


    @RequestMapping("/")
    public String index(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model)
    {
//        System.out.println("969666666666666666666666666666666666666666666666");
        model.addAttribute("page",blogService.listBlog(pageable));     // 统计所有博客的数量
        model.addAttribute("types",typeService.listTypeTop(6));  // 统计所有分类的数量
        model.addAttribute("tags",tagService.listTagTop(10));    // 统计所有标签的数量
        // index.html中有一个博客推荐。recommendBlogs存放的博客都是允许被推荐的博客
        model.addAttribute("recommendBlogs",blogService.listRecommendBlogTop(8)); //根据更新博客的时间推荐前8篇博客（因为设置的默认值是8）
        return "index";
    }

    // 查看单个博客信息
    @GetMapping("/blog/{id}")
    public String getBlogByid(@PathVariable Long id,Model model)
    {
//        System.out.println("/blog/{id}: "+id);
        Blog blog = null;
        blog = blogService.getBlogAndConvert(id); // 获取blog（content内容已由markdown格式转化为html格式）
        model.addAttribute("blog",blog);
        return "blog";
    }

    // 前台主页搜索
    @PostMapping("/search")
    public String search(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam String query,Model model)
    {
        model.addAttribute("query",query);
        query = "%"+query+"%";
        model.addAttribute("page",blogService.listBlog(query,pageable));
        return "search";
    }

    // 查看所有的博客类型(以及该博客类型所对应的博客)
   @GetMapping("/types/{id}")
    public String types(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model,@PathVariable Long id)
   {
       List<Type> list = typeService.listTypeTop(10000);  // 查看所有博客类型
//       System.out.println("66666666666666666666666666size: "+list.size());
       if(id==-1)  // 初次点开分类页面（默认显示第一个分类以及它所对应的博客）
       {
           id = list.get(0).getId(); // 第一个分类的id
       }
       BlogQuery blogQuery = new BlogQuery();
       blogQuery.setTypeId(id);
       model.addAttribute("types",list);                                    // 默认显示的是第一个分类所对应的博客
       model.addAttribute("page",blogService.listBlog(pageable,blogQuery)); // 根据博客typeid获取相应的博客（动态查询）
       model.addAttribute("activeTypeId",id);
       return "types";
   }

   // 查看所有的标签（以及每个标签所对应的博客有哪些）
    @GetMapping("/tags/{id}")
    public String tags(@PageableDefault(size = 8,sort = "updateTime",direction = Sort.Direction.DESC) Pageable pageable,
                       @PathVariable Long id,Model model)
    {
        List<Tag> list = tagService.listTagTop(10000);
        if(id==-1)  // 初次点开标签页面（默认显示的是第一个标签）
        {
            id = list.get(0).getId();
        }
        model.addAttribute("tags",list);                                 // 查询条件：该tagId所对应的博客
        model.addAttribute("page",blogService.listBlog(id,pageable));  // 默认显示的是第一个tagid所对应的博客
        model.addAttribute("activeTagId",id);
        return "tags";
    }





}
