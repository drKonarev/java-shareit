package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class ItemRequestServiceTest {
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    User user = new User(1L, "userName", "user@mail.com");
    ItemRequest itemRequest = new ItemRequest(1L, "desc", user, LocalDateTime.now());
    ItemDtoRequest itemDtoRequest = new ItemDtoRequest(1L, "name", "desc", true, 1L);
    ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "desc", LocalDateTime.now(), Collections.singletonList(itemDtoRequest));
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void getItemRequestById_whenUserAndItemRequestAreExists() {

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(eq(1L))).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto actual = itemRequestService.getItemRequestById(1L, 1L);

        verify(userRepository).findById(1L);
        verify(itemRequestRepository).findById(1L);
        assertEquals(itemRequestDto.toString(), actual.toString());

    }

    @Test
    void getItemRequestById_whenUserNotExist_thenThrowUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.getItemRequestById(1L, 1L));
    }

    @Test
    void getItemRequestById_whenItemRequestNotExist_thenThrowItemNotFound() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemRequestService.getItemRequestById(1L, 1L));
        verify(itemRequestRepository).findById(eq(1L));
    }

    @Test
    void save_whenUserExist_thenOk() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(itemRequestMapper.dtoToItemRequest(eq(itemRequestDto), eq(user))).thenReturn(itemRequest);
        when(itemRequestMapper.toDto(eq(itemRequest))).thenReturn(itemRequestDto);
        when(itemRequestRepository.save(eq(itemRequest))).thenReturn(itemRequest);

        ItemRequestDto actual = itemRequestService.save(itemRequestDto, 1L);

        verify(userRepository).findById(1L);
        verify(itemRequestRepository).save(any());
        verify(itemRequestMapper).dtoToItemRequest(any(), any());
        verify(itemRequestMapper).toDto(any());

        assertEquals(itemRequestDto.toString(), actual.toString());


    }

    @Test
    void save_whenUserNotExist_thenThrowUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.save(itemRequestDto, 1L));
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getAllItemRequestsByOwnerId_whenOwnerExist_thenOk() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByOwnerId(eq(1L))).thenReturn(Collections.singletonList(itemRequest));
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> actual = itemRequestService.getAllItemRequestsByOwnerId(1L);

        verify(userRepository).findById(1L);
        verify(itemRequestRepository).findAllByOwnerId(1L);
        assertEquals(Collections.singletonList(itemRequestDto).toString(), actual.toString());
    }

    @Test
    void getAllItemRequestsByOwnerId_whenOwnerNotExist_thenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.getAllItemRequestsByOwnerId(1L));
        verify(itemRequestRepository, never()).findAllByOwnerId(anyLong());
    }

    @Test
    void getAllItemRequests_whenRepositoryIsEmpty_thenReturnedEmptyList() {
        when(itemRequestRepository.findAllExtendUserId(1L, PageRequest.of(0, 10))).thenReturn(Page.empty());
        List<ItemRequestDto> actual = itemRequestService.getAllItemRequests(1L, 0, 10);

        assertEquals(Collections.emptyList(), actual);
        verify(itemRequestRepository).findAllExtendUserId(any(), any());
        verify(itemRequestMapper, never()).toDto(any());
    }

    @Test
    void getAllItemRequests_whenRepositoryIsNotEmpty_thenReturnedSingletonList() {
        when(itemRequestRepository.findAllExtendUserId(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<ItemRequest>(Collections.singletonList(itemRequest)));

        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> actual = itemRequestService.getAllItemRequests(1L, 0, 10);

        assertEquals(Collections.singletonList(itemRequestDto), actual);
        verify(itemRequestRepository).findAllExtendUserId(any(), any());
        verify(itemRequestMapper).toDto(itemRequest);
    }
}