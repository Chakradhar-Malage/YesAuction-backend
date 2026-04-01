package com.Chakradhar.YesAuction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateProfileResponse {
    private String username;
    private String email;
    private String message;
}