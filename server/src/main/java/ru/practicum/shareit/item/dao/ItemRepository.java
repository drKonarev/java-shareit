package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select it from Item as it where it.owner.id=?1 order by it.id desc ")
    Page<Item> findItemByOwnerId(Long userId, Pageable pageable);

    @Query(value = "select it from Item as it where (LOWER(it.name) " +
            " like %?1% or LOWER(it.description) like %?1%)" +
            " and it.available=true",
            countQuery = "select count(it) from Item as it where (LOWER(it.name) " +
                    " like %?1% or LOWER(it.description) like %?1%)" +
                    " and it.available=true")
    Page<Item> searchByDescriptionAndName(String text, Pageable pageable);

    List<Item> findAllByRequest_Id(Long requestId);


}
