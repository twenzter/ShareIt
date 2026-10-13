package ru.yandex.practicum.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "description can't be empty")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "request can't be empty")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    private LocalDateTime created;
}
