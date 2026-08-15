package com.lostfound.dto;

import com.lostfound.entity.Item.ItemStatus;
import com.lostfound.entity.Item.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ItemDto {

    // ── Create Request ────────────────────────────────────
    public static class CreateRequest {
        @NotBlank(message = "Title is required")
        private String title;
        private String description;
        @NotNull(message = "Item type (LOST/FOUND) is required")
        private ItemType type;
        private String category;
        private String location;
        private LocalDate dateLostFound;
        private String imageUrl;
        private String contactEmail;
        private String contactPhone;

        public CreateRequest() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public ItemType getType() { return type; }
        public void setType(ItemType type) { this.type = type; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public LocalDate getDateLostFound() { return dateLostFound; }
        public void setDateLostFound(LocalDate dateLostFound) { this.dateLostFound = dateLostFound; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getContactEmail() { return contactEmail; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    }

    // ── Update Request ────────────────────────────────────
    public static class UpdateRequest {
        private String title;
        private String description;
        private String category;
        private String location;
        private LocalDate dateLostFound;
        private String imageUrl;
        private String contactEmail;
        private String contactPhone;
        private ItemStatus status;

        public UpdateRequest() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public LocalDate getDateLostFound() { return dateLostFound; }
        public void setDateLostFound(LocalDate dateLostFound) { this.dateLostFound = dateLostFound; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getContactEmail() { return contactEmail; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
        public ItemStatus getStatus() { return status; }
        public void setStatus(ItemStatus status) { this.status = status; }
    }

    // ── Response ──────────────────────────────────────────
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private ItemType type;
        private ItemStatus status;
        private String category;
        private String location;
        private LocalDate dateLostFound;
        private String imageUrl;
        private String contactEmail;
        private String contactPhone;
        private Long reportedById;
        private String reportedByName;
        private LocalDateTime createdAt;

        public Response() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public ItemType getType() { return type; }
        public void setType(ItemType type) { this.type = type; }
        public ItemStatus getStatus() { return status; }
        public void setStatus(ItemStatus status) { this.status = status; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public LocalDate getDateLostFound() { return dateLostFound; }
        public void setDateLostFound(LocalDate dateLostFound) { this.dateLostFound = dateLostFound; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getContactEmail() { return contactEmail; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
        public Long getReportedById() { return reportedById; }
        public void setReportedById(Long reportedById) { this.reportedById = reportedById; }
        public String getReportedByName() { return reportedByName; }
        public void setReportedByName(String reportedByName) { this.reportedByName = reportedByName; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Long id;
            private String title;
            private String description;
            private ItemType type;
            private ItemStatus status;
            private String category;
            private String location;
            private LocalDate dateLostFound;
            private String imageUrl;
            private String contactEmail;
            private String contactPhone;
            private Long reportedById;
            private String reportedByName;
            private LocalDateTime createdAt;

            public Builder id(Long id) { this.id = id; return this; }
            public Builder title(String title) { this.title = title; return this; }
            public Builder description(String description) { this.description = description; return this; }
            public Builder type(ItemType type) { this.type = type; return this; }
            public Builder status(ItemStatus status) { this.status = status; return this; }
            public Builder category(String category) { this.category = category; return this; }
            public Builder location(String location) { this.location = location; return this; }
            public Builder dateLostFound(LocalDate d) { this.dateLostFound = d; return this; }
            public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
            public Builder contactEmail(String contactEmail) { this.contactEmail = contactEmail; return this; }
            public Builder contactPhone(String contactPhone) { this.contactPhone = contactPhone; return this; }
            public Builder reportedById(Long reportedById) { this.reportedById = reportedById; return this; }
            public Builder reportedByName(String reportedByName) { this.reportedByName = reportedByName; return this; }
            public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

            public Response build() {
                Response r = new Response();
                r.id = this.id; r.title = this.title; r.description = this.description;
                r.type = this.type; r.status = this.status; r.category = this.category;
                r.location = this.location; r.dateLostFound = this.dateLostFound;
                r.imageUrl = this.imageUrl;
                r.contactEmail = this.contactEmail; r.contactPhone = this.contactPhone;
                r.reportedById = this.reportedById; r.reportedByName = this.reportedByName;
                r.createdAt = this.createdAt;
                return r;
            }
        }
    }
}
