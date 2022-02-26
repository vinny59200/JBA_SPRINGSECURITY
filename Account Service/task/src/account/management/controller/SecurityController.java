package account.management.controller;

import account.management.dtos.EventDto;
import account.management.dtos.RoleOperationDTO;
import account.management.dtos.UserLightDTO;
import account.management.dtos.UserStatusDTO;
import account.management.exceptions.BusinessException;
import account.management.exceptions.CustomErrorResponse;
import account.management.exceptions.UserNotFoundException;
import account.management.service.EventService;
import account.management.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;



@Slf4j
@RestController
public class SecurityController {

    @Autowired
    private EventService eventService;

    @GetMapping("/api/security/events")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @CrossOrigin(origins = "http://localhost:28852")
    public ResponseEntity getEvents() {
        try {
            log.info("getEvents");
            List<EventDto> result = eventService.getEvents();
            if (result.size() == 1) return ResponseEntity.ok(result.get(0));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("pb getEvents:" + e.getMessage());
            CustomErrorResponse error = new CustomErrorResponse();
            error.setPath("/api/security/events");
            error.setError("Bad Request");
            error.setStatus(400);
            error.setMessage(e.getMessage());
            error.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }


}
