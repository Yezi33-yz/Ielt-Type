package org.example.ieltstyper.service;

import jakarta.transaction.Transactional;
import org.example.ieltstyper.dto.UserTypeRegistrationDto;
import org.example.ieltstyper.entity.CheckIn;
import org.example.ieltstyper.entity.Mistake;
import org.example.ieltstyper.entity.User;
import org.example.ieltstyper.entity.Word;
import org.example.ieltstyper.repository.CheckInRepository;
import org.example.ieltstyper.repository.MistakeRepository;
import org.example.ieltstyper.repository.UserRepository;
import org.example.ieltstyper.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MistakeRepository mistakeRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // 🌟 新增：注入密码加密器

    // 1. 用户注册 (已启用 BCrypt 加密)
    public void register(UserTypeRegistrationDto reg) {
        if (userRepository.findByUsername(reg.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(reg.getUsername());

        // 🌟 核心修改：密码加密存储，公开发布必备
        user.setPassword(passwordEncoder.encode(reg.getPassword()));

        user.setPhone(reg.getPhone());
        user.setEmail(reg.getEmail());

        user.setDailyGoal(20);
        user.setDailyReviewLimit(20);
        user.setLearnedTotal(0);
        user.setCurrentCount(0);
        user.setVip(false);
        user.setThemePreference("default");
        userRepository.save(user);
    }

    /**
     * 2. 登录方法在 JWT 模式下通常由 AuthController 直接调用 PasswordEncoder 校验。
     * 这里的旧 login 方法可以删除或留作备用。
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier);
    }

    // 3. 获取用户信息
    public User getUserInfo(String username) {
        return userRepository.findByUsername(username);
    }

    // 4. 更新背词进度与打卡记录
// UserService.java

    public void updateProgress(String username, Integer currentCount,
                               Integer dailyGoal, Integer studyMinutes) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            LocalDate today = LocalDate.now();

            if (currentCount != null) {
                // 1. 计算本次请求对比“上一次同步”多了几个词
                int increment = 0;

                // 如果当前的计数比数据库里的小，说明用户开启了新的一轮（页面刷新了）
                if (currentCount < user.getCurrentCount()) {
                    // 新的一轮，增量就是当前的 currentCount 本身
                    increment = currentCount;
                } else {
                    // 同一轮学习中，增量 = 当前总数 - 上次记录的总数
                    increment = currentCount - user.getCurrentCount();
                }

                // 2. 更新用户总表（learnedTotal 永远累加）
                if (increment > 0) {
                    user.setLearnedTotal((user.getLearnedTotal() == null ? 0 : user.getLearnedTotal()) + increment);
                }

                // 同步当前计数，作为下次计算的基准
                user.setCurrentCount(currentCount);

                // 3. 更新每日打卡表 (CheckIn)
                CheckIn todayRecord = checkInRepository.findByUsernameAndDate(username, today);
                if (todayRecord == null) {
                    todayRecord = new CheckIn();
                    todayRecord.setUsername(username);
                    todayRecord.setDate(today);
                    todayRecord.setWordCount(0); // 初始化
                    todayRecord.setStudyMinutes(0);
                }

                // 【关键修复】这里的 wordCount 应该是“当天的累计值”，而不是“本次学习的数值”
                if (increment > 0) {
                    todayRecord.setWordCount(todayRecord.getWordCount() + increment);
                }

                // 累计学习时长
                if (studyMinutes != null) {
                    // 同样采取累计逻辑，或者取最大值（取决于你想怎么统计）
                    int existingMinutes = todayRecord.getStudyMinutes() == null ? 0 : todayRecord.getStudyMinutes();
                    // 假设前端传的是“本次打开页面后的总时长”，我们取最大值
                    todayRecord.setStudyMinutes(Math.max(existingMinutes, studyMinutes));
                }

                checkInRepository.save(todayRecord);
            }

            if (dailyGoal != null) user.setDailyGoal(dailyGoal);
            user.setLastDate(today.toString());
            userRepository.save(user);
        }
    }

    // 5. 更新复习上限
    public void updateReviewLimit(String username, int limit) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setDailyReviewLimit(limit);
            userRepository.save(user);
        }
    }

    // 6. 更新用户隐私信息 (邮箱、电话)
    public void updateUserDetail(String username, String type, String value) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            if ("email".equalsIgnoreCase(type)) {
                user.setEmail(value);
            } else if ("phone".equalsIgnoreCase(type)) {
                user.setPhone(value);
            }
            userRepository.save(user);
        }
    }

    // 7. 修改密码 (已启用 BCrypt 加密)
    public void updatePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            // 🌟 核心修改：新密码也要加密存储
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    // ================= 错词本核心逻辑 =================

    public void recordMistake(String username, Long wordId) {
        if (!mistakeRepository.existsByUsernameAndWordId(username, wordId)) {
            Mistake newMistake = new Mistake();
            newMistake.setUsername(username);
            newMistake.setWordId(wordId);
            mistakeRepository.save(newMistake);
        }
    }

    public List<Word> getReviewWords(String username) {
        List<Mistake> mistakes = mistakeRepository.findByUsername(username);
        if (mistakes.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> wordIds = mistakes.stream()
                .map(Mistake::getWordId)
                .collect(Collectors.toList());
        return wordRepository.findAllById(wordIds);
    }

    @Transactional
    public void removeMistake(String username, Long wordId) {
        mistakeRepository.deleteByUsernameAndWordId(username, wordId);
    }


    public void setVip(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setVip(true);
            userRepository.save(user);
        }
    }



    public void switchBook(String username, String bookName) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setCurrentBook(bookName);
            userRepository.save(user);
        }
    }
}