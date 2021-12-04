package ru.netology.mycloud.dto;

import lombok.Data;
@Data
public class AuthRequestDTO {
    private String login;
    private String password;
}
