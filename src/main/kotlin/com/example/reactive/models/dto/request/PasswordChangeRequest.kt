package com.example.reactive.models.dto.request

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
data class PasswordChangeRequest (
    var username: String,
    var oldPassword: String,
    var newPassword: String
)