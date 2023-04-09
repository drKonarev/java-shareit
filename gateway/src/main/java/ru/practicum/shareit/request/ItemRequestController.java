package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;


    @GetMapping
    public ResponseEntity<Object> getMyOwnRequests(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemRequestClient.getAllItemRequestsByOwnerId(ownerId);
    }


    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                              @Valid @RequestBody ItemRequestDto request) {
        return itemRequestClient.save(request, ownerId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable(name = "requestId") Long requestId) {
        return itemRequestClient.getItemRequestById(requestId, userId);
    }


}
