package ru.practicum.explore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.validation.Ip;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitRequestDto {
    @NotBlank(message = "App cannot be blank")
    private String app;

    @NotBlank(message = "Uri cannot be blank")
    private String uri;

    @NotBlank(message = "IP cannot be blank")
    //@Ip(message = "Invalid IP")
    private String ip;

    @NotNull(message = "Timestamp cannot be blank")
    @Past(message = "Timestamp must be in past")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
