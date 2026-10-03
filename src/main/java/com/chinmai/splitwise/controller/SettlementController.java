
package com.chinmai.splitwise.controller;

import com.chinmai.splitwise.dto.SettlementRequest;
import com.chinmai.splitwise.service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping
    public ResponseEntity<String> createSettlement(
            @Valid @RequestBody SettlementRequest request) {

        settlementService.createSettlement(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Settlement recorded successfully");
    }
}
