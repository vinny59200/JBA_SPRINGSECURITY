package account.management.controller;

import account.management.dtos.*;
import account.management.exceptions.*;
import account.management.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/auth/signup")
    @CrossOrigin(origins = "http://localhost:28852")
    public ResponseEntity signUp(@RequestBody UserDTO request) {

        try {
            log.error("signUp");
            return ResponseEntity.ok(userService.signUp(request));
        }   catch (BusinessException e) {
            log.error("pb signup:"+e.getMessage());
            CustomErrorResponse error = new CustomErrorResponse();
            error.setPath("");
            error.setError("Bad Request");
            error.setStatus(400);
            error.setMessage(e.getMessage());
            error.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/empl/payment")
    @PreAuthorize("hasAnyRole('USER','ACCOUNTANT')")
    @CrossOrigin(origins = "http://localhost:28852")
    public ResponseEntity getPayment(@AuthenticationPrincipal UserDetails request,
                                     @RequestParam(required = false)
                                             String period) {
        try {
            log.info("getPayment");
            List<PaymentExtractDTO> result = userService.getPayment(request, period);
            if (result.size() == 1) return ResponseEntity.ok(result.get(0));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("pb getPayment:" + e.getMessage());
            CustomErrorResponse error = new CustomErrorResponse();
            error.setPath("/api/empl/payment");
            error.setError("Bad Request");
            error.setStatus(400);
            error.setMessage(e.getMessage());
            error.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/auth/changepass")
    @CrossOrigin(origins = "http://localhost:28852")
    public ResponseEntity changePass(@RequestBody PasswordDTO request) {

        try {
            log.error("signUp");
            return ResponseEntity.ok(userService.changePass(request));
        }   catch (BusinessException e) {
            log.error("pb signup:"+e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PostMapping("/api/empl/payment")
//    @CrossOrigin(origins = "http://localhost:28852")
//    public ResponseEntity createPayment(@AuthenticationPrincipal List<PaymentDTO> request) {
//        try {
//            log.info("getPayment");
//            return ResponseEntity.ok(userService.createPayment(request));
//        }   catch (Exception e) {
//            log.error("pb getPayment:"+e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

}
