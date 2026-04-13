package org.example.ieltstyper.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // 自动生成 Getter, Setter, toString
@NoArgsConstructor // Lombok 帮你生成无参构造函数
@Table(name = "mistakes")
public class Mistake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    // 注意这里！必须是 Long，因为你的 Word 类的 id 是 Long
    private Long wordId;

    // 自定义一个带参构造函数，方便我们在 Service 里直接 new 对象
    public Mistake(String username, Long wordId) {
        this.username = username;
        this.wordId = wordId;
    }
}