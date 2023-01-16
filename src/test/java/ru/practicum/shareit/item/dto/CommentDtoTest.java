package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDate;

@JsonTest
class CommentDtoTest {

    @Autowired
    JacksonTester<CommentDto> json;

    @SneakyThrows
    @Test
    void serializeTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("text");
        commentDto.setAuthorName("authorName");
        commentDto.setCreated(LocalDate.of(2000, 12, 31));

        JsonContent<CommentDto> result = json.write(commentDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(commentDto.getId().intValue());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
        Assertions.assertThat(result).extractingJsonPathValue("$.created")
                .isEqualTo(commentDto.getCreated().toString());
    }

}