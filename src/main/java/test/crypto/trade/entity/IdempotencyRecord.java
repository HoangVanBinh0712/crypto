package test.crypto.trade.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_record")
@Data
public class IdempotencyRecord {
    @Id
    @Column(name = "idempotency_key")
    private String key;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Column(name = "responseBody", nullable = false, columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createdAt;

}