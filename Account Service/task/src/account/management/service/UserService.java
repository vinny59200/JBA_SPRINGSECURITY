package account.management.service;


import account.management.dtos.*;
import account.management.exceptions.BusinessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


public interface UserService extends UserDetailsService {

    UserLightDTO signUp(UserDTO request) throws BusinessException;

    List<PaymentExtractDTO> getPayment(UserDetails request,String period) throws BusinessException;

    SuccessfulPasswordChangeDTO changePass(PasswordDTO request) throws BusinessException;

    StatusDTO createPayment(List<PaymentDTO> request);

    List<UserLightDTO> findAll() throws BusinessException;

    UserStatusDTO delete(String username) throws BusinessException;

    UserLightDTO updateRole(RoleOperationDTO request) throws BusinessException;

    void updateUserIsAccountNonLocked(String email, boolean isAccountNonLocked, String operation, boolean isFromBrutForceOrigin);
}
