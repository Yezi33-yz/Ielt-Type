package org.example.ieltstyper.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity // 必须加这个注解，Repository 里的 @Query 才能认出它
@Data
public class UserWordProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private Long wordId;

    private Integer reviewStage = 0;
    private LocalDateTime nextReviewTime;
}
