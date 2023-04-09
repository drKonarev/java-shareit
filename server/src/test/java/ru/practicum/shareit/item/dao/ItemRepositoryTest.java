package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {
    PageRequest p = PageRequest.of(0, 20);
    User user1 = new User(null, "testName", "test1@mail.ru");
    User user2 = new User(null, "testNam2", "test2@mail.com");
    ItemRequest itemRequest = new ItemRequest(null, "Xo4u sdat' rabotu", user1, LocalDateTime.now());
    Item item2 = new Item(null, user2, "iTem2 NAme", "desc2 CAAAAT", false, itemRequest);
    Item item1 = new Item(null, user1, "iTem1 purrrrr", "desc1 pam pam pam", true, null);
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TestEntityManager em;

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findItemByOwnerId() {
        em.persist(user1);
        em.persist(user2);
        em.persist(itemRequest);
        em.persist(item1);
        em.persist(item2);
        List<Item> actualItem = itemRepository.findItemByOwnerId(user1.getId(), p).toList();

        assertFalse(actualItem.isEmpty());

        assertEquals(1, actualItem.size());
        assertEquals("iTem1 purrrrr", actualItem.get(0).getName());
    }

    @Test
    void searchByDescriptionAndName() {
        em.persist(user1);
        em.persist(user2);
        em.persist(itemRequest);
        em.persist(item1);
        em.persist(item2);

        List<Item> actualItem = itemRepository.searchByDescriptionAndName("pam", p).toList();
        assertFalse(actualItem.isEmpty());

        assertEquals(1, actualItem.size());
        assertEquals("desc1 pam pam pam", actualItem.get(0).getDescription());

    }

    @Test
    void findAllByRequest_Id() {
        em.persist(user1);
        em.persist(user2);
        em.persist(itemRequest);
        em.persist(item1);
        em.persist(item2);

        List<Item> actualItem = itemRepository.findAllByRequest_Id(itemRequest.getId());

        assertFalse(actualItem.isEmpty());

        assertEquals(1, actualItem.size());
        assertEquals("desc2 CAAAAT", actualItem.get(0).getDescription());

    }
}