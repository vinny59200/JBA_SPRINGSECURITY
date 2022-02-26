package account.management.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class UserLockDTO {


    String user;

    String operation;
}
