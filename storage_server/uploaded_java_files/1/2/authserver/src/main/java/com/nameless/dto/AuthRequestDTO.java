package com.nameless.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDTO {

  @NotBlank(message = "Username cannot be null or blank")
  private String username;  // Changed from email to username

  @NotBlank(message = "Password cannot be null or blank")
  private String password;
}
