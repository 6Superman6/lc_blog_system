package demo.mapper;

import demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    // 登录（根据用户名和密码获取User信息）
    User findByUsernameAndPassword(String username,String password);

    // 检验用户名是否存在
    User findByUsername(String username);

    // 检验昵称是否存在
    User findByNickname(String nickname);

    // 获取用户列表
    @Query("select b from Blog b")
    List<User> findAll();

    // 获取id个数
    @Query("select count(b) from Blog b where b.id = ?1")
    int getCountById(Long id);

    // 更新用户权限
    @Transactional
    @Modifying
    @Query("update Blog b set b.type = ?1 where b.id = ?1")
    int updateType(Long id,Integer type);

}
