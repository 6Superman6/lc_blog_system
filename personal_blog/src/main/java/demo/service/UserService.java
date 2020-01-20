package demo.service;

import demo.model.User;
import demo.utils.ServerResponse;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserService {

    // 登录验证
    User checkUser(String username,String password);

    // 增
    ServerResponse saveUser(User user);

    // 删
    void deleteUser(User user);

    // 删
    void deleteUser(Long id);

    // 改
    ServerResponse updateUser(User user);

    // 查
    User findUser(Long id);

    List<User> findAll();

    // 获取id个数
    int getCountById(Long id);

    // 更新用户权限
    int updateType(Long id,Integer type);

    // 检验昵称
    boolean checkNickname(String nickname);
    // 检验用户名
    boolean checkUsername(String username);

    // 根据用户名获取User
    User getUserByUsername(String username);

}
