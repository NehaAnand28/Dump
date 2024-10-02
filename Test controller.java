import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditController.class)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditService auditService;

    @Autowired
    private ObjectMapper objectMapper;

    // Test for search audit transactions with pagination
    @Test
    void searchAuditTransactions_shouldReturnPaginatedResponse_whenCriteriaIsValid() throws Exception {
        AuditTrans auditTransCriteria = new AuditTrans();  // Assuming this is the object for search criteria
        int pageNumber = 1;
        int pageSize = 5;

        List<AuditTrans> mockTransactions = Arrays.asList(new AuditTrans(), new AuditTrans());

        PaginatedAuditTransResponse mockResponse = new PaginatedAuditTransResponse();
        mockResponse.setAuditTransList(mockTransactions);
        mockResponse.setPageNumber(pageNumber);
        mockResponse.setPageSize(pageSize);
        mockResponse.setTotalCount(10L);  // Assuming total count is 10 from the query

        when(auditService.searchAuditTransactions(any(AuditTrans.class), eq(pageNumber), eq(pageSize)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/audit/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNumber", String.valueOf(pageNumber))
                .param("pageSize", String.valueOf(pageSize))
                .content(objectMapper.writeValueAsString(auditTransCriteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auditTransList.length()").value(mockTransactions.size()))
                .andExpect(jsonPath("$.pageNumber").value(pageNumber))
                .andExpect(jsonPath("$.pageSize").value(pageSize))
                .andExpect(jsonPath("$.totalCount").value(10));
    }

    // Additional test for pagination defaults (page number 1, page size 5)
    @Test
    void searchAuditTransactions_shouldUseDefaultPagination_whenNoPageParamsProvided() throws Exception {
        AuditTrans auditTransCriteria = new AuditTrans();
        int defaultPageNumber = 1;
        int defaultPageSize = 5;

        List<AuditTrans> mockTransactions = Arrays.asList(new AuditTrans(), new AuditTrans());

        PaginatedAuditTransResponse mockResponse = new PaginatedAuditTransResponse();
        mockResponse.setAuditTransList(mockTransactions);
        mockResponse.setPageNumber(defaultPageNumber);
        mockResponse.setPageSize(defaultPageSize);
        mockResponse.setTotalCount(10L);

        when(auditService.searchAuditTransactions(any(AuditTrans.class), eq(defaultPageNumber), eq(defaultPageSize)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/audit/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auditTransCriteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auditTransList.length()").value(mockTransactions.size()))
                .andExpect(jsonPath("$.pageNumber").value(defaultPageNumber))
                .andExpect(jsonPath("$.pageSize").value(defaultPageSize))
                .andExpect(jsonPath("$.totalCount").value(10));
    }
}
