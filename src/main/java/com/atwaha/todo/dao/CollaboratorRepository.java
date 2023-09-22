package com.atwaha.todo.dao;

import com.atwaha.todo.model.Collaborator;
import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;
import com.atwaha.todo.model.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    boolean existsByUserAndTask(User user, Task task);

    boolean existsByUserAndTaskAndIsEnabled(User user, Task task, boolean enabled);

    //        List<Collaborator> findAllByTaskAndUserNotAndInvitationStatus(Task task, User user, InvitationStatus invitationStatus);
    List<Collaborator> findAllByTaskAndUserNotAndInvitationStatusAndIsEnabled(Task invalidTaskId, User invalidUserId, InvitationStatus invitationStatus, boolean enabled);

    boolean existsByTask(Task task);

    Collaborator findByUserAndTask(User user, Task task);

    Long countByUserAndInvitationStatus(User user, InvitationStatus invitationStatus);

    List<Collaborator> findAllByUserAndInvitationStatus(User user, InvitationStatus invitationStatus);

    List<Collaborator> findAllByUserAndInvitationStatusAndIsEnabled(User user, InvitationStatus invitationStatus, boolean enabled);

//    boolean existsByUserAndTaskAndInvitationStatus(User user, Task task, InvitationStatus invitationStatus);

    boolean existsByUserAndTaskAndInvitationStatusAndIsEnabled(User user, Task task, InvitationStatus invitationStatus, boolean enabled);

    boolean existsByUserAndTaskAndInvitationStatusNot(User user, Task task, InvitationStatus invitationStatus);


    Long countByUserAndInvitationStatusAndIsEnabled(User user, InvitationStatus invitationStatus, boolean enabled);


}
