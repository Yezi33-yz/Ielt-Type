package org.example.ieltstyper.repository;

import org.example.ieltstyper.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByOrderBySortOrderAsc();
}
