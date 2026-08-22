package com.raid.lmee.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ClubDTO {

    private UUID id;

    @NotNull
    @NotBlank(message = "Club name cannot be blank")
    @Size(max = 255)
    private String name;

    @NotNull
    @NotBlank(message = "Club country cannot be blank")
    @Size(max = 255)
    private String country;

}
