package test.crypto.trade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.crypto.trade.entity.IdempotencyRecord;
import test.crypto.trade.repository.IdempotencyRecordRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IdempotencyService {

    @Autowired
    private IdempotencyRecordRepository recordRepo;

    public Optional<String> getSavedResponse(String key) {
        return recordRepo.findById(key).map(IdempotencyRecord::getResponseBody);
    }

    public void saveResponse(String key, String responseBody, String endpoint) {
        IdempotencyRecord record = new IdempotencyRecord();
        record.setKey(key);
        record.setResponseBody(responseBody);
        record.setEndpoint(endpoint);
        record.setCreatedAt(LocalDateTime.now());
        recordRepo.save(record);
    }
}