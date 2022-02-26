package account.management.repository;

import account.management.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;


public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByUserIdOrderByPeriodDesc(final Long id);

    List<Payment> findPaymentByPeriodAndUser_Email(Date period, String email);

}
