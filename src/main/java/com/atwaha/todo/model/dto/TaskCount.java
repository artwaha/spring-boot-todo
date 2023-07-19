package com.atwaha.todo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskCount {
    private Long all;
    private Long done;
    private Long pending;
}
