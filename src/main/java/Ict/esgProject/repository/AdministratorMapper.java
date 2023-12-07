package Ict.esgProject.repository;

import Ict.esgProject.model.Administrator;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdministratorMapper {
    @Select("SELECT * FROM Administrator")
    List<Administrator> findAll();

    @Select("SELECT * FROM Administrator WHERE ( admin_email = #{adminEmail} )")
    Administrator findByEmail(String adminEmail);

    @Insert("INSERT INTO Administrator (admin_email,admin_name,admin_mobile,admin_sns)" +
            " VALUES (#{adminEmail},#{adminName},#{adminMobile},#{adminSns} ")
    void createAdministrator(Administrator administrator);
}
