package cn.ggstd;

import cn.ggstd.annotation.RpcServiceAno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RpcServiceAno
@Service
public class UserServiceImpl implements UserService {

    private static  Logger logger = LoggerFactory.getLogger(UserService.class);


    @Override
    public ApiResult<User> getUser(Long id) {
        logger.info("现在是【2】号提供服务");
        User user = new User(id,"lx",2,"www.2.com");
        return ApiResult.success(user);
    }
}
