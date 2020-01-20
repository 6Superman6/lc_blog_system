package demo.utils;

import demo.model.Comment;
import demo.model.User;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取一个类中空属性数组
 */
public class MyBeanUtils {


    /**
     * 获取所有的属性值为空属性名数组,例如User类中有9个属性（不包括list）为空，返回一个数组(长度为9)
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds =  beanWrapper.getPropertyDescriptors();
        List<String> nullPropertyNames = new ArrayList<>();
        for (PropertyDescriptor pd : pds) {
            String propertyName = pd.getName();
            if (beanWrapper.getPropertyValue(propertyName) == null) {
                nullPropertyNames.add(propertyName);
            }
        }
        return nullPropertyNames.toArray(new String[nullPropertyNames.size()]);
    }

    public static void main(String[] args)
    {
        User user = new User();
        Comment comment = new Comment();
        System.out.println(getNullPropertyNames(user).length);  // 9
        System.out.println(getNullPropertyNames(comment).length);// 8
        user.setId(10L);
        user.setUsername("666");
        System.out.println(getNullPropertyNames(user).length);  // 7（9个属性有7个为空）
        comment.setAvatar("6666");
        System.out.println(getNullPropertyNames(comment).length); // 7(8个属性有7个为空)
    }

}
