package demo.controller.admin;

import demo.model.Type;
import demo.service.TypeService;
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
public class TypeController {

    @Autowired
    private TypeService typeService;

    // 跳转到分类页面
    @GetMapping("/types")
    public String types(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model)
    {
//        System.out.println("pageable："+pageable);  // print: Page request [number: 0, size 10, sort: id: DESC]
        model.addAttribute("page",typeService.listType(pageable));  // 把所有的博客都放到一个page页面中
        return "admin/types";
    }

    // 新增分类
    @GetMapping("/types/input")
    public String input(Model model)
    {
        model.addAttribute("type",new Type());
        return "admin/types-input";
    }

    // 编辑某一个分类,进入编辑页面
    @GetMapping("/types/{id}/input")
    public String addType(@PathVariable Long id,Model model)
    {
        model.addAttribute("type",typeService.getType(id));
        return "admin/types-input";
    }

    // 删除某一个分类
    @GetMapping("/types/{id}/delete")
    public String deleteType(@PathVariable Long id, RedirectAttributes attributes)
    {
        typeService.deleteType(id);
        attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/types";
    }

    // 添加分类（id==null的情况）
    @PostMapping("/types")
    public String add1(@Valid Type type, BindingResult result,RedirectAttributes attributes)  // 使用BindingResult校验字段
    {
        Type type1 = typeService.getTypeByName(type.getName());
        if(type1!=null)
        {
            result.rejectValue("name","nameError","不能添加重复的分类");
        }
        if (result.hasErrors())
        {
            return "admin/types-input";
        }
        Type t = typeService.saveType(type);
        if (t==null)
        {
            attributes.addFlashAttribute("message","新增失败");
        }
        else
        {
            attributes.addFlashAttribute("message","新增成功");
        }
        return "redirect:/admin/types";
    }

    // 更新分类（id!=null的情况，因为需要id来更新）
    @PostMapping("/types/{id}")
    public String add2(@Valid Type type,BindingResult result,@PathVariable Long id,RedirectAttributes attributes)
    {
        Type type1 = typeService.getTypeByName(type.getName());
        if(type1!=null)
        {
            result.rejectValue("name","nameError","不能添加重复的分类");
        }
        if (result.hasErrors())
        {
            return "admin/types-input";
        }
        Type t = typeService.updateType(id,type);
        if (t==null)
        {
            attributes.addFlashAttribute("message","更新失败");
        }
        else
        {
            attributes.addFlashAttribute("message","更新成功");
        }
        return "redirect:/admin/types";
    }



}
