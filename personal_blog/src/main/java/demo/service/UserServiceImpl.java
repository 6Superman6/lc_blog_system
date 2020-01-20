package demo.service;

import demo.mapper.UserRepository;
import demo.model.User;
import demo.utils.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.soap.SOAPBinding;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {
        User user = null;
        user = userRepository.findByUsernameAndPassword(username,password);
        return user;
    }

    @Override
    public ServerResponse saveUser(User user) {
        User user1 = null;
        User user2 = null;
        User user3 = null;
        user3 = userRepository.findByNickname(user.getNickname());
        if (user3!=null)
        {
            return ServerResponse.createByError(-1,"该昵称已经被注册，请重新注册");
        }
        user2 = userRepository.findByUsername(user.getUsername());
        if (user2!=null)
        {
            return ServerResponse.createByError(-1,"该用户名已经被注册，请重新注册");
        }
        user1 = userRepository.save(user);
//        System.out.println("user1 : "+user1);
        if (user1==null)
        {
            return ServerResponse.createByError(-1,"注册失败，请重新注册");
        }
        return ServerResponse.createBySuccessMsg("注册成功，请登录");

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public ServerResponse updateUser(User user) {
        User user1 = null;
        user1 = userRepository.save(user);
        if (user1==null)
        {
            return ServerResponse.createByError("更新失败");
        }
        return ServerResponse.createBySuccessMsg("更新成功");
    }

    @Override
    public User findUser(Long id) {

        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public int getCountById(Long id) {
        return 0;
    }

    @Override
    public int updateType(Long id, Integer type) {
        return 0;
    }

    @Override
    public boolean checkNickname(String nickname) {
        User user = null;
        user = userRepository.findByNickname(nickname);
        if (user!=null)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkUsername(String username) {
        User user = null;
        user = userRepository.findByUsername(username);
        if (user!=null)
        {
            return false;
        }
        return true;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = null;
        user =  userRepository.findByUsername(username);
        return user;
    }
}
