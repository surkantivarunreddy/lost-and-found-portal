package com.lostfound.entity;
 
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
 
@Entity
@Table(name = "items")
public class Item {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @NotBlank
    @Column(nullable = false)
    private String title;
 
    @Column(columnDefinition = "TEXT")
    private String description;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;
 
    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.ACTIVE;
 
    private String category;
    private String location;
    private LocalDate dateLostFound;
    private String imageUrl;
 
    // Split contact fields
    private String contactEmail;
    private String contactPhone;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;
 
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
 
    // ✅ NO CascadeType.ALL here — MySQL handles item_id via ON DELETE SET NULL.
    //    If we cascade here, JPA tries to delete Message rows which still have
    //    sender_id/receiver_id FK references, causing a constraint violation
    //    that silently rolls back the entire transaction.
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Message> messages;
 
    public enum ItemType { LOST, FOUND }
    public enum ItemStatus { ACTIVE, RESOLVED, CLOSED }
 
    // --- Constructors ---
    public Item() {}
 
    // --- Getters ---
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ItemType getType() { return type; }
    public ItemStatus getStatus() { return status; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public LocalDate getDateLostFound() { return dateLostFound; }
    public String getImageUrl() { return imageUrl; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public User getReportedBy() { return reportedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Message> getMessages() { return messages; }
 
    // --- Setters ---
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setType(ItemType type) { this.type = type; }
    public void setStatus(ItemStatus status) { this.status = status; }
    public void setCategory(String category) { this.category = category; }
    public void setLocation(String location) { this.location = location; }
    public void setDateLostFound(LocalDate dateLostFound) { this.dateLostFound = dateLostFound; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public void setReportedBy(User reportedBy) { this.reportedBy = reportedBy; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
 
    // --- Builder ---
    public static Builder builder() { return new Builder(); }
 
    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private ItemType type;
        private ItemStatus status = ItemStatus.ACTIVE;
        private String category;
        private String location;
        private LocalDate dateLostFound;
        private String imageUrl;
        private String contactEmail;
        private String contactPhone;
        private User reportedBy;
 
        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder type(ItemType type) { this.type = type; return this; }
        public Builder status(ItemStatus status) { this.status = status; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder dateLostFound(LocalDate dateLostFound) { this.dateLostFound = dateLostFound; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Builder contactEmail(String contactEmail) { this.contactEmail = contactEmail; return this; }
        public Builder contactPhone(String contactPhone) { this.contactPhone = contactPhone; return this; }
        public Builder reportedBy(User reportedBy) { this.reportedBy = reportedBy; return this; }
 
        public Item build() {
            Item i = new Item();
            i.id = this.id;
            i.title = this.title;
            i.description = this.description;
            i.type = this.type;
            i.status = this.status;
            i.category = this.category;
            i.location = this.location;
            i.dateLostFound = this.dateLostFound;
            i.imageUrl = this.imageUrl;
            i.contactEmail = this.contactEmail;
            i.contactPhone = this.contactPhone;
            i.reportedBy = this.reportedBy;
            return i;
        }
    }
}