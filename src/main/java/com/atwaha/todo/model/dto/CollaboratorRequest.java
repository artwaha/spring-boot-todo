package com.atwaha.todo.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CollaboratorRequest {
    private Long userId;
    private Long taskId;
}
