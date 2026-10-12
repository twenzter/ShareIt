package ru.yandex.practicum.booking;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.item.Item;
import ru.yandex.practicum.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @ToString.Exclude
    @JoinColumn(name = "item_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Item item;

    @ToString.Exclude
    @JoinColumn(name = "booker_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
}
