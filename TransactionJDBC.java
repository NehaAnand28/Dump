package com.yourcompany.yourproject.integration.repository;

import com.yourcompany.yourproject.domain.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TransactionJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> searchTransactions(Transaction searchCriteria, List<String> statuses) {
        StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE 1=1");

        List<Object> params = new ArrayList<>();

        // Build the query based on non-null search criteria
        if (searchCriteria.getId() != null) {
            query.append(" AND id = ?");
            params.add(searchCriteria.getId());
        }
        if (searchCriteria.getReferenceNumber() != null) {
            query.append(" AND reference_number = ?");
            params.add(searchCriteria.getReferenceNumber());
        }
        if (searchCriteria.getCtcMessage() != null) {
            query.append(" AND ctc_message LIKE ?");
            params.add("%" + searchCriteria.getCtcMessage() + "%");
        }
        if (searchCriteria.getApplicationId() != null) {
            query.append(" AND application_id = ?");
            params.add(searchCriteria.getApplicationId());
        }

        // Handle multi-select status filtering
        if (statuses != null && !statuses.isEmpty()) {
            String placeholders = String.join(",", statuses.stream().map(s -> "?").toArray(String[]::new));
            query.append(" AND transaction_status IN (").append(placeholders).append(")");
            params.addAll(statuses);
        }

        // Execute the query
        return jdbcTemplate.queryForList(query.toString(), params.toArray());
    }
}
