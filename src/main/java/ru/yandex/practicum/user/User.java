package ru.yandex.practicum.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "email can not be empty")
    @Email(message = "email must be in email format")
    @Column(nullable = false, length = 512, unique = true)
    private String email;

    @NotBlank(message = "name can not be empty")
    @Column(nullable = false, length = 225)
    private String name;
}