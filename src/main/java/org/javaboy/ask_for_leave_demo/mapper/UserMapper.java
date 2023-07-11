package org.javaboy.ask_for_leave_demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.javaboy.ask_for_leave_demo.model.Role;
import org.javaboy.ask_for_leave_demo.model.User;

import java.util.List;

/**
 * @author xyma
 * @version 1.0
 * @data 2023/7/10 15:18
 */
@Mapper
public interface UserMapper {
    User loadUserByUsername(String username);

    List<Role> getUserRolesByUserId(Integer id);

    List<User> getAllUsersExcludeCurrent(String name);
}
