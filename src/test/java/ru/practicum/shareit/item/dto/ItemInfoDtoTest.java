package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemInfoDtoTest {

    @Autowired
    private JacksonTester<ItemInfoDto> json;

    @SneakyThrows
    @Test
    void serializeTest() {
        ItemInfoDto itemInfoDto = new ItemInfoDto();
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

        JsonContent<ItemInfoDto> result = json.write(itemInfoDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemInfoDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemInfoDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemInfoDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemInfoDto.getAvailable());

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(itemInfoDto.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.lastBooking.start")
                .isEqualTo(itemInfoDto.getLastBooking().getStart().toString());
        assertThat(result).extractingJsonPathValue("$.lastBooking.end")
                .isEqualTo(itemInfoDto.getLastBooking().getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(itemInfoDto.getLastBooking().getBookerId().intValue());

        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(itemInfoDto.getNextBooking().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.nextBooking.start")
                .isEqualTo(itemInfoDto.getNextBooking().getStart().toString());
        assertThat(result).extractingJsonPathValue("$.nextBooking.end")
                .isEqualTo(itemInfoDto.getNextBooking().getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(itemInfoDto.getNextBooking().getBookerId().intValue());
    }

}