package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {"db.name=test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
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


        Item item1 = new Item();
        Item item2 = new Item();

        item1.setName("iTem1 purrrrr");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1.setDescription("desc1 pam pam pam");

        item2.setName("iTem2 NAme");
        item2.setAvailable(false);
        item2.setOwner(user2);
        item2.setDescription("desc2 CAAAAT");


        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        Booking booking3 = new Booking();

        booking1.setItem(item1);
        booking1.setBooker(user1);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().plusHours(5));

        booking2.setItem(item2);
        booking2.setBooker(user1);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setStart(LocalDateTime.now().minusDays(1));
        booking2.setEnd(LocalDateTime.now().minusHours(6));

        booking3.setItem(item2);
        booking3.setBooker(user2);
        booking3.setStatus(BookingStatus.APPROVED);
        booking3.setStart(LocalDateTime.now().plusDays(1));
        booking3.setEnd(LocalDateTime.now().plusHours(36));

        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

    }


    @Test
    void contextLoads() {
        assertNotNull(em);
    }


    @Test
    void getLastBookingByItemIdTest() {
        System.out.println("\n" + bookingRepository.findAll() + "\n");
        Optional<Booking> actual = bookingRepository.getLastBookingByItemId(2);

        assertTrue(actual.isPresent());
        assertEquals(2L, actual.get().getId());
        assertEquals(BookingStatus.APPROVED, actual.get().getStatus());
    }

    @Test
    void getNextBookingByItemIdTest() {
        System.out.println("\n" + bookingRepository.findAll() + "\n");
        Optional<Booking> actual = bookingRepository.getNextBookingByItemId(2);
        assertTrue(actual.isPresent());
        assertEquals(3L, actual.get().getId());
        assertEquals(BookingStatus.APPROVED, actual.get().getStatus());

    }

    @Test
    void findPastBookingByBooker_IdAndItem_IdTest() {
        System.out.println("\n" + bookingRepository.findAll() + "\n");
        List<Booking> actualList = bookingRepository
                .findPastBookingByBooker_IdAndItem_Id(2L, 1L);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(2L, actualList.get(0).getId());
    }

    @Test
    void findAllByBooker_IdOrderByStartDescTest() {
        System.out.println("\n" + bookingRepository.findAll() + "\n");
        List<Booking> actualList = bookingRepository.findAllByBooker_IdOrderByStartDesc(1L, Pageable.unpaged()).toList();
        assertFalse(actualList.isEmpty());
        assertEquals(2, actualList.size());
        assertEquals(1L, actualList.get(0).getId());
        assertEquals(2L, actualList.get(1).getId());
    }

    @Test
    void findBookingByItem_Owner_IdTest() {
        System.out.println("\n" + bookingRepository.findAll() + "\n");
        List<Booking> actualList = bookingRepository.findBookingByItemOwner_Id(2L, Pageable.unpaged()).toList();
        System.out.println("\n" + "!!!!!!" + "\n" + actualList);
        assertFalse(actualList.isEmpty());
        assertEquals(2, actualList.size());
        assertEquals(3L, actualList.get(0).getId());
        assertEquals(2L, actualList.get(1).getId());
    }


}