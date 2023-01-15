package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    JacksonTester<BookingDto> json;

    @SneakyThrows
    @Test
    void serializeTest() {
        BookingDto bookingDto = new BookingDto();
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

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.start")
                .isEqualTo(bookingDto.getStart().toString());
        assertThat(result).extractingJsonPathValue("$.end")
                .isEqualTo(bookingDto.getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingDto.getItemId().intValue());
        assertThat(result).extractingJsonPathValue("$.status")
                .isEqualTo(bookingDto.getStatus().toString());

        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(bookingDto.getItem().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingDto.getItem().getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo(bookingDto.getItem().getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(bookingDto.getItem().getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.item.ownerId")
                .isEqualTo(bookingDto.getItem().getOwnerId().intValue());

        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookingDto.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(bookingDto.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo(bookingDto.getBooker().getEmail());
    }

}