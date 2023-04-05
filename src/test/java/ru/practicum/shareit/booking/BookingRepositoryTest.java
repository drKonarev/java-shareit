package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TestEntityManager em;

    User user1 = new User(null, "testName", "test1@mail.ru");
    User user2 = new User(null, "testNam2", "test2@mail.com");
    Item item1 = new Item(null, user1, "iTem1 purrrrr", "desc1 pam pam pam", true, null);
    Item item2 = new Item(null, user2, "iTem2 NAme", "desc2 CAAAAT", false, null);
    Booking booking1 = new Booking(null, item1, user1, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(6), BookingStatus.WAITING);
    Booking booking2 = new Booking(null, item2, user2, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusHours(6), BookingStatus.APPROVED);
    Booking booking3 = new Booking(null, item2, user2, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusHours(56), BookingStatus.APPROVED);

    @Test
    void contextLoads() {
        assertNotNull(em);
    }


    @Test
    void getLastBookingByItemIdTest() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);
        System.out.println(bookingRepository.findAll());
        Optional<Booking> actual = bookingRepository.getLastBookingByItemId(2);

        assertTrue(actual.isPresent());
        assertEquals(2L, actual.get().getId());
        assertEquals(BookingStatus.APPROVED, actual.get().getStatus());
    }

    @Test
    void getNextBookingByItemIdTest() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);
        System.out.println(bookingRepository.findAll());
        Optional<Booking> actual = bookingRepository.getNextBookingByItemId(2);
        System.out.println(actual);
        assertTrue(actual.isPresent());
        assertEquals(3L, actual.get().getId());
        assertEquals(BookingStatus.APPROVED, actual.get().getStatus());

    }

    @Test
    void findPastBookingByBooker_IdAndItem_IdTest() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);
        System.out.println(bookingRepository.findAll());
        List<Booking> actualList = bookingRepository
                .findPastBookingByBooker_IdAndItem_Id(2L, 2L);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(2L, actualList.get(0).getId());
    }

    @Test
    void findAllByBooker_IdOrderByStartDescTest() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);
        System.out.println(bookingRepository.findAll());
        List<Booking> actualList = bookingRepository.findAllByBooker_IdOrderByStartDesc(2L, Pageable.unpaged()).toList();
        System.out.println("\n" + actualList);
        assertFalse(actualList.isEmpty());
        assertEquals(2, actualList.size());
        assertEquals(3L, actualList.get(0).getId());
        assertEquals(2L, actualList.get(1).getId());
    }

    @Test
    void findBookingByItem_Owner_IdTest() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);
        System.out.println(bookingRepository.findAll());
        List<Booking> actualList = bookingRepository.findBookingByItem_Owner_Id(user2.getId(), Pageable.unpaged()).toList();

        assertFalse(actualList.isEmpty());
        assertEquals(2, actualList.size());
        assertEquals(item2.getId(), actualList.get(0).getItem().getId());
    }


}