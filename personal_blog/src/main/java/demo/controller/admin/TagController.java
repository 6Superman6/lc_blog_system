package demo.controller.admin;

import demo.model.Tag;
import demo.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TagController {

    @Autowired
    private TagService tagService;

    // 跳转到tag.html页面
    // 把所有的标签都放到一个page页面中
    @GetMapping("/tags")
    public String tags(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC)Pageable pageable,
                       Model model)
    {
//        System.out.println("listTag(pageable): "+tagService.listTag(pageable)); //listTag(pageable): Page 1 of 1 containing demo.model.Tag instances
        model.addAttribute("page",tagService.listTag(pageable));  // 把所有的标签都放到一个page页面中
        return "admin/tags";
    }

    // 跳转到新增页面
    @GetMapping("/tags/input")
    public String tag_input(Model model)
    {
        model.addAttribute("tag",new Tag());
        return "admin/tags-input";  // 添加和更新在一个页面
    }

    // 根据id获取Tag标签，为更新做准备
    @GetMapping("/tags/{id}/input")
    public String getTag(@PathVariable Long id,Model model)
    {
        model.addAttribute("tag",tagService.getTag(id));
        return "admin/tags-input";
    }

    // 添加新的Tag标签
    @PostMapping("/tags")
    public String addTag(@Valid Tag tag, BindingResult result, RedirectAttributes attributes)
    {
        Tag tag1 = tagService.getTagByName(tag.getName());
        if(tag1!=null)
        {
            result.rejectValue("name","nameError","不能添加重复的标签");
        }
        if(result.hasErrors())
        {
            return "/admin/tags-input";  //添加
        }
        Tag tag2 = tagService.saveTag(tag);
        if (tag2==null)
        {
            attributes.addFlashAttribute("message","新增失败");
        }
        else
        {
            attributes.addFlashAttribute("message","新增成功");
        }
        return "redirect:/admin/tags";  // 跳转到tag页面

    }

    // 更新Tag标签
    @PostMapping("/tags/{id}")
    public String  updateTag(@Valid Tag tag,@PathVariable Long id,BindingResult result,RedirectAttributes attributes)
    {
        Tag tag1 = tagService.getTagByName(tag.getName());
        if(tag1!=null)
        {
            result.rejectValue("name","nameError","不能添加重复的类型");
        }
        if (result.hasErrors())
        {
            return "admin/tags-input";  // 更新
        }
        Tag t = tagService.updatTag(id,tag);
        if (t==null)
        {
            attributes.addFlashAttribute("message","更新失败");
        }
        else
        {
            attributes.addFlashAttribute("message","更新成功");
        }
        return "redirect:/admin/tags";  // 跳转到tag页面
    }

    // 删除一个Tag标签
    @GetMapping("/tags/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes)
    {
        tagService.deleteTag(id);
        attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/tags";  // 跳转到tag页面
    }

















}
