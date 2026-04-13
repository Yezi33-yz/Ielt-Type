package org.example.ieltstyper.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "word", indexes = {
        @Index(name = "idx_book_sublist", columnList = "book_name, sublist"), // ✅ 加索引，按词书+频率查询更快
        @Index(name = "idx_spelling", columnList = "spelling")
})
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String spelling;        // 单词拼写

    @Column(length = 100)
    private String phonetic;        // 音标

    @Column(nullable = false)
    private String meaning;         // 中文释义

    @Column(name = "part_of_speech", length = 30)
    private String partOfSpeech;    // 词性

    @Column(length = 500)
    private String example;         // 例句

    private Integer sublist;        // 子列表/难度（AWL用1-10，其他词书可自定义）

    @Column(name = "book_name", nullable = false, length = 100)
    private String bookName;        // 所属词书（见下方词书命名规范）
}