package com.atwaha.todo.dao;

import com.atwaha.todo.model.Collaborator;
import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    @Query(
//            value = "SELECT * FROM collaborators c WHERE c.task_id=:taskId AND c.user_id!=:userId AND c.invitation_status='ACCEPTED'",
            value = "SELECT * FROM collaborators c WHERE c.task_id=:taskId AND c.user_id!=:userId AND c.invitation_status!='REJECTED'",
            nativeQuery = true
    )
    List<Collaborator> findTaskCollaborators(Long taskId, Long userId);

    boolean existsByUserAndTask(User user, Task task);
}
