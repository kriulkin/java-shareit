package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    long id;

    @Column
    String name;

    @Column
    String description;

    @Column
    Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    User user;

    @Column(name = "request_id")
    Long requestId;

    public Item(long id, String name, String description, Boolean available, User user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.user = user;
    }
}
