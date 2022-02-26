package account.management.entities;

import lombok.*;

import javax.persistence.Id;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "user")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @NotBlank
    @Column
    private String name;

    @NotNull
    @NotBlank
    @Column
    private String lastname;

    @Column
    @Pattern(regexp = "[\\w.]+(@acme.com)$")
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Column
    private String password;

    @NotNull
    @Column
    @Convert(converter = StringListConverter.class)
    private List<String> roles;

    @Column
    @Builder.Default
    private boolean isAccountNonLocked = true;

}
