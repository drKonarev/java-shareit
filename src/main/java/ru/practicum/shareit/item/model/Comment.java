package ru.practicum.shareit.item.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @DateTimeFormat(pattern = "yyyy/MM/dd hh:mm:ss")
    @Column(name = "created")
    private LocalDateTime created;

}
