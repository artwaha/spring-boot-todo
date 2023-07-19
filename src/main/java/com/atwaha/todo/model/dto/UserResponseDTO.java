package com.atwaha.todo.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
}
