package org.example.ieltstyper.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data // 自动生成 Getter, Setter, toString 等
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    // --- 1. 喂猫打卡进度相关 ---
    private Integer dailyGoal = 0;    // 今日目标单词数
    private Integer currentCount = 0; // 今日已完成单词数
    private String lastDate;          // 上次打卡日期 (yyyy-MM-dd)

    // --- 2. 艾宾浩斯与主页统计 ---
    private Integer dailyReviewLimit = 20; // 每日复习上限
    private Integer learnedTotal = 0;      // 累计掌握单词总数
    private String currentBook = "ielts_awl_570"; // 当前学习词书

    // --- 3. 个人中心与隐私设置 ---
    private Boolean vip = false;         // 是否为 VIP
    private String avatarUrl;              // 头像图片地址
    private String themePreference = "default"; // 用户偏好的主题色

    // 隐私字段（建议设置唯一约束，防止重复绑定）
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String wechatOpenid;
    private String qqOpenid;

    // --- 4. 辅助构造函数 ---
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}