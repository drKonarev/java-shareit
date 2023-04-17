package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)

class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    public void setUp() {
        User user1 = new User();
        User user2 = new User();

        user1.setName("testName1");
        user1.setEmail("test1@mail.com");
        user2.setName("testName2");
        user2.setEmail("test25@mail.ru");

        ItemRequest itemRequest1 = new ItemRequest();
        ItemRequest itemRequest2 = new ItemRequest();

        itemRequest1.setOwner(user1);
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("desc1");

        itemRequest2.setOwner(user2);
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("desc2");

        em.persist(user1);
        em.persist(user2);
        em.persist(itemRequest1);
        em.persist(itemRequest2);
    }


    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findAllByOwnerId() {
        List<ItemRequest> actualList = itemRequestRepository.findAllByOwnerId(1L);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals("desc1", actualList.get(0).getDescription());
    }

    @Test
    void findAllExtendUserId() {
        List<ItemRequest> actualList = itemRequestRepository.findAllExtendUserId(1L, PageRequest.of(0, 10)).toList();

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals("desc2", actualList.get(0).getDescription());
    }


}