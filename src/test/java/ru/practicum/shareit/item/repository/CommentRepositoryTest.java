package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void findAllByItem() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("ownerName");
        owner.setEmail("owner@email");

        User author = new User();
        author.setId(2L);
        author.setName("authorName");
        author.setEmail("author@email");

        owner = userRepository.save(owner);
        author = userRepository.save(author);

        Item item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDate.of(2000, 12, 31));
        comment = commentRepository.save(comment);

        List<Comment> allCommentsByItem = commentRepository.findAllByItem(item);

        assertNotNull(allCommentsByItem);
        assertEquals(1, allCommentsByItem.size());
        Comment result = allCommentsByItem.get(0);
        assertEquals(comment, result);

    }

}