package org.example.ieltstyper.controller;

import org.example.ieltstyper.entity.CheckIn;
import org.example.ieltstyper.repository.CheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private CheckInRepository checkInRepository;

    /**
     * 日历热力图：返回过去一整年的所有打卡记录
     * 前端根据日期精确点亮格子
     */

    @GetMapping("/calendar")
    public List<CheckIn> getCalendarData(@RequestParam String username) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.withDayOfYear(1); // 当年 1 月 1 日
        return checkInRepository.findByUsernameAndDateBetweenOrderByDateAsc(username, start, end);
    }
    @GetMapping("/chart")
    public List<CheckIn> getChartData(
            @RequestParam String username,
            @RequestParam String range,
            @RequestParam(defaultValue = "0") int offset) {

        LocalDate now = LocalDate.now();
        LocalDate start;
        LocalDate end;

        // 🚀 核心逻辑：给出一个足够宽的窗口，让前端去精确聚合
        if ("week".equals(range)) {
            // 当前周的基准点
            LocalDate base = now.plusWeeks(offset);
            // 取基准点前后各两周，绝对够用
            start = base.minusWeeks(2);
            end = base.plusWeeks(1);
        } else if ("month".equals(range)) {
            // 雅思 3 个月周期基准
            LocalDate base = now.plusMonths(offset * 3);
            // 取基准点前后半年
            start = base.minusMonths(6);
            end = base.plusMonths(6);
        } else {
            // 季度/年度模式：直接给当年前后两年的
            start = now.plusYears(offset).withDayOfYear(1);
            end = start.plusYears(1).minusDays(1);
        }

        return checkInRepository.findByUsernameAndDateBetweenOrderByDateAsc(username, start, end);
    }
}