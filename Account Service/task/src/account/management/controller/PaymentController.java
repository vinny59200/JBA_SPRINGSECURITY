package account.management.controller;

import account.management.dtos.PaymentDTO;
import account.management.exceptions.BusinessException;
import account.management.exceptions.CustomErrorResponse;
import account.management.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
public class PaymentController {

    private @Autowired
    PaymentService paymentService;

    @PostMapping("/api/acct/payments")
    @PreAuthorize("hasAnyRole('ACCOUNTANT')")
    public ResponseEntity createPayments(@RequestBody(required = false) List<PaymentDTO> request ){
        try {
            log.info("Upload Payment Parameter: {}", request.toString());
            paymentService.createPayments(request);
            return new ResponseEntity<>(Map.of("status", "Added successfully!"), HttpStatus.OK);
        }   catch (BusinessException e) {
            log.error("pb createPayments:"+e.getMessage());
            CustomErrorResponse error = new CustomErrorResponse();
            error.setPath("/api/acct/payments");
            error.setError("Bad Request");
            error.setStatus(400);
            error.setMessage(e.getMessage());
            error.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("api/acct/payments")
    public ResponseEntity changeSalary(@RequestBody PaymentDTO payment) {
        try {
            log.info("Change Payment Parameter: {}", payment.toString());
            paymentService.update(payment);
            return new ResponseEntity<>(Map.of("status", "Updated successfully!"), HttpStatus.OK);
        }   catch (BusinessException e) {
            log.error("pb changeSalary:"+e.getMessage());
            CustomErrorResponse error = new CustomErrorResponse();
            error.setPath("/api/acct/payments");
            error.setError("Bad Request");
            error.setStatus(400);
            error.setMessage(e.getMessage());
            error.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

}
