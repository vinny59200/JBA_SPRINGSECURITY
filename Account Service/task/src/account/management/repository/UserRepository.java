package account.management.repository;

import account.management.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;


public interface UserRepository extends JpaRepository<User, Long> {

    default boolean existsByEmail(String email) {
        return this.findAll().stream().anyMatch(user -> user.getEmail().equals(email.toLowerCase()));
    }

    default User getUserByEmail(String email) {
        User result= this.findAll()
                .stream()
                .filter(user -> user.getEmail().equals(email.toLowerCase(Locale.ROOT)))
                .findFirst()
                .orElse(null);
        if(result!=null)System.out.println("VV65 user: "+result.getEmail()+" + "+result.getRoles());
        return result;
    }
    @Modifying
    @Transactional
    @Query("update user set isAccountNonLocked = ?1 where email = ?2")
    void updateUserIsAccountNonLocked (boolean isAccountNonLocked, String email);
    long deleteByEmail(String username);
}
