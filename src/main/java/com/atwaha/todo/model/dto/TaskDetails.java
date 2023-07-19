package com.atwaha.todo.model.dto;

import com.atwaha.todo.model.Collaborator;
import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;

import java.util.List;

public class TaskDetails {
    private Task task;
    private List<Collaborator> collaborators;
    private List<User> guests;
}
