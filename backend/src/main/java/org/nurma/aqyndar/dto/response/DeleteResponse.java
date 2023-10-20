package org.nurma.aqyndar.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteResponse {
    private final String status = "success";
}
