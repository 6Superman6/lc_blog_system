package demo.controller.before;

import demo.model.User;
import demo.service.UserService;
import demo.utils.ImaTool;
import demo.utils.MD5Utils;
import demo.utils.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("/before")
public class BeforeUserController {

    @Autowired
    private UserService userService;

    // 跳转到登录页面
    @GetMapping
    public String loginPage(HttpSession session)
    {
        User user = null;
        user = (User)session.getAttribute("user");
        if(user!=null)
        {
            return "redirect:/before/index";
        }
        return "login";
    }

    // 跳转到注册页面
    @GetMapping("/reg")
    public String registerPage()
    {
        return "register";
    }

    // 跳转到前台首页
    @GetMapping("/index")
    public String indexPage()
    {
        return "redirect:/";
    }

    // 登录
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session,
                        RedirectAttributes attributes)
    {
        if((User)session.getAttribute("user")!=null)
        {
            return "redirect:/before/index";
        }
//        System.out.println("username: "+username+" "+"password: "+password);
        String pw = MD5Utils.code(password);
        User user = userService.checkUser(username,pw);
//        System.out.println("user:"+user);
        if (user!=null)
        {
            session.setAttribute("user",user);
            return "redirect:/before/index";
        }
        else
        {
            attributes.addFlashAttribute("message","用户名和密码错误");
            return "redirect:/before";   // 访问接口/admin方法
        }

    }

    // 注册
    @PostMapping("/register")
    public String register(@RequestParam("nickname") String nickname, @RequestParam("username") String username,
                           @RequestParam("password") String password, @RequestParam("email") String email,
                           @RequestParam(value = "type",defaultValue = "0",required = false) Integer type,
                           @RequestParam(value = "avatar",required = false) MultipartFile file,
                           HttpServletRequest request,RedirectAttributes attributes)
    {
        User user = new User();
        user.setNickname(nickname);
        user.setUsername(username);
        String pw = MD5Utils.code(password);
        user.setPassword(pw);
        user.setEmail(email);
        user.setType(type);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        String filename = ImaTool.Imagetool(request,file);
        if(filename!=null&&filename.length()!=0&&file!=null)
        {
            if(!(filename.endsWith(".png")||filename.endsWith(".jpg")))
            {
                attributes.addFlashAttribute("message","头像格式不正确，请重新上传头像");
                return "redirect:/before/reg";
            }
            user.setAvatar("/uploads/"+filename);
        }
        ServerResponse response = userService.saveUser(user);
        attributes.addFlashAttribute("message",response.getMsg());
        return "redirect:/before/reg";
    }

    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session)
    {
        session.setAttribute("user",null);
        session.removeAttribute("user");
        session.setAttribute("deletespoke",null);
        session.removeAttribute("deletespoke");
//        System.out.println("退出登录");
        return "redirect:/";  // 访问接口/admin方法
    }

    // 跳转到修改页面
    @GetMapping("/up")
    public String up()
    {
        return "up";
    }

    // 修改个人信息
    @PostMapping("/upUser")
    public String update(@RequestParam("nickname") String nickname, @RequestParam("username") String username,
                           @RequestParam("password") String password, @RequestParam("email") String email,
                           @RequestParam(value = "avatar",required = false) MultipartFile file,
                           HttpServletRequest request,RedirectAttributes attributes,HttpSession session)
    {
        User user = null;
        user = (User) session.getAttribute("user");
        if(!user.getNickname().equals(nickname)&&!userService.checkNickname(nickname))
        {
            attributes.addFlashAttribute("message","该昵称已经被占用了");
            return "redirect:/before/up";
        }
        if (nickname!=null&&nickname.length()!=0)
        {
            user.setNickname(nickname);
        }
        if(!user.getUsername().equals(username)&&!userService.checkUsername(username))
        {
            attributes.addFlashAttribute("message","该用户名已经被占用了");
            return "redirect:/before/up";
        }
        if(username!=null&&username.length()!=0)
        {
            user.setUsername(username);
        }
        if (password!=null&&password.length()!=0&&!user.getPassword().equals(password))
        {
            String pw = MD5Utils.code(password);
            user.setPassword(pw);
        }
        if(email!=null&&email.length()!=0)
        {
            user.setEmail(email);
        }
        String filename = ImaTool.Imagetool(request,file);
        if(filename!=null)
        {
            if(!(filename.endsWith(".png")||filename.endsWith(".jpg")))
            {
                attributes.addFlashAttribute("message","头像格式不正确，请重新上传头像");
                return "redirect:/before/up";
            }
            user.setAvatar("/uploads/"+filename);
        }
        user.setUpdateTime(new Date());
        ServerResponse serverResponse = userService.updateUser(user);
//        System.out.println("msg: "+serverResponse.getMsg());
        attributes.addFlashAttribute("message",serverResponse.getMsg());
        return "redirect:/before/up";
    }

}
