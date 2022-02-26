package account.management.dtos;

import account.management.PeriodValidation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;


@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class PaymentDTO {

    @JsonProperty("employee")
    @Pattern(regexp = "[\\w.]+(@acme.com)$", message = "Email must be from acme.com domain")
    @Email(message = "You must enter a valid email")
    @NotBlank(message = "Email must not be null!")
    private String employeeEmail;

    @JsonProperty("period")
    @PeriodValidation
    private String period;

    @JsonProperty("salary")
    @Min(value = 1, message = "Salary must be non negative!")
    private long salary;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentDTO that = (PaymentDTO) o;
        return Objects.equals(employeeEmail, that.employeeEmail) && Objects.equals(period, that.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeEmail);
    }

}
