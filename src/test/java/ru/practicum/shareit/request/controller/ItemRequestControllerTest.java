package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.sevice.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("itemRequestDescription");
        itemRequestDto.setCreated(LocalDateTime.of(2000, 12, 31, 23, 59, 59));

        ItemRequestDto.RequestorDto requesterDto = new ItemRequestDto.RequestorDto();
        requesterDto.setId(1L);
        requesterDto.setName("requesterName");
        requesterDto.setEmail("requester@email");
        itemRequestDto.setRequestorDto(requesterDto);

        ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
        itemDto.setId(1L);
        itemDto.setName("itemName");
        itemDto.setDescription("itemDescription");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        itemRequestDto.setItems(List.of(itemDto));
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        when(itemRequestService.getItemRequestById(1L, 1L))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(itemRequestDto));
        verify(itemRequestService, times(1))
                .getItemRequestById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getItemRequestsByRequester() {
        List<ItemRequestDto> itemRequestDtos = Collections.singletonList(itemRequestDto);
        when(itemRequestService.getItemRequestsByRequester(1L))
                .thenReturn(itemRequestDtos);

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(itemRequestDtos));
        verify(itemRequestService, times(1))
                .getItemRequestsByRequester(1L);
    }

    @SneakyThrows
    @Test
    void getItemRequestsOfOtherRequesters() {
        List<ItemRequestDto> itemRequestDtos = Collections.singletonList(itemRequestDto);
        when(itemRequestService.getItemRequestsOfOtherRequesters(1L, 0, 10))
                .thenReturn(itemRequestDtos);

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(itemRequestDtos));
        verify(itemRequestService, times(1))
                .getItemRequestsOfOtherRequesters(1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void createItemRequest() {
        when(itemRequestService.createItemRequest(any(), anyLong()))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(itemRequestDto));
        verify(itemRequestService, times(1))
                .createItemRequest(any(), anyLong());
    }

}