package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.sevice.BookingService;
import ru.practicum.shareit.exception.ItemIsNotAvailableException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2030, 12, 31, 23, 59, 59));
        bookingDto.setEnd(LocalDateTime.of(2031, 12, 31, 23, 59, 59));
        bookingDto.setItemId(1L);
        bookingDto.setStatus(BookingStatus.WAITING);

        BookingDto.ItemDto itemDto = new BookingDto.ItemDto();
        itemDto.setId(1L);
        itemDto.setName("itemName");
        itemDto.setDescription("itemDescription");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(1L);
        bookingDto.setItem(itemDto);

        BookingDto.UserDto bookerDto = new BookingDto.UserDto();
        bookerDto.setId(2L);
        bookerDto.setName("bookerName");
        bookerDto.setEmail("booker@Email");
        bookingDto.setBooker(bookerDto);
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        when(bookingService.getBookingById(1L, 1L))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(bookingDto));
        verify(bookingService, times(1))
                .getBookingById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getAllBookingByBookerId() {
        List<BookingDto> bookingDtos = Collections.singletonList(bookingDto);
        when(bookingService.getAllBookingsByBookerId(1L, BookingState.ALL, 0, 10))
                .thenReturn(bookingDtos);

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(bookingDtos));
        verify(bookingService, times(1))
                .getAllBookingsByBookerId(1L, BookingState.ALL, 0, 10);
    }

    @SneakyThrows
    @Test
    void getAllBookingByBookerId_whenWrongState_thenThrowException() {
        mockMvc.perform(get("/bookings/?state=WRONG_STATE")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(bookingService, never())
                .getAllBookingsByBookerId(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllBookingByOwnerId() {
        List<BookingDto> bookingDtos = Collections.singletonList(bookingDto);
        when(bookingService.getAllBookingsByOwnerId(1L, BookingState.ALL, 0, 10))
                .thenReturn(bookingDtos);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(bookingDtos));
        verify(bookingService, times(1))
                .getAllBookingsByOwnerId(1L, BookingState.ALL, 0, 10);
    }

    @SneakyThrows
    @Test
    void createBooking() {
        when(bookingService.createBooking(any(), anyLong()))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(bookingDto));
        verify(bookingService, times(1))
                .createBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void createBooking_whenItemIsNotAvailableException() {
        when(bookingService.createBooking(any(), anyLong()))
                .thenThrow(new ItemIsNotAvailableException("Вещь не доступна для бронирования"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void changeStatusOfBooking() {
        when(bookingService.changeStatusOfBooking(1L, 1L, true))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/1/?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(bookingDto));
        verify(bookingService, times(1))
                .changeStatusOfBooking(1L, 1L, true);
    }

}