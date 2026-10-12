package ru.yandex.practicum.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.request.Request;
import ru.yandex.practicum.user.User;

@Entity
@Table(name = "items")
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name can't be empty")
    @Column(nullable = false, length = 225)
    private String name;

    @NotBlank(message = "Description can't be empty")
    @Column(nullable = false, length = 512)
    private String description;

    @NotNull(message = "Owner can't be empty")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private User owner;

    @NotNull(message = "Available can't be empty")
    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    private Request request;
}