package com.kay.pfie.classify;

import com.kay.pfie.category.Category;
import com.kay.pfie.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "classification_cache",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_classification_user_sig", columnNames = {"user_id", "signature"})
        }
)
public class ClassificationCache {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "signature", nullable = false)
    private String signature;

    @Column(name = "merchant_normalized")
    private String merchantNormalized;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_code", referencedColumnName = "code")
    private Category category;

    @Column(name = "confidence", nullable = false, precision = 4, scale = 3)
    private BigDecimal confidence = new BigDecimal("0.000");

    @Column(name = "model")
    private String model;

    @Column(name = "prompt_version")
    private String promptVersion;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (confidence == null) confidence = new BigDecimal("0.000");
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getMerchantNormalized() {
        return merchantNormalized;
    }

    public void setMerchantNormalized(String merchantNormalized) {
        this.merchantNormalized = merchantNormalized;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
