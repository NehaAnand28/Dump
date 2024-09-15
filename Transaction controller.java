package com.yourcompany.yourproject.api;

import com.yourcompany.yourproject.domain.Transaction;
import com.yourcompany.yourproject.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Endpoint to search transactions based on various criteria.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchTransactions(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "referenceNumber", required = false) String referenceNumber,
            @RequestParam(value = "ctcMessage", required = false) String ctcMessage,
            @RequestParam(value = "applicationIds", required = false) List<String> applicationIds,
            @RequestParam(value = "statuses", required = false) List<String> statuses
    ) {
        // Create search criteria object
        Transaction searchCriteria = Transaction.builder()
                .id(id)
                .referenceNumber(referenceNumber)
                .ctcMessage(ctcMessage)
                .applicationIdList(applicationIds)
                .statusList(statuses)
                .isApplicationIdListSelected(applicationIds != null && !applicationIds.isEmpty())
                .isStatusListSelected(statuses != null && !statuses.isEmpty())
                .build();

        // Call the service to perform the search
        List<Map<String, Object>> results = transactionService.findTransactions(searchCriteria);

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    /**
     * Endpoint to review a specific transaction by ID and reference number.
     */
    @GetMapping("/review")
    public ResponseEntity<Map<String, Object>> reviewTransaction(
            @RequestParam("id") Long id,
            @RequestParam("referenceNumber") String referenceNumber
    ) {
        // Find the transaction to review
        Map<String, Object> transaction = transactionService.findTransactionToReview(id, referenceNumber);
        if (transaction != null) {
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to approve a specific transaction by ID and reference number.
     */
    @PostMapping("/approve")
    public ResponseEntity<String> approveTransaction(
            @RequestParam("id") Long id,
            @RequestParam("referenceNumber") String referenceNumber
    ) {
        boolean isApproved = transactionService.approveTransaction(id, referenceNumber);
        if (isApproved) {
            return new ResponseEntity<>("Transaction approved successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Transaction not found or approval failed", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to create a new transaction.
     */
    @PostMapping("/create")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    /**
     * Endpoint to update an existing transaction.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable("id") Long id,
            @RequestBody Transaction transaction
    ) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction);
        if (updatedTransaction != null) {
            return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to delete a specific transaction by ID.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable("id") Long id) {
        boolean isDeleted = transactionService.deleteTransaction(id);
        if (isDeleted) {
            return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to bulk create transactions.
     */
    @PostMapping("/bulkCreate")
    public ResponseEntity<List<Transaction>> bulkCreateTransactions(@RequestBody List<Transaction> transactions) {
        List<Transaction> createdTransactions = transactionService.bulkCreateTransactions(transactions);
        return new ResponseEntity<>(createdTransactions, HttpStatus.CREATED);
    }

    /**
     * Endpoint to bulk delete transactions.
     */
    @DeleteMapping("/bulkDelete")
    public ResponseEntity<String> bulkDeleteTransactions(@RequestBody List<Long> ids) {
        transactionService.bulkDeleteTransactions(ids);
        return new ResponseEntity<>("Transactions deleted successfully", HttpStatus.OK);
    }
}
