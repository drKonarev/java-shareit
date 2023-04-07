package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping
    public List<ItemRequestDto> getMyOwnRequests(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemRequestService.getAllItemRequestsByOwnerId(ownerId);
    }


    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @RequestBody ItemRequestDto request) {
        return itemRequestService.save(request, ownerId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(name = "size", required = false, defaultValue = "10") @Min(1) @Max(100) Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable(required = true, name = "requestId") Long requestId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }


}
