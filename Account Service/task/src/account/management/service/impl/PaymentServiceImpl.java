package account.management.service.impl;

import account.management.dtos.PaymentDTO;
import account.management.entities.Payment;
import account.management.exceptions.BusinessException;
import account.management.repository.PaymentRepository;
import account.management.repository.UserRepository;
import account.management.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void createPayments(List<PaymentDTO> request) throws BusinessException {

        paymentRepository.saveAll(mapDTOtoEntity(request));
    }

    @Override
    public void update(PaymentDTO payment) throws BusinessException {
        paymentRepository.findPaymentByPeriodAndUser_Email(
                        stringToDate(payment.getPeriod()),
                        payment.getEmployeeEmail())
                .stream()
                .forEach(pay -> paymentRepository.delete(pay));
        paymentRepository.save(mapSingleDTOtoEntity(payment));
    }

    private Payment mapSingleDTOtoEntity(PaymentDTO paymentDTO) throws BusinessException {
        if (paymentDTO.getSalary() <0) throw new BusinessException("Salary cannot be negative");
        if (stringToDate(paymentDTO.getPeriod())==null) throw new BusinessException("Date is invalid");
        Payment result= Payment.builder()
                .period(stringToDate(paymentDTO.getPeriod()))
                .salary(paymentDTO.getSalary())
                .user(userRepository.getUserByEmail(paymentDTO.getEmployeeEmail())).build();
        log.error("VV55 mapSingleDTOtoEntity ");
        return result;
    }

    private List<Payment> mapDTOtoEntity(List<PaymentDTO> request) throws BusinessException {
        request.stream().forEach(paymentDTO -> {log.error("PaymentDTO: " + paymentDTO);});
        checkSalary(request);
        checkPeriod(request);
        checkDuplicate(request);
        List<Payment> result= request.stream().map(paymentDTO ->
                Payment.builder()
                        .period(stringToDate(paymentDTO.getPeriod()))
                        .salary(paymentDTO.getSalary())
                        .user(userRepository.getUserByEmail(paymentDTO.getEmployeeEmail())).build())
                .collect(Collectors.toList());
        log.error("VV55 mapDTOtoEntity ");
        result.stream().forEach(paymentDTO -> {log.error("Payment: " + paymentDTO);});
        return result;

    }

    private void checkDuplicate(List<PaymentDTO> potentialDuplicates) throws BusinessException {
        final Set<PaymentDTO> duplicatesFinding = new HashSet<PaymentDTO>();

        for (PaymentDTO potentialDuplicate : potentialDuplicates) {
            if (!duplicatesFinding.add(potentialDuplicate)) {
                throw new BusinessException("Duplicate found");
            }
        }

    }

    private void checkSalary(List<PaymentDTO> req) throws BusinessException {
        for (PaymentDTO paymentDTO : req) {
            if (paymentDTO.getSalary() <0) throw new BusinessException("Salary cannot be negative");
        }
    }

    private void checkPeriod(List<PaymentDTO> req) throws BusinessException {
        for (PaymentDTO paymentDTO : req) {
            if (stringToDate(paymentDTO.getPeriod())==null) throw new BusinessException("Date is invalid");
        }
    }

    private Date stringToDate(String period) {
        SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
        format.setLenient(false);
        Date javaDate = null;
        try {
            javaDate = new Date(format.parse(period).getTime());
        } catch (ParseException e) {
            System.out.println(period + " is Invalid Date format");
        }
        return javaDate;
    }
}
