package org.example.ieltstyper.controller;


import org.example.ieltstyper.entity.Word;
import org.example.ieltstyper.repository.WordRepository;
import org.example.ieltstyper.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController//告诉Spring 这是一个接口类，返回的数据直接转换成JSON格式
@RequestMapping("/api/words") // 这一层接口的基础路径
public class WordController {

    @Autowired // 自动注入我们之前写的数据库仓库
    private WordRepository wordRepository;
    @Autowired
    private WordService wordService;

    /**
     * 随机获取一个单词
     * 访问路径： GET http://localhost:8080/api/words/random
     */
    @GetMapping("/random")
    public Word getRandomWord(@RequestParam(defaultValue = "雅思真题词汇精华") String book) {
        return wordRepository.findRandomByBook(book, 1).stream().findFirst().orElse(null);
    }

    @GetMapping("/books")        // ← 加上这个，完整路径是 /api/words/books
    public ResponseEntity<?> getAllBooks(@RequestParam String username) {
        return ResponseEntity.ok(wordService.getAllBookInfo(username));
    }

    @GetMapping("/batch")
    public List<Word> getBatch(@RequestParam(defaultValue = "20") int count) {
        return wordRepository.findRandom(count);
    }


    @GetMapping
    public List<Word> getBatchWords(@RequestParam(defaultValue = "20") int count,
                                    @RequestParam(defaultValue = "ielts_standard") String book) {
        // 从词库随机取 count 个单词
        return wordRepository.findRandomByBook(book, count);
    }




    @PostMapping("/mistakes")
    public String recordMistake(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");

        // 【关键修复】先把前端传来的数字转成字符串，再安全地解析为 Long 类型
        Long wordId = Long.valueOf(payload.get("wordId").toString());

        wordService.addMistake(username, wordId);

        return "单词已收录至错词本";
    }



    // WordController.java

    @PostMapping("/success")
    public ResponseEntity<?> recordSuccess(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        Long wordId = Long.valueOf(payload.get("wordId").toString());

        wordService.recordSuccess(username, wordId);
        return ResponseEntity.ok("单词进度已更新");
    }
}


/**
 *这段代码建立了一个数据通道，它的运行逻辑如下：
 *
 * @RestController: 这个注解非常关键。它告诉 Spring，这个类里的方法返回的不是“网页名字”，而是“数据”。
 * Spring 会自动调用一个叫 Jackson 的库，把你的 Word 对象转成类似
 * { "spelling": "environment", ... } 这种格式（即 JSON）。
 *
 * @RequestMapping("/api/words"): 这是一个前缀。为了让 API 更有条理，我们通常会加上 /api 前缀。
 *
 * @GetMapping("/random"): 规定了只有通过 GET 方法（比如浏览器直接访问）请求 /random 时，才会触发这个方法。
 */