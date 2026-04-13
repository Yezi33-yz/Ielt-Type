package org.example.ieltstyper.component;


import org.example.ieltstyper.entity.Word;
import org.example.ieltstyper.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component //让 Spring 管理这个类
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private WordRepository wordRepository;

    @Override
    public void run(String... args) throws Exception{
        //1.先检查数据库里是否有单词了，防止每次启动都重复插入
        if (wordRepository.count() == 0) {
            System.out.println("检测到数据库为空，正在导入雅思核心库...");

            List<Word> ieltsWords = new ArrayList<>();

            //2.准备第一批数据
            ieltsWords.add(createWord("environment", "/ɪnˈvaɪrənmənt/", "环境", "We must protect the natural environment."));
            ieltsWords.add(createWord("sustainable", "/səˈsteɪnəbl/", "可持续的", "The government promotes sustainable development."));
            ieltsWords.add(createWord("infrastructure", "/ˈɪnfrəstrʌktʃə(r)/", "基础设施", "The city needs better public infrastructure."));
            ieltsWords.add(createWord("significant", "/sɪɡˈnɪfɪkənt/", "显著的/重要的", "There has been a significant increase in prices."));
            ieltsWords.add(createWord("perspective", "/pəˈspektɪv/", "观点/视角", "Try to see the issue from a different perspective."));
            ieltsWords.add(createWord("analyze", "/ˈænəlaɪz/", "分析", "Researchers need to analyze the data carefully."));

            //3.批量存入数据库
            wordRepository.saveAll(ieltsWords);
            System.out.println("词库初始化完成！共导入" + ieltsWords.size() + " 个单词。");
        } else {
            System.out.println("数据库已有数据，跳过初始化。");
        }
    }

    //辅助方法：快速创建单词对象
    private Word createWord(String spelling, String phonetic, String meaning, String example) {
        Word word = new Word();
        word.setSpelling(spelling);
        word.setPhonetic(phonetic);
        word.setMeaning(meaning);
        word.setExample(example);
        return word;
    }
}
