package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select * from bookings  as b , items as i   " +
            " where b.item_id=i.id and b.item_id = ?1 and b.status like 'APPROVED' and start_time < now() " +
            " order by  b.end_time DESC " +
            "limit 1", nativeQuery = true)
    Optional<Booking> getLastBookingByItemId(long itemId);

    @Query(value = "select * from bookings  as b , items as i   " +
            " where b.item_id=i.id and b.item_id = ?1 and " +
            "b.status like 'APPROVED'   and start_time>= now()" +
            "order by  b.start_time ASC " +
            "limit 1", nativeQuery = true)
    Optional<Booking> getNextBookingByItemId(long itemId);

    @Query(value = "select * from bookings as b where b.item_id = ?1" +
            " and b.booker_id=?2 and status like 'APPROVED' " +
            "and end_time<now()", nativeQuery = true)
    List<Booking> findPastBookingByBooker_IdAndItem_Id(Long itemId, Long bookerId);

    Page<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);


    @Query(value = "select * from bookings  as b , items as i , users as u  " +
            " where b.item_id=i.id and i.user_id=u.id and u.id = ?1 " +
            "order by  b.start_time DESC ", nativeQuery = true,
            countQuery = "select count(*) from bookings  as b , items as i , users as u  " +
                    " where b.item_id=i.id and i.user_id=u.id and u.id = ?1")
    Page<Booking> findBookingByItem_Owner_Id(Long ownerId, Pageable pageable);
}
