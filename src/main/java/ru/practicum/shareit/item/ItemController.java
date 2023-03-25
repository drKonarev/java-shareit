package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {


    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDtoBooking> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(name = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(name = "size", required = false, defaultValue = "10") @Min(1) @Max(100) Integer size) {
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") @NotBlank @NotNull String text,
                                @RequestParam(name = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                @RequestParam(name = "size", required = false, defaultValue = "10") @Min(1) @Max(100) Integer size) {
        return itemService.search(text.toLowerCase(), from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long itemId) {
        return itemService.getItem(itemId, userId);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") Long userId,
                         @Validated({Update.class}) @RequestBody ItemDto itemDto,
                         @PathVariable long itemId) {
        return itemService.patch(userId, itemDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId,
                                    @Validated({Create.class}) @RequestBody CommentDto comment) {
        return itemService.createComment(comment, userId, itemId);
    }


}
