package org.example.ieltstyper.repository;


import org.example.ieltstyper.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/*
什么是 JpaRepository<Word, Long>？
你可以把它理解为一个**“全自动的数据库工具箱”**。

JpaRepository: 这是 Spring 框架提供的一个内置接口。只要你继承了它，你的 WordRepository 就立刻拥有了各种“超能力”，比如保存单词 (save)、删除单词 (delete)、查找所有单词 (findAll) 等。你一行 SQL 代码都不用写，Spring 就帮你写好了。

泛型 <Word, Long>:

Word: 告诉工具箱，这个仓库是专门用来装 Word 实体（即对应 word 表）的。

Long: 告诉工具箱，Word 实体的主键（ID）类型是 Long。

一句话总结： 继承它，是为了让 Spring 自动帮你处理那些枯燥的“增删改查”工作。
 */
@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    //随机获取一个单词的“黑科技”逻辑
    /*
    JpaRepository 给了你很多基础工具，但它不知道你具体的业务需求（比如“随机抽一个单词”）。这时候你就需要**“自定义定制”**。

我们拆开看这段代码： @Query(value = "SELECT * FROM word ORDER BY RAND() LIMIT 1", nativeQuery = true)

value = "...": 这是真正的 SQL 语句。

SELECT * FROM word: 从 word 表里选中所有字段。

ORDER BY RAND(): 关键点！ 让数据库给结果随机排个序。

LIMIT 1: 只取第一条。

合起来就是：从库里随机抽一个单词。

nativeQuery = true:

这是一个“翻译开关”。

默认情况下，Spring 使用的是一种叫 JPQL 的语言（面向对象的）。

设置 true，意味着告诉 Spring：“不要翻译，直接用我写的原生 MySQL 语法去运行”。

一句话总结： 这是在给工具箱增加一个“特制功能”，告诉数据库如何按我们的要求（随机）选出数据。
     */
    @Query(value = "SELECT * FROM word ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Word findRandomWord();

    // 艾宾浩斯复习查询：查找该用户 到达复习时间 的单词
    @Query("SELECT w FROM Word w JOIN UserWordProgress p ON w.id = p.wordId " +
            "WHERE p.username = :username AND p.nextReviewTime <= :now " +
            "ORDER BY p.nextReviewTime ASC")
    List<Word> findNeedReviewWords(@Param("username") String username,
                                   @Param("now") LocalDateTime now,
                                   Pageable pageable);

    @Query(value = "SELECT * FROM word WHERE book_name = :book ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Word> findRandomByBook(@Param("book") String book, @Param("count") int count);





    @Query(value = "SELECT * FROM word ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Word> findRandom(@Param("count") int count);


    // WordRepository.java
    @Query(value = "SELECT book_name AS bookName, COUNT(*) AS totalWords FROM word GROUP BY book_name", nativeQuery = true)
    List<Map<String, Object>> findBookStats();


    @Query(value = "SELECT COUNT(*) FROM word WHERE book_name = :bookName", nativeQuery = true)
    int countByBookName(@Param("bookName") String bookName);
}
