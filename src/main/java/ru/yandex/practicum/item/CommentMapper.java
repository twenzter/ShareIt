package ru.yandex.practicum.item;

import ru.yandex.practicum.item.dto.CommentDto;
import ru.yandex.practicum.item.dto.NewCommentRequest;
import ru.yandex.practicum.user.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(NewCommentRequest comment, Item item, User user) {
        return new Comment(null,
                comment.getText(),
                item,
                user,
                LocalDateTime.now());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}
