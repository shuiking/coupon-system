package com.lk.merchantadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lk.merchantadmin.dao.entity.UserDO;
import com.lk.merchantadmin.service.UserService;
import com.lk.merchantadmin.dao.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author k
 * @description 针对表【t_user(商家用户表)】的数据库操作Service实现
 * @createDate 2024-08-11 15:21:26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO>
        implements UserService {

}




