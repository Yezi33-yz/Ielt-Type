package org.example.ieltstyper.repository;

import org.example.ieltstyper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    // 新增：一条 SQL 同时匹配用户名、邮箱、手机号
    @Query("SELECT u FROM User u WHERE u.username = :identifier " +
            "OR u.email = :identifier " +
            "OR u.phone = :identifier")
    User findByIdentifier(@Param("identifier") String identifier);
}
