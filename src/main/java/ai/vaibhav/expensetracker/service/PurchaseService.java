package ai.vaibhav.expensetracker.service;

import ai.vaibhav.expensetracker.dto.PurchaseProjection;
import ai.vaibhav.expensetracker.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public List<PurchaseProjection> getLastPurchases(String itemName) {
        return purchaseRepository.findLastPurchasesByItem(itemName);
    }
}
