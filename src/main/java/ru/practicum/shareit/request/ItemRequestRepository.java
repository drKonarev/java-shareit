package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByOwnerId(Long ownerId);

    @Query(nativeQuery = true,
            value = "select * from item_requests as ir where owner_id !=?1",
            countQuery = "select count(*) from item_requests as ir where owner_id !=?1")
    Page<ItemRequest> findAllExtendUserId(Long userId, Pageable pageable);
}

