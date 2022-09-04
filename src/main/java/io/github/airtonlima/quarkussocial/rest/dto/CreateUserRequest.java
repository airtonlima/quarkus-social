package io.github.airtonlima.quarkussocial.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data // é igual colocar @Getter & @Setter
public class CreateUserRequest {
    @NotBlank(message="Name is required")
    private String name;
    @NotNull(message="Age is required")
    private Integer age;
}
