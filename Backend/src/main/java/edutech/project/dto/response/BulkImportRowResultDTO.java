package edutech.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkImportRowResultDTO {
    private int rowNumber;
    private boolean success;
    private String message;
    private String generatedUsername;
    private String generatedPassword;
    private String name;
    private String email;
    private String role;
}