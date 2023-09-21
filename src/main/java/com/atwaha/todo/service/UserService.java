package com.atwaha.todo.service;

import com.atwaha.todo.dao.CollaboratorRepository;
import com.atwaha.todo.dao.TaskRepository;
import com.atwaha.todo.dao.UserRepository;
import com.atwaha.todo.model.Collaborator;
import com.atwaha.todo.model.Task;
import com.atwaha.todo.model.User;
import com.atwaha.todo.model.dto.LoginRequestDTO;
import com.atwaha.todo.model.dto.UserResponseDTO;
import com.atwaha.todo.model.enums.InvitationStatus;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final CollaboratorService collaboratorService;
    private final BCryptPasswordEncoder passwordEncoder;

    //    Transform User to UserResponseDTO
    @Nonnull
    private UserResponseDTO createUserResponseDTO(@Nonnull User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setEmail(user.getEmail());
        return userResponseDTO;
    }

    public ResponseEntity<UserResponseDTO> createUser(User user) {
        try {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);
//            convert to lowercase - email should be case-insensitive
            user.setEmail(user.getEmail().toLowerCase());
            User newUser = userRepository.save(user);

//            Convert to UserResponseDTO
            UserResponseDTO userResponseDTO = createUserResponseDTO(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<UserResponseDTO> login(LoginRequestDTO loginRequest) {
        try {
//            Convert to lowercase - email should be case-insensitive
            String username = loginRequest.getUsername().toLowerCase();
            String rawPassword = loginRequest.getPassword();

            User user = userRepository.findByEmail(username);

            if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
//                Convert to UserResponseDTO
                UserResponseDTO userResponseDTO = createUserResponseDTO(user);
                //            try {
//                TimeUnit.SECONDS.sleep(5);
//            } catch (InterruptedException e) {
//                // do nothing
//            }
                return ResponseEntity.ok(userResponseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    public ResponseEntity<List<User>> getUsers() {
        try {
            return ResponseEntity.ok(userRepository.findAll());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //    User tp invite: not owner of the task neither collaborator

    public ResponseEntity<List<User>> getPendingInvitations(Long userId, Long taskId) {
        try {
//            List<Collaborator> pendingCollaborators = collaboratorRepository.findAllByTaskAndUserNotAndInvitationStatus(
//                    taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Invalid Task Id")),
//                    userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User Id")),
//                    InvitationStatus.PENDING);
//TODO HERE
            List<Collaborator> pendingCollaborators = collaboratorRepository.findAllByTaskAndUserNotAndInvitationStatusAndIsEnabled(
                    taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Invalid Task Id")),
                    userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Invalid User Id")),
                    InvitationStatus.PENDING, true);

            if (pendingCollaborators.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            List<Long> pendingUserIds = pendingCollaborators
                    .stream()
                    .map(user -> user.getUser().getId())
                    .toList();

            List<User> pendingUsers = userRepository.findAllByIdIn(pendingUserIds);
            return ResponseEntity.ok(pendingUsers);
        } catch (Exception e) {
            System.err.println("Collaborator Service: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<User>> getUsersToInvite(Long userId, Long taskId) {
        try {
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Invalid Task Id"));
            boolean taskExistsInCollaborators = collaboratorRepository.existsByTask(task);
            List<User> usersToInvite;
            if (taskExistsInCollaborators) {
                usersToInvite = userRepository
                        .findAllByIdNot(userId)
                        .stream()
                        .filter(user -> !collaboratorRepository.existsByUserAndTask(user, task) || collaboratorRepository.existsByUserAndTaskAndIsEnabled(user, task, false))
                        .toList();
            } else {
                usersToInvite = userRepository.findAllByIdNot(userId);
            }
            return ResponseEntity.ok(usersToInvite);
        } catch (Exception e) {
            System.err.println("User Service: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}