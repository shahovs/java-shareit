package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private ItemDto itemDto;
    private ItemInfoDto itemInfoDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("itemDtoName");
        itemDto.setDescription("itemDtoDescription");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        itemInfoDto = new ItemInfoDto();
        itemInfoDto.setId(1L);
        itemInfoDto.setName("itemInfoDtoName");
        itemInfoDto.setDescription("itemInfoDtoDescription");
        itemInfoDto.setAvailable(true);

        ItemInfoDto.BookingInfoDto lastBookingInfoDto = new ItemInfoDto.BookingInfoDto();
        lastBookingInfoDto.setId(1L);
        lastBookingInfoDto.setStart(LocalDateTime.of(2000, 12, 31, 23, 59, 59));
        lastBookingInfoDto.setEnd(LocalDateTime.of(2030, 12, 31, 23, 59, 59));
        lastBookingInfoDto.setBookerId(1L);

        ItemInfoDto.BookingInfoDto nextBookingInfoDto = new ItemInfoDto.BookingInfoDto();
        nextBookingInfoDto.setId(2L);
        nextBookingInfoDto.setStart(LocalDateTime.of(2030, 12, 31, 23, 59, 59));
        nextBookingInfoDto.setEnd(LocalDateTime.of(2032, 12, 31, 23, 59, 59));
        nextBookingInfoDto.setBookerId(1L);

        itemInfoDto.setLastBooking(lastBookingInfoDto);
        itemInfoDto.setNextBooking(nextBookingInfoDto);

        ItemInfoDto.CommentInfoDto commentInfoDto = new ItemInfoDto.CommentInfoDto();
        commentInfoDto.setId(1L);
        commentInfoDto.setText("text");
        commentInfoDto.setItemId(1L);
        commentInfoDto.setAuthorName("AuthorName");
        commentInfoDto.setCreated(LocalDate.of(2000, 12, 31));
        itemInfoDto.setComments(Collections.singletonList(commentInfoDto));
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(itemService.getItemById(1L, 1L))
                .thenReturn(itemInfoDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemInfoDto.getName())))
                .andExpect(jsonPath("$.description", is(itemInfoDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemInfoDto.getAvailable())));

        verify(itemService, times(1))
                .getItemById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getAllItemsByOwnerId() {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1))
                .getAllItemsByOwnerId(1L, PageRequest.ofSize(10));
    }

    @SneakyThrows
    @Test
    void createItem() {
        when(itemService.createItem(any(), anyLong()))
                .thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService, times(1))
                .createItem(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItem() {
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService, times(1))
                .updateItem(anyLong(), any(), anyLong());
    }

    @SneakyThrows
    @Test
    void findItems() {
        mockMvc.perform(get("/items/search/")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "someText"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1))
                .findItems("someText", PageRequest.ofSize(10));
    }

    @SneakyThrows
    @Test
    void createComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("someText");
        commentDto.setAuthorName("AuthorName");
        commentDto.setCreated(LocalDate.now());

        when(itemService.createComment(anyLong(), any(), anyLong()))
                .thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/1/comment/")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
        verify(itemService, times(1))
                .createComment(anyLong(), any(), anyLong());
    }

}