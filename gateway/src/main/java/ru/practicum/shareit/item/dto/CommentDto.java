package ru.practicum.shareit.item.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.markers.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class CommentDto {

    private Long id;

    @NotNull(groups = Create.class)
    @NotBlank(groups = Create.class)
    private String text;

    private String authorName;

    @DateTimeFormat(pattern = "yyyy/MM/dd hh:mm:ss")
    private LocalDateTime created;
}
