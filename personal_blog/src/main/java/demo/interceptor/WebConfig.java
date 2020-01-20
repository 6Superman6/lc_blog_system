package demo.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
/*
    把登录拦截器加载到配置组件中去
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    /**
     *  拦截controller层方法、哪些不拦截
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/admin/**")           // 拦截/admin/** (拦截admin下的所有接口)
                .excludePathPatterns("/admin")          // 但不拦截/admin
                .excludePathPatterns("/admin/login")   // 也不拦截/admin/login(登录)
                .excludePathPatterns("/admin/reg")     // 也不拦截/admin/reg（跳转注册页面）
                .excludePathPatterns("/admin/register")   // 也不拦截/admin/register（注册）
                .excludePathPatterns("/before/blogs");    // 不拦截我的博客
    }
}
