package ru.yandex.practicum.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "text can't be empty")
    @Column(nullable = false, length = 512)
    private String text;

    @NotNull(message = "item can't be empty")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @NotNull(message = "author can't be empty")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
