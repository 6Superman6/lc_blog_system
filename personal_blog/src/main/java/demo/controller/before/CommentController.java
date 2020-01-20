package demo.controller.before;



import demo.model.Comment;
import demo.model.User;
import demo.service.BlogService;
import demo.service.CommentService;
import demo.service.UserService;
import demo.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

// 评论
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    // 在application.yml文件中定义了该属性（头像地址）
    @Value("${comment.avatar}")
    private String avatar;

    // 获取该博客所有的评论
    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model) {
        model.addAttribute("comments", commentService.listCommentByBlogId(blogId));
        return "blog :: commentList";
    }

    // 登录
    @RequestMapping(value = "/login2",method = RequestMethod.POST)
    public String login2(@RequestParam String username, @RequestParam String password, HttpSession session,
                         RedirectAttributes attributes)
    {
        if((User)session.getAttribute("user")!=null)
        {
            return "redirect:/before/index";
        }
        String pw = MD5Utils.code(password);
        User user = userService.checkUser(username,pw);
        if (user!=null)
        {
            session.setAttribute("user",user);
            Long blogid = (Long) session.getAttribute("bId");
            session.setAttribute("bId",null);
            session.removeAttribute("bId");
            return "redirect:/blog/"+blogid;
        }
        else
        {
            attributes.addFlashAttribute("message","用户名和密码错误");
            return "redirect:/before";   // 访问接口/admin方法
        }

    }

    // 添加评论
    @PostMapping("/comments")
    public String post(Comment comment, HttpSession session) {
        Long blogId = comment.getBlog().getId();
        User user = null;
        user = (User) session.getAttribute("user");
        if (user==null)
        {
            session.setAttribute("bId",blogId);
            return "login2";
        }
        comment.setBlog(blogService.getBlog(blogId));   // 设置所属博客
        comment.setUser(user);                            // 设置所属用户
        if (user != null) {
            comment.setAvatar(user.getAvatar());   // 设置头像
            comment.setAdminComment(true);         // 开启评论
        } else {
            comment.setAvatar(avatar);             // 设置头像
        }
        commentService.saveComment(comment);      // 添加
        return "redirect:/comments/" + blogId;
    }

    // 删除评论
    @GetMapping("/delcomment/{id}/{blId}")
    public String del(@PathVariable Long id,@PathVariable Long blId)
    {
        commentService.deleteComment(id);    // id     评论id（根据该id删除博客）
        return "redirect:/blog/"+blId;      // blId   博客id（需要返回到博客页面）
    }

}
