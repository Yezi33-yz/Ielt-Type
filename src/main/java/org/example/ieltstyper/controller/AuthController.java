package org.example.ieltstyper.controller;

import org.example.ieltstyper.config.JwtUtils; // 确保路径正确
import org.example.ieltstyper.dto.LoginDto;
import org.example.ieltstyper.dto.UserTypeRegistrationDto;
import org.example.ieltstyper.entity.User;
import org.example.ieltstyper.entity.Word;
import org.example.ieltstyper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils; // 🌟 注入 JWT 工具类

    @Autowired
    private PasswordEncoder passwordEncoder; // 🌟 注入加密器

    /**
     * 1. 账号注册
     * 提示：确保 UserService.register 内部使用了 passwordEncoder.encode()
     */
    @PostMapping("/register")
    public String register(@RequestBody UserTypeRegistrationDto reg) {
        userService.register(reg);
        return "注册成功";
    }

    /**
     * 2. 账号密码登录 (JWT 模式)
     * 返回包含 Token 的 JSON 对象
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto login) {

        // ✅ 改这一行，用三合一查询替换原来的 getUserInfo
        User user = userService.findByIdentifier(login.getIdentifier());

        if (user != null && passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            String token = jwtUtils.generateToken(user.getUsername());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("username", user.getUsername());
            result.put("message", "Login successful");

            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("账号或密码不正确");
        }
    }

    // 3. 社交登录回调占位
    @GetMapping("/callback/{platform}")
    public String socialCallback(@PathVariable String platform, @RequestParam String code) {
        return platform + " 登录成功";
    }

    // 4. 获取用户信息
    @GetMapping("/user/info")
    public User getUserInfo(@RequestParam String username) {
        return userService.getUserInfo(username);
    }

    /**
     * 5. 同步背词进度
     * 由 JwtAuthenticationFilter 保护，请求需携带 Authorization: Bearer <token>
     */
    @PostMapping("/user/updateProgress")
    public String updateProgress(@RequestBody java.util.Map<String, Object> payload) {
        String username = (String) payload.get("username");
        Integer currentCount = payload.get("currentCount") != null ?
                Integer.parseInt(payload.get("currentCount").toString()) : null;
        Integer dailyGoal = payload.get("dailyGoal") != null ?
                Integer.parseInt(payload.get("dailyGoal").toString()) : null;
        // ✅ 新增
        Integer studyMinutes = payload.get("studyMinutes") != null ?
                Integer.parseInt(payload.get("studyMinutes").toString()) : null;

        userService.updateProgress(username, currentCount, dailyGoal, studyMinutes);
        return "目标与进度同步成功！";
    }

    @PostMapping("/user/updateReviewLimit")
    public String updateReviewLimit(@RequestParam String username, @RequestParam int limit){
        userService.updateReviewLimit(username, limit);
        return "复习目标已设置";
    }

    @PostMapping("/user/updatePrivacy")
    public String updatePrivacy(@RequestParam String username, @RequestParam String type, @RequestParam String value) {
        userService.updateUserDetail(username, type, value);
        return "修改成功";
    }

    @PostMapping("/user/changePassword")
    public String changePassword(@RequestParam String username, @RequestParam String newPassword) {
        userService.updatePassword(username, newPassword);
        return "密码已更新";
    }

    // ================= 错词本接口 =================

    @PostMapping("/user/recordMistake")
    public String recordMistake(@RequestBody java.util.Map<String, Object> payload) {
        String username = (String) payload.get("username");
        Long wordId = Long.valueOf(payload.get("wordId").toString());
        userService.recordMistake(username, wordId);
        return "错词记录成功";
    }

    @PostMapping("/user/setVip")
    public String setVip(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        // ✅ 通过 UserService 操作，不直接用 userRepository
        userService.setVip(username);
        return "VIP 已开通";
    }

    @PostMapping("/user/switchBook")
    public ResponseEntity<?> switchBook(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String bookName = payload.get("bookName");
        userService.switchBook(username, bookName);
        return ResponseEntity.ok("switched");
    }






    @GetMapping("/user/reviewWords")
    public java.util.List<Word> getReviewWords(@RequestParam String username) {
        return userService.getReviewWords(username);
    }

    @PostMapping("/user/removeMistake")
    public String removeMistake(@RequestParam String username, @RequestParam Long wordId) {
        userService.removeMistake(username, wordId);
        return "错词已彻底消灭";
    }
}