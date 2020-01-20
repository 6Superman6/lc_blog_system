package demo.controller.admin;

import demo.model.User;
import demo.service.UserService;
import demo.utils.ImaTool;
import demo.utils.MD5Utils;
import demo.utils.ServerResponse;
import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;

@Controller
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/in")
    public String login2()
    {
        return "admin/login";
    }

    // 跳转到登录页面
    @GetMapping
    public String loginPage(HttpSession session,RedirectAttributes attributes)
    {
        User user = null;
        user = (User)session.getAttribute("user");
//        System.out.println("user: "+user);
        if(user!=null&&user.getType()==1)
        {
            return "redirect:/admin/index";
        }
        else if(user!=null&&user.getType()==0)
        {
            attributes.addFlashAttribute("message","用户您好，您没有管理员权限");
            return "redirect:/admin/in";
        }
        return "admin/login";

    }

    // 跳转到注册页面
    @GetMapping("/reg")
    public String registerPage()
    {
        return "admin/register";
    }

    // 跳转到后台首页
    @GetMapping("/index")
    public String indexPage()
    {
        return "admin/index";
    }

    // 登录
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session,
                        RedirectAttributes attributes)
    {
        User user2 = null;
        user2 = (User)session.getAttribute("user");
//        System.out.println("user: "+user2);
//        System.out.println("username: "+username+" "+"password: "+password);
        String pw = MD5Utils.code(password);
        User user = userService.checkUser(username,pw);
        if(user2!=null&&user2.getType()==1)
        {
            return "redirect:/admin/index";
        }
        else if(user2!=null&&user2.getType()==0&&user!=null&&user.getType()==0)
        {
            attributes.addFlashAttribute("message","用户您好，您没有管理员权限");
            return "redirect:/admin";   // 访问接口/admin方法
        }

//        System.out.println("user:"+user);
        if (user!=null&&user.getType()==0)
        {
            attributes.addFlashAttribute("message","用户您好，您没有管理员权限");
            return "redirect:/admin";

        }
        else if(user!=null)
        {
            session.setAttribute("user",user);
            return "redirect:/admin/index";
        }
        else
        {
            attributes.addFlashAttribute("message","用户名和密码错误");
            return "redirect:/admin";   // 访问接口/admin方法
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
                return "redirect:/admin/reg";
            }
            user.setAvatar("/uploads/"+filename);
        }
        ServerResponse response = userService.saveUser(user);
        attributes.addFlashAttribute("message",response.getMsg());
        return "redirect:/admin/reg";
    }

    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session)
    {
        session.setAttribute("user",null);
        session.removeAttribute("user");
//        System.out.println("退出登录");
        return "redirect:/admin";  // 访问接口/admin方法
    }

    // 跳转到修改页面
    @GetMapping("/up")
    public String up()
    {
        return "admin/up";
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
            return "redirect:/admin/up";
        }
        if (nickname!=null&&nickname.length()!=0)
        {
            user.setNickname(nickname);
        }
        if(!user.getUsername().equals(username)&&!userService.checkUsername(username))
        {
            attributes.addFlashAttribute("message","该用户名已经被占用了");
            return "redirect:/admin/up";
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
                return "redirect:/admin/up";
            }
            user.setAvatar("/uploads/"+filename);
        }
        user.setUpdateTime(new Date());
        ServerResponse serverResponse = userService.updateUser(user);
//        System.out.println("msg: "+serverResponse.getMsg());
        attributes.addFlashAttribute("message",serverResponse.getMsg());
        return "redirect:/admin/up";
    }

    // 跳转到修改用户权限页面
    @GetMapping("/power")
    public String power(HttpSession session, RedirectAttributes attributes)
    {
        User user = null;
        user = (User) session.getAttribute("user");
        if(user==null)
        {
            return "redirect:/admin";
        }
        if(user.getType()==0)
        {
            attributes.addFlashAttribute("power","只有管理员才拥有此权限");
            return "redirect:/admin/index";
        }
        return "admin/power";
    }

    @GetMapping("/backup")
    public String backup()
    {
        return "admin/power";
    }

    // 修改用户权限
    @PostMapping("/uppower")
    public String uppower(@RequestParam String username,@RequestParam Integer type,HttpSession session,RedirectAttributes attributes)
    {
        User user = null;
        user = userService.getUserByUsername(username);
        if (user==null)
        {
            attributes.addFlashAttribute("message","该用户不存在，请重新输入用户名");
            return "redirect:/admin/backup";
        }
        user.setType(type);
        ServerResponse serverResponse = userService.updateUser(user);
//        System.out.println("msg: "+serverResponse.getMsg());
        attributes.addFlashAttribute("message",serverResponse.getMsg());
        return "redirect:/admin/backup";

    }


}
