package org.example.ieltstyper.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_name", unique = true, nullable = false)
    private String bookName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    private String description;
    private String color;

    @Column(name = "cover_text")
    private String coverText;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Transient
    private Integer totalWords;

    @Transient
    private Integer learnedCount = 0;
}