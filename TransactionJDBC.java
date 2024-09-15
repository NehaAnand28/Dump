package com.yourcompany.yourproject.integration.repository;

import com.yourcompany.yourproject.domain.Transaction;
import com.yourcompany.yourproject.integration.queries.TransactionQueries;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TransactionJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> searchTransactions(Transaction searchCriteria) {
        StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE 1=1");

        // Build the query based on non-null search criteria
        if (searchCriteria.getId() != null) {
            query.append(" AND id = ?");
        }
        if (searchCriteria.getReferenceNumber() != null) {
            query.append(" AND reference_number = ?");
        }
        if (searchCriteria.getCtcMessage() != null) {
            query.append(" AND ctc_message LIKE ?");
        }
        if (searchCriteria.getApplicationId() != null) {
            query.append(" AND application_id = ?");
        }
        if (searchCriteria.getTransactionStatus() != null && !searchCriteria.getTransactionStatus().isEmpty()) {
            query.append(" AND transaction_status IN (")
                 .append(String.join(",", searchCriteria.getTransactionStatus().stream().map(s -> "?").toArray(String[]::new)))
                 .append(")");
        }

        // Execute the query
        return jdbcTemplate.queryForList(query.toString(), buildParams(searchCriteria));
    }

    private Object[] buildParams(Transaction searchCriteria) {
        // Build the parameters array based on search criteria
        List<Object> params = new ArrayList<>();
        if (searchCriteria.getId() != null) params.add(searchCriteria.getId());
        if (searchCriteria.getReferenceNumber() != null) params.add(searchCriteria.getReferenceNumber());
        if (searchCriteria.getCtcMessage() != null) params.add("%" + searchCriteria.getCtcMessage() + "%");
        if (searchCriteria.getApplicationId() != null) params.add(searchCriteria.getApplicationId());
        if (searchCriteria.getTransactionStatus() != null && !searchCriteria.getTransactionStatus().isEmpty()) {
            params.addAll(searchCriteria.getTransactionStatus());
        }
        return params.toArray();
    }
}
