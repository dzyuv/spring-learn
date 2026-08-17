package com.dzy.userservice8092.mapper;


import com.dzy.common.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, password_hash AS password, phone, role, status, " +
            "created_at AS createdAt, updated_at AS updatedAt FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT id, username, password_hash AS password, phone, role, status, " +
            "created_at AS createdAt, updated_at AS updatedAt FROM user WHERE id = #{id}")
    User selectById(Long id);

    @Insert("INSERT INTO user(username, password_hash, phone, role, status, created_at, updated_at) " +
            "VALUES(#{username}, #{password}, #{phone}, #{role}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET password_hash = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone} ")
    int countByPhone(String phone);

}
