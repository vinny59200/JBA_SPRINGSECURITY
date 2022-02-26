package account.management.service;

import account.management.dtos.PaymentDTO;
import account.management.exceptions.BusinessException;

import java.util.List;

public interface PaymentService {
    void createPayments(List<PaymentDTO> request) throws BusinessException;

    void update(PaymentDTO payment) throws BusinessException;
}
