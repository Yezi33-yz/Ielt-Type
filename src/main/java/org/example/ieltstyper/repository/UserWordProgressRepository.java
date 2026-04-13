package org.example.ieltstyper.repository;

import org.example.ieltstyper.entity.UserWordProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserWordProgressRepository extends JpaRepository<UserWordProgress, Long> {

    // 用于检查用户是否已经学过这个词
    Optional<UserWordProgress> findByUsernameAndWordId(String username, Long wordId);

    // 之前写的统计进度的查询
    @Query("SELECT COUNT(p) FROM UserWordProgress p JOIN Word w ON p.wordId = w.id " +
            "WHERE p.username = :username AND w.bookName = :bookName")
    int countLearnedWords(@Param("username") String username, @Param("bookName") String bookName);
}