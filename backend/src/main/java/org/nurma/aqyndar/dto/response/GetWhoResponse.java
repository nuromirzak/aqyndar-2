package org.nurma.aqyndar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetWhoResponse {
    private int id;
    private String email;
    private String firstName;
    private Date createdAt;
    private List<String> roles;
}
