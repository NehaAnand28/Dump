import com.yourcompany.yourproject.domain.Transaction;
import com.yourcompany.yourproject.integration.repository.TransactionJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionJdbcRepository transactionJdbcRepository;

    public List<Map<String, Object>> findTransactions(Transaction searchCriteria) {
        // Check if all search fields are null or empty
        boolean isCriteriaEmpty = (searchCriteria.getId() == null) &&
                                  !StringUtils.hasText(searchCriteria.getReferenceNumber()) &&
                                  !StringUtils.hasText(searchCriteria.getCtcMessage()) &&
                                  !StringUtils.hasText(searchCriteria.getApplicationId());

        // If all fields are empty, use default statuses for searching
        List<String> statuses;
        if (isCriteriaEmpty) {
            statuses = Arrays.asList("USER_APPROVED", "SYSTEM_APPROVED", "PENDING_REVIEW", "REJECTED");
        } else {
            // Else use the status provided in the search criteria (assuming you may pass it in the service call)
            statuses = Collections.emptyList(); // Or you can decide the logic for the statuses here
        }

        return transactionJdbcRepository.searchTransactions(searchCriteria, statuses);
    }
}
