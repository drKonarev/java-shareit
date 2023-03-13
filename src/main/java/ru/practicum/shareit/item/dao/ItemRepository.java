package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemByOwnerId(Long userId);

 @Query("select it from Item as it where (LOWER(it.name) " +
         " like %?1% or LOWER(it.description) like %?1%)" +
         " and it.available=true")
 List<Item> searchByDescriptionAndName(String text);


}
