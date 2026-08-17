package com.dzy.userservice8092.mapper;

import com.dzy.userservice8092.entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

    @Insert("INSERT INTO address(user_id, receiver_name, phone, province, city, district, detail, is_default) " +
            "VALUES(#{userId}, #{receiverName}, #{phone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Address address);

    @Update("UPDATE address SET receiver_name=#{receiverName}, phone=#{phone}, province=#{province}, city=#{city}, " +
            "district=#{district}, detail=#{detail}, is_default=#{isDefault} WHERE id=#{id} AND user_id=#{userId}")
    int update(Address address);

    @Delete("DELETE FROM address WHERE id=#{id} AND user_id=#{userId}")
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);

    @Select("SELECT * FROM address WHERE user_id=#{userId} ORDER BY is_default DESC, id DESC")
    List<Address> selectByUserId(Long userId);

    @Select("SELECT * FROM address WHERE id=#{id} AND user_id=#{userId}")
    Address selectByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);

    @Select("SELECT * FROM address WHERE user_id=#{userId} AND is_default=1")
    Address selectDefaultByUserId(Long userId);

    @Update("UPDATE address SET is_default=0 WHERE user_id=#{userId}")
    int clearDefault(Long userId);

    @Update("UPDATE address SET is_default=1 WHERE id=#{id} AND user_id=#{userId}")
    int setDefault(@Param("id") Long id, @Param("userId") Long userId);

    // 查询用户地址总数（用于删除时判断）
    @Select("SELECT COUNT(*) FROM address WHERE user_id=#{userId}")
    int countByUserId(Long userId);
}