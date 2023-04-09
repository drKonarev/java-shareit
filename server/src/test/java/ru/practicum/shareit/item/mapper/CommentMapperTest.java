package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    CommentMapper commentMapper = new CommentMapper();
    User user = new User(1L, "name", "mail@mail.com");

    Item item = new Item();
    Comment comment = new Comment(1L, "opisanie", user, item, LocalDateTime.now());

    CommentDto commentDto = new CommentDto(1L, "opisanie", "name", LocalDateTime.now());

    @Test
    void toDto() {
        CommentDto expectedComment = new CommentDto(1L, "opisanie", "name", comment.getCreated());

        CommentDto actualComment = commentMapper.toDto(comment);

        assertEquals(expectedComment.toString(), actualComment.toString());
    }

    @Test
    void dtoToComment() {
        Comment actualComment = commentMapper.dtoToComment(commentDto, user, item);

        Comment expectedComment = new Comment(1L, "opisanie", user, item, actualComment.getCreated());

        assertEquals(expectedComment.toString(), actualComment.toString());
    }

}