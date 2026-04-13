package org.example.ieltstyper.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String identifier; // 可以是用户名、邮箱或手机号
    private String password;
}
