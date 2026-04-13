package org.example.ieltstyper.dto;

import lombok.Data;

@Data // 同样使用 Lombok 自动生成 Get/Set 方法
public class UserTypeRegistrationDto {
    private String username;
    private String password;
    private String email;
    private String phone;
}
