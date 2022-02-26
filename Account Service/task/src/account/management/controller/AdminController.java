package account.management.controller;

import account.management.dtos.*;
import account.management.exceptions.BusinessException;
import account.management.exceptions.CustomErrorResponse;
import account.management.exceptions.UserNotFoundException;
import account.management.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RestController
public class AdminController {

    @Autowired
    private UserService userService;


    @GetMapping("/api/admin/user")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @CrossOrigin(origins = "http://localhost:28852")
    public ResponseEntity getUsers() {
        try {
            log.info("getUsers");
            List<UserLightDTO> result = userService.findAll();
            if (result.size() == 1) return ResponseEntity.ok(result.get(0));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("pb getUsers:" + e.getMessage());
            CustomErrorResponse error = new CustomErrorResponse();
            error.setPath("/api/admin/user");
            error.setError("Bad Request");
            error.setStatus(400);
            error.setMessage(e.getMessage());
            error.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/api/admin/user/{email}")
    @CrossOrigin(origins = "http://localhost:28852")
    public ResponseEntity deleteUser(@PathVariable("email") String email) {
        try {
            log.info("deleteUser");
            UserStatusDTO result = userService.delete(email);
            return ResponseEntity.ok(result);
        } catch (UserNotFoundException e) {
            log.error("pb deleteUser 1:" + e.getMessage());
            CustomErrorResponse error = new CustomErrorResponse();
            error.setPath("/api/admin/user/" + email);
            error.setError("Not Found");
            error.setStatus(404);
            error.setMessage(e.getMessage());
            error.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("pb deleteUser 2:" + e.getMessage());
            CustomErrorResponse error = new CustomErrorResponse();
            error.setPath("/api/admin/user/" + email);
            error.setError("Bad Request");
            error.setStatus(400);
            error.setMessage(e.getMessage());
            error.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/api/admin/user/role")
    public ResponseEntity modifyRole(@RequestBody RoleOperationDTO operation) {
        try {
            log.info("Change role Parameter: {}", operation.toString());
            return new ResponseEntity<>(userService.updateRole(operation), HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("pb modifyRole:" + e.getMessage());
            CustomErrorResponse error = new CustomErrorResponse();
            error.setPath("/api/admin/user/role");
            error.setError("Bad Request");
            error.setStatus(400);
            error.setMessage(e.getMessage());
            error.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/api/admin/user/access")
    public ResponseEntity<?> changeAccess(@RequestBody ChangeUserLockedStatusDTO operation) {

        log.info("Change access Parameter: {}", operation.toString());
        String username = operation.getUser().toLowerCase(Locale.ROOT);
        boolean isAccountNonLocked = operation.getOperation().equals("UNLOCK");
        userService.updateUserIsAccountNonLocked(username, isAccountNonLocked, operation.getOperation(),false);
        log.error("VV55 " + username + " " + operation.getOperation().toLowerCase(Locale.ROOT));
        Map<String, String> response = Map.of("status",
                String.format("User %s %sed!", username, operation.getOperation().toLowerCase(Locale.ROOT)));

        return new ResponseEntity<>(response, HttpStatus.OK);

    }


}
