package com.example.reactive.models.dto.request

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Data
@NoArgsConstructor
@AllArgsConstructor
data class AuthRequest (
    @field:Size(min = 5, max = 255)
    @field:NotBlank
    var username: String,
    @field:NotBlank
    @field:Size(min = 8, max = 255)
    var password: String
)