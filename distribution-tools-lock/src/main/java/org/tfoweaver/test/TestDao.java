package org.tfoweaver.test;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @title: TestDao
 * @Author Star_Chen
 * @Date: 2021/12/24 9:04
 * @Version 1.0
 */
public interface TestDao {

    /**
     * @Date: 2021/12/24 9:04
     * @Author: Star_Chen
     * @Param: * @param null
     * @Description:
     * @Return: {@link null}
     */
    List<Users> getUserList();

    Users getUserById(@Param("name") String name);


    int update(Users users);

    int insert(Users users);
}
