package org.s3m.userservice.family.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFamilyRequest {
    private String name;
    private String description;
}