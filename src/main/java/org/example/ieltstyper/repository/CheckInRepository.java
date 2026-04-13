package org.example.ieltstyper.repository;


import org.example.ieltstyper.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn,Long> {
    //查找某个用户在某个日期的记录
    CheckIn findByUsernameAndDate(String username, LocalDate date);

    //查找用户在某一段时间内的所有记录（用于折线图）
    List<CheckIn> findByUsernameAndDateBetweenOrderByDateAsc (String username, LocalDate start, LocalDate end);

    List<CheckIn> findByUsername(String username);

}
