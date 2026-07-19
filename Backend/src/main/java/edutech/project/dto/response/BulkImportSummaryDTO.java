package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BulkImportSummaryDTO {
    private int totalRows;
    private int successCount;
    private int failureCount;
    private List<BulkImportRowResultDTO> results;
}