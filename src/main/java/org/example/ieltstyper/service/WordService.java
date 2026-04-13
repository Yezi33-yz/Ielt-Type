package org.example.ieltstyper.service;

import org.example.ieltstyper.entity.Book;
import org.example.ieltstyper.entity.Mistake;
import org.example.ieltstyper.entity.User;
import org.example.ieltstyper.entity.UserWordProgress;
import org.example.ieltstyper.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordService {

    @Autowired
    private MistakeRepository mistakeRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private WordRepository wordRepository;   // ← 新增

    @Autowired
    private UserWordProgressRepository progressRepository;

    public void addMistake(String username, Long wordId) {
        if (!mistakeRepository.existsByUsernameAndWordId(username, wordId)) {
            Mistake newMistake = new Mistake(username, wordId);
            mistakeRepository.save(newMistake);
        }
    }

    // WordService.java

// 记得在顶部 @Autowired UserWordProgressRepository progressRepository;

    @Autowired
    private UserRepository userRepository; // 注入用户仓库

    public void recordSuccess(String username, Long wordId) {
        if (!progressRepository.findByUsernameAndWordId(username, wordId).isPresent()) {
            // 1. 记录具体的词（用于主页进度条）
            UserWordProgress progress = new UserWordProgress();
            progress.setUsername(username);
            progress.setWordId(wordId);
            progressRepository.save(progress);

            // 2.【关键修改】增加用户的总学习数（用于统计图表）
            User user = userRepository.findByUsername(username);
            if (user != null) {
                user.setLearnedTotal((user.getLearnedTotal() == null ? 0 : user.getLearnedTotal()) + 1);
                userRepository.save(user);
            }
        }
    }



    public List<Book> getAllBookInfo(String username) { // 增加用户名参数
        List<Book> books = bookRepository.findAllByOrderBySortOrderAsc();
        books.forEach(book -> {

            book.setTotalWords(wordRepository.countByBookName(book.getBookName()));

            book.setLearnedCount(progressRepository.countLearnedWords(username, book.getBookName()));
        });
        return books;
    }
}