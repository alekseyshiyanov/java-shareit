package ru.practicum.shareit.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="comments", schema="public")@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comments_id")
    Long id;

    @Column(name="text", nullable = false)
    String text;

    @OneToOne
    @JoinColumn(name="item_id")
    Item item;

    @OneToOne
    @JoinColumn(name="author_id")
    User author;

    @Column(name="created", nullable = false)
    LocalDateTime created;
}
