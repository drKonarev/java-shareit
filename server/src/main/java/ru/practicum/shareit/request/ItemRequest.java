package ru.practicum.shareit.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @NotBlank
    @Column(name = "description")
    private String description;


    @ManyToOne
    //@JoinColumn(name = "owner_id")
    @JoinColumn(name = "requestor_id")
    private User owner;


    //@Column(name = "created_time")
    @Column(name = "created")
    @DateTimeFormat(pattern = "yyyy/MM/dd hh:mm:ss")
    private LocalDateTime created;
}
