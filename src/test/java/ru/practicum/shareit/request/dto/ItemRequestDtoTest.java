package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @SneakyThrows
    @Test
    void serializeTest() {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
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

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathValue("$.created")
                .isEqualTo(itemRequestDto.getCreated().toString());

        assertThat(result).extractingJsonPathNumberValue("$.requestorDto.id")
                .isEqualTo(itemRequestDto.getRequestorDto().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.requestorDto.name")
                .isEqualTo(itemRequestDto.getRequestorDto().getName());
        assertThat(result).extractingJsonPathStringValue("$.requestorDto.email")
                .isEqualTo(itemRequestDto.getRequestorDto().getEmail());
    }

}