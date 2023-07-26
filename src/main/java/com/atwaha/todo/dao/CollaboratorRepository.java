package com.atwaha.todo.dao;

import com.atwaha.todo.model.Collaborator;
import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;
import com.atwaha.todo.model.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    boolean existsByUserAndTask(User user, Task task);

    List<Collaborator> findAllByTaskAndUserNotAndInvitationStatusNot(Task task, User user, InvitationStatus invitationStatus);
}
