package com.example.chatroom.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class UserDto {
    @NotBlank(message = "Tên đăng nhập không được để trống!")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự!")
    private String username;

    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không hợp lệ!")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống!")
    @Size(min = 8, max = 100, message = "Mật khẩu phải từ 8 đến 100 ký tự!")
    private String password;

    @NotBlank(message = "Họ không được để trống!")
    private String firstName;

    @NotBlank(message = "Tên không được để trống!")
    private String lastName;
}
