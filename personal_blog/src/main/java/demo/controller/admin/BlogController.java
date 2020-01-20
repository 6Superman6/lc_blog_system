package demo.controller.admin;

import demo.model.Blog;
import demo.model.User;
import demo.service.BlogService;
import demo.service.TagService;
import demo.service.TypeService;
import demo.utils.ImaTool;
import demo.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class BlogController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @Autowired
    private BlogService blogService;

    private static final String INPUT = "admin/blogs-input";  // 跳转到新增博客页面
    private static final String LIST = "admin/blogs";          // 跳转到博客页面
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";  // 跳转到/admin/blogs接口

    // 跳转到博客页面（分页查询博客list）
    // pageable默认size为8，根据时间倒叙查询
    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC)Pageable pageable,
                        BlogQuery blog,Model model)
    {
        model.addAttribute("types",typeService.listType());
        // page存放的是所有的博客信息，listBlog(pageable,blog)中blog里面的内容全为null，
        // 所以blogService中该方法中并没有加入任何条件，所以相当于调用的blogRepository.findAll();
        model.addAttribute("page",blogService.listBlog(pageable,blog));
        return LIST;
    }

    //  博客分页查询，不是搜索框查询（根据博客title、typeid、recommend组合查询）
    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size=8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blog,Model model)
    {
        // page存放的是条件组合查询的博客信息，listBlog(pageable,blog)中blog里面的内容不为null，
        // 所以blogService中该方法中加入了组合条件，所以相当于调用的blogRepository.findAll(条件);
        model.addAttribute("page",blogService.listBlog(pageable,blog));
        return "admin/blogs :: blogList";  // blogs.html页面中有一个blogList属性表
    }

    // 向model中加入types集合和tags集合属性
    private void setTypeAndTag(Model model)
    {
        model.addAttribute("types",typeService.listType());
        model.addAttribute("tags",tagService.listTag());
    }

    // 跳转到blogs-input.html页面
    @GetMapping("/blogs/input")
    public String toBlogs(Model model)
    {
        setTypeAndTag(model);
        model.addAttribute("blog",new Blog());
        return INPUT;  /// 跳转到新增博客页面
    }

    // 根据id获取一个Blog信息，为更新做准备
    @GetMapping("/blogs/{id}/input")
    public String startToUpdate(@PathVariable Long id,Model model)
    {
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        // 将数据库中tag表中的tagid都放到该博客blog中的tagIds字符串里面，以逗号分割
        blog.init();
//        System.out.println("init Ids: "+blog.getTagIds());
        model.addAttribute("blog",blog);
        return INPUT; // 跳转到新增博客页面

    }

    @GetMapping("/blogs/picture")
    public String reToInput()
    {
        return INPUT;
    }

    // 添加和更新博客
    @PostMapping("/blogs")
   public String upAndaddBlog(Blog blog, RedirectAttributes attributes, HttpSession session,
                              @RequestParam(value = "firstPicture2",required = false) MultipartFile file,
                              HttpServletRequest request)
    {
//        System.out.println("published: "+blog.isPublished());
//        System.out.println("blog : "+blog);
//        System.out.println("file: "+file);
        String filename = ImaTool.Imagetool(request,file);
        User user = (User) session.getAttribute("user");
        blog.setUser(user);
        blog.setType(typeService.getType(blog.getType().getId())); // 设置该博客类型id
        blog.setTags(tagService.listTag(blog.getTagIds()));        // 获取ids对应的所有标签
        Blog b;
        if(filename!=null&&!"".equals(filename)&&file!=null)
        {
            if((filename.endsWith(".png")||filename.endsWith(".jpg")))
            {
                blog.setFirstPicture("/uploads/"+filename);
            }
        }
        if(blog.getId()==null)  // 说明是添加
        {
            b = blogService.saveBlog(blog);
        }
        else  // 说明是更新
        {
            b = blogService.updateBlog(blog.getId(),blog);
        }
        if(b==null)
        {
            attributes.addFlashAttribute("message","操作失败");
        }
        else
        {
            attributes.addFlashAttribute("message","操作成功");
        }
        return REDIRECT_LIST;

    }

    // 删除博客
    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes)
    {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message","删除成功");
        return REDIRECT_LIST;
    }






}
