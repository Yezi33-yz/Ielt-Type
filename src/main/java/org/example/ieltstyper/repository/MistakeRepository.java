package org.example.ieltstyper.repository;

import org.example.ieltstyper.entity.Mistake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MistakeRepository extends JpaRepository<Mistake, Long> {

    // 1. 判断这个用户是否已经错过了这个词 (用于：防止同一个词被重复塞进错词本)
    boolean existsByUsernameAndWordId(String username, Long wordId);

    // 2. 查到这个人的所有错词 (用于：错词本页面列表渲染)
    List<Mistake> findByUsername(String username);

    // 3. 【新增】：斩杀错词！(用于：连对 3 次后从数据库彻底抹杀)
    @Transactional // 删除操作必须加事务注解
    void deleteByUsernameAndWordId(String username, Long wordId);
}