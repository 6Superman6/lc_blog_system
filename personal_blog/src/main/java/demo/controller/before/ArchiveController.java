package demo.controller.before;

import demo.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArchiveController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/archives")
    public String archives(Model model)
    {
        model.addAttribute("archiveMap",blogService.archiveBlog());   // 按年分类博客
        model.addAttribute("blogCount",blogService.countBlog());       // 所有博客的个数
        return "archives";
    }

}
