package account.management.service.impl;

import account.RestAuthenticationEntryPoint;
import account.management.EventLogger;
import account.management.entities.EventAction;
import account.management.entities.User;
import account.management.dtos.*;
import account.management.exceptions.*;
import account.management.exceptions.BusinessException;
import account.management.exceptions.UserExistException;
import account.management.repository.PaymentRepository;
import account.management.repository.UserRepository;
import account.management.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private final PasswordEncoder encoder;

    @Autowired
    EventLogger logger;


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }


    @Override
    public UserLightDTO signUp(UserDTO request) throws BusinessException {

        log.error("VV request: {}", request);

        if (request.getPassword() == null ||
                request.getPassword().isEmpty()) throw new BusinessException("Password must not be empty");

        checkBreachPasswords(request.getPassword());

        if (request.getName() == null ||
                request.getName().isEmpty()) throw new BusinessException("Name must not be empty");

        if (request.getLastname() == null ||
                request.getLastname().isEmpty()) throw new BusinessException("Lastname must not be empty");

        if (request.getEmail() == null ||
                request.getEmail().isEmpty()) throw new BusinessException("Email must not be empty");

        if (!request.getEmail().endsWith("@acme.com")) throw new BusinessException("Email must end with @acme.com");

        if (userRepository.existsByEmail(request.getEmail())) throw new UserExistException();

        User user = userRepository.save(
                User.builder()
                        .name(request.getName())
                        .lastname(request.getLastname())
                        .email(request.getEmail().toLowerCase(Locale.ROOT))
                        .password(encoder.encode(request.getPassword()))
                        .roles(getRole()).build()
        );
        logger.log(0L, EventAction.CREATE_USER, "Anonymous", user.getEmail().toLowerCase(), "/api/auth/signup");
        return UserLightDTO.builder()
                .id(user.getId())
                .name(request.getName())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .roles(user.getRoles())
                .build();
    }

    private List<String> getRole() {
        return userRepository.findAll().stream().count() > 0 ? Arrays.asList("ROLE_USER") : Arrays.asList("ROLE_ADMINISTRATOR");
    }

    @Override
    public List<PaymentExtractDTO> getPayment(UserDetails request, String period) throws BusinessException {
        log.error("VV25 request: {}", request.getUsername());
        User user = userRepository.getUserByEmail(request.getUsername());
        log.error("VV55 request: {}", request.getUsername());
        if (isNull(period))
            return paymentRepository.findAllByUserIdOrderByPeriodDesc(user.getId())
                    .stream()
                    .map(pay -> PaymentExtractDTO.builder()
                            .name(pay.getUser().getName())
                            .period(custFormatDate(pay.getPeriod()))
                            .salary(custFormatMoney(pay.getSalary()))
                            .lastname(pay.getUser().getLastname()).build()).collect(Collectors.toList());
        else {
            if (stringToDate(period) == null) throw new BusinessException("Date is invalid");
            return paymentRepository.findPaymentByPeriodAndUser_Email(stringToDate(period), user.getEmail())
                    .stream()
                    .map(pay -> PaymentExtractDTO.builder()
                            .name(pay.getUser().getName())
                            .period(custFormatDate(pay.getPeriod()))
                            .salary(custFormatMoney(pay.getSalary()))
                            .lastname(pay.getUser().getLastname()).build()).collect(Collectors.toList());
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

    private String custFormatMoney(Long salary) {
        Long dollars = salary / 100;
        Long cents = salary % 100;
        return String.format("%d dollar(s) %d cent(s)", dollars, cents);
    }

    private String custFormatDate(Date period) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM-yyyy", Locale.ENGLISH);
        return format.format(period);
    }

    @Override
    public SuccessfulPasswordChangeDTO changePass(PasswordDTO request) throws BusinessException {

        checkPasswordLength(request.getNewPassword());

        User user = getAndCheckAuthenticatedUser();

        checkBreachPasswords(request.getNewPassword());

        checkIfSamePasswords(request, user);

        log.error("VV9 user: {} {} {} {} {} {}", user.getEmail(), user.getName(), user.getLastname(), user.getId(), "NewPass=", request.getNewPassword());
        userRepository.findById(user.getId()).ifPresent(u -> u.setPassword(encoder.encode(request.getNewPassword())));
        logger.log(0L, EventAction.CHANGE_PASSWORD, user.getEmail().toLowerCase(), user.getEmail().toLowerCase(), "/api/auth/changepass");
        userRepository.save(user);

        return SuccessfulPasswordChangeDTO.builder().email(user.getEmail()).status("The password has been updated successfully").build();
    }

    @Override
    public StatusDTO createPayment(List<PaymentDTO> request) {
        for (PaymentDTO paymentDTO : request) {
            log.error("VV saving: {}", paymentDTO);

        }
        return null;
    }

    @Override
    public List<UserLightDTO> findAll() throws BusinessException {
        log.error("VV36 count:" + userRepository.count());
        List<UserLightDTO> result = userRepository.findAll()
                .stream()
                .map(user ->
                        UserLightDTO.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .lastname(user.getLastname())
                                .email(user.getEmail())
                                .roles(user.getRoles())
                                .build()).collect(Collectors.toList());
        result.stream().forEach(user ->
                log.error("VV36 user: {} {} {} {} {} {}",
                        user.getEmail(),
                        user.getName(),
                        user.getLastname(),
                        user.getId(),
                        "Roles=",
                        user.getRoles()));
        return result;
    }

    @Override
    public UserStatusDTO delete(String username) throws BusinessException {
        log.error("VV55 delete ");
        log.error("VV delete 1");
        if (userRepository.getUserByEmail(username) == null) throw new UserNotFoundException("User not found!");
        log.error("VV delete 2");
        if (userRepository.getUserByEmail(username).getRoles().contains("ROLE_ADMINISTRATOR"))
            throw new BusinessException("Can't remove ADMINISTRATOR role!");
        log.error("VV delete 3");
        logger.log(0L, EventAction.DELETE_USER, SecurityContextHolder.getContext().getAuthentication().getName(), username, String.format("/api/admin/user/{email}", username));
        long deleteCount = userRepository.deleteByEmail(username);
        log.error("VV delete 4");
        if (deleteCount > 0) return UserStatusDTO.builder()
                .user(username)
                .status("Deleted successfully!")
                .build();
        else throw new BusinessException("User not found!");
    }

    @Override
    public UserLightDTO updateRole(RoleOperationDTO request) throws BusinessException {
        if (request.getRole().equals("ADMINISTRATOR") && request.getOperation().equals(RoleOperation.REMOVE))
            throw new BusinessException("Can't remove ADMINISTRATOR role!");
        log.error("VV47 operation: {} / {} {}", request.getOperation(), RoleOperation.REMOVE.name(), RoleOperation.GRANT.name());
        log.error("VV47 operation: {}", !Arrays.stream(RoleOperation.values()).anyMatch(r -> r.name().equals(request.getOperation().name())));
        if (!Arrays.stream(RoleOperation.values()).anyMatch(r -> r.name().equals(request.getOperation().name())))
            throw new RoleOperationNotFoundException("Role operation not found!");
        log.error("VV49 request: {}", request);
        User user = userRepository.getUserByEmail(request.getUser());
        log.error("VV55 updateRole user: {}", user);
        if (user == null) throw new UserNotFoundException("User not found!");
        if (request.getRole().equals("AUDITOR") && request.getOperation().equals(RoleOperation.GRANT) && user.getRoles().contains("ROLE_ADMINISTRATOR"))
            throw new BusinessException("The user cannot combine administrative and business roles!");
        log.error("VV 57 user: {} {} {} {} {} {}", user.getEmail(), user.getName(), user.getLastname(), user.getId(), "Roles=", user.getRoles());
        List<String> newRoles = new ArrayList<>(user.getRoles());
        log.error("VV57 remove: {} in {} ?=> {}", "ROLE_" + request.getRole(), newRoles, newRoles.contains("ROLE_" + request.getRole()));
        if (!newRoles.contains("ROLE_" + request.getRole()) && request.getOperation().equals(RoleOperation.REMOVE))
            throw new BusinessException("The user does not have a role!");
        checkRole(request.getRole());
        if (newRoles.size() == 1 && newRoles.get(0).equals("ROLE_" + request.getRole()) && RoleOperation.REMOVE.equals(request.getOperation()))
            throw new BusinessException("The user must have at least one role!");
        for (String role : user.getRoles()) {
            if (role.substring(5).equals("ADMINISTRATOR") && request.getRole().equals("USER")
                    || role.substring(5).equals("USER") && request.getRole().equals("ADMINISTRATOR"))
                throw new BusinessException("The user cannot combine administrative and business roles!");
            if (role.substring(5).equals(request.getRole()) && request.getOperation().equals(RoleOperation.REMOVE)) {
                log.error("VV49 remove: {} {}", role, role.substring(5));
                newRoles.remove(role);
            } else if (!role.equals(request.getRole()) && request.getOperation().equals(RoleOperation.GRANT)) {
                log.error("VV49 grant: {} {}", role, "ROLE_" + request.getRole());
                newRoles.add("ROLE_" + request.getRole());
            }
        }
        newRoles.sort(Comparator.naturalOrder());
        user.setRoles(newRoles);
        if (request.getOperation().equals(RoleOperation.REMOVE)) {
            logger.log(0L, EventAction.REMOVE_ROLE, SecurityContextHolder.getContext().getAuthentication().getName(), "Remove role " + request.getRole() + " from " + user.getEmail(), "/api/admin/user/role");
        } else {
            logger.log(0L, EventAction.GRANT_ROLE, SecurityContextHolder.getContext().getAuthentication().getName(), "Grant role " + request.getRole() + " to " + user.getEmail(), "/api/admin/user/role");
        }
        userRepository.save(user);
        return UserLightDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }

    private void checkRole(String role) {
        if (!role.equals("USER") &&
                !role.equals("ACCOUNTANT") &&
                !role.equals("ADMINISTRATOR") && !role.equals("AUDITOR"))
            throw new RoleNotFoundException("Role not found!");
    }

    private void checkPasswordLength(String newPass) {
        if (newPass != null && newPass.length() < 12)
            throw new PasswordLengthInfringedException();
    }

    private User getAndCheckAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getName().toLowerCase(Locale.ROOT).startsWith("anonymous")) throw new UserUnauthorizedException();
        final UserDetails userD = (UserDetails) auth.getPrincipal();
        log.error("VV22 user: {} {} {} {} {} {}", userD.getUsername(), userD.getPassword(), userD.getAuthorities(), userD.getAuthorities().toString(), userD.getAuthorities().toArray(), userD.getAuthorities().toArray()[0]);
        User user = userRepository.getUserByEmail(userD.getUsername());
        log.error("VV55 getAndCheckAuthenticatedUser: {} {} ", user.getEmail(),  user.getRoles());
        if (user == null) throw new UserUnauthorizedException();
        return user;
    }

    private void checkIfSamePasswords(PasswordDTO request, User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean sameEncryptedPass = encoder.matches(request.getNewPassword(), user.getPassword());
        log.error("VV11 sameEncryptedPass: {} for {}", sameEncryptedPass, request.getNewPassword());
        if (sameEncryptedPass) throw new PasswordSameException();
    }

    private void checkBreachPasswords(String newPass) {
        String[] breachedList = {"PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"};

        if (Arrays.toString(breachedList).contains(newPass)) throw new BreachedPasswordException();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);
        log.error("VV55 loadUserByUserName: {} {}", email, user);
        if (user == null) {
            log.error("VV55 loadUserByUserName:user null");
            throw new UsernameNotFoundException(email);
        }
        UserDetails result= new UserDetailsImpl(user);
        if(!result.isAccountNonLocked()) {
            log.error("VV55 loadUserByUserName:user locked");
            throw new  UserLockedException();
        }
        return result  ;
    }

    @Override
    public void updateUserIsAccountNonLocked(String email, boolean isAccountNonLocked, String operation, boolean isFromBruteForceOrigin) {
        User user = userRepository.getUserByEmail(email);
        log.error("VV55 updateUserIsAccountNonLocked: {} {} {}  ", email, isAccountNonLocked,operation);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        if (user.getRoles().contains("ROLE_ADMINISTRATOR") && operation.equals("LOCK")) {
            throw new BlockAdministratorException();
        }
        log.error("VV59 list user:"+ userRepository.findAll());
        User admin = userRepository.findById(1L).get();
        log.error("VV59 admin:"+ admin);
        switch (operation) {

            case "LOCK":
                log.error("VV59 log lock/unlock: {} {} {}",EventAction.LOCK_USER,isFromBruteForceOrigin,isFromBruteForceOrigin?admin.getEmail():user.getEmail());
                logger.log(0L, EventAction.LOCK_USER, !isFromBruteForceOrigin?admin.getEmail():user.getEmail(), "Lock user " + user.getEmail().toLowerCase(), "/api/admin/user/access");
                break;

            case "UNLOCK":
                log.error("VV59 log lock/unlock: {} {} {}",EventAction.UNLOCK_USER,isFromBruteForceOrigin,isFromBruteForceOrigin?admin.getEmail():user.getEmail());
                logger.log(0L, EventAction.UNLOCK_USER, !isFromBruteForceOrigin?admin.getEmail():user.getEmail(), "Unlock user " + user.getEmail().toLowerCase(), "/api/admin/user/access");
                break;

        }
        userRepository.updateUserIsAccountNonLocked(isAccountNonLocked, email);
    }
}


class UserDetailsImpl implements UserDetails {
    private final String username;
    private final String password;
    private final List<GrantedAuthority> rolesAndAuthorities;
    private final boolean accountNonLocked;

    public UserDetailsImpl(User user) {
        username = user.getEmail().toLowerCase(Locale.ROOT);
        password = user.getPassword();
        rolesAndAuthorities = user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r)).collect(Collectors.toList());
        this.accountNonLocked = user.isAccountNonLocked();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolesAndAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // 4 remaining methods that just return true
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}


