package com.skillsync.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String id;
    private String email;
    private String name;
    private String password;
    private List<String> followers;
    private List<String> following;
}
