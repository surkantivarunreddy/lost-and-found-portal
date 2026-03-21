package com.lostfound.dto;

import com.lostfound.entity.Item.ItemStatus;
import com.lostfound.entity.User.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminDto {

    // ── Stats ────────────────────────────────────────────
    public static class StatsResponse {
        private long totalUsers;
        private long totalItems;
        private long totalMessages;
        private long lostItems;
        private long foundItems;
        private long activeItems;
        private long resolvedItems;

        public StatsResponse() {}

        public StatsResponse(long totalUsers, long totalItems, long totalMessages,
                             long lostItems, long foundItems,
                             long activeItems, long resolvedItems) {
            this.totalUsers    = totalUsers;
            this.totalItems    = totalItems;
            this.totalMessages = totalMessages;
            this.lostItems     = lostItems;
            this.foundItems    = foundItems;
            this.activeItems   = activeItems;
            this.resolvedItems = resolvedItems;
        }

        public long getTotalUsers()    { return totalUsers; }
        public long getTotalItems()    { return totalItems; }
        public long getTotalMessages() { return totalMessages; }
        public long getLostItems()     { return lostItems; }
        public long getFoundItems()    { return foundItems; }
        public long getActiveItems()   { return activeItems; }
        public long getResolvedItems() { return resolvedItems; }
    }

    // ── User response ────────────────────────────────────
    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String role;
        private LocalDateTime createdAt;
        private long itemCount;

        public UserResponse() {}

        public Long getId()              { return id; }
        public String getName()          { return name; }
        public String getEmail()         { return email; }
        public String getPhone()         { return phone; }
        public String getRole()          { return role; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public long getItemCount()       { return itemCount; }

        public void setId(Long id)                    { this.id = id; }
        public void setName(String name)              { this.name = name; }
        public void setEmail(String email)            { this.email = email; }
        public void setPhone(String phone)            { this.phone = phone; }
        public void setRole(String role)              { this.role = role; }
        public void setCreatedAt(LocalDateTime c)     { this.createdAt = c; }
        public void setItemCount(long itemCount)      { this.itemCount = itemCount; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private final UserResponse r = new UserResponse();
            public Builder id(Long v)              { r.id = v; return this; }
            public Builder name(String v)          { r.name = v; return this; }
            public Builder email(String v)         { r.email = v; return this; }
            public Builder phone(String v)         { r.phone = v; return this; }
            public Builder role(String v)          { r.role = v; return this; }
            public Builder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
            public Builder itemCount(long v)       { r.itemCount = v; return this; }
            public UserResponse build()            { return r; }
        }
    }

    // ── Item response ────────────────────────────────────
    public static class ItemResponse {
        private Long id;
        private String title;
        private String type;
        private String status;
        private String category;
        private String location;
        private String reportedByName;
        private String reportedByEmail;
        private LocalDate dateLostFound;
        private LocalDateTime createdAt;

        public ItemResponse() {}

        public Long getId()                   { return id; }
        public String getTitle()              { return title; }
        public String getType()               { return type; }
        public String getStatus()             { return status; }
        public String getCategory()           { return category; }
        public String getLocation()           { return location; }
        public String getReportedByName()     { return reportedByName; }
        public String getReportedByEmail()    { return reportedByEmail; }
        public LocalDate getDateLostFound()   { return dateLostFound; }
        public LocalDateTime getCreatedAt()   { return createdAt; }

        public void setId(Long id)                    { this.id = id; }
        public void setTitle(String title)            { this.title = title; }
        public void setType(String type)              { this.type = type; }
        public void setStatus(String status)          { this.status = status; }
        public void setCategory(String category)      { this.category = category; }
        public void setLocation(String location)      { this.location = location; }
        public void setReportedByName(String v)       { this.reportedByName = v; }
        public void setReportedByEmail(String v)      { this.reportedByEmail = v; }
        public void setDateLostFound(LocalDate v)     { this.dateLostFound = v; }
        public void setCreatedAt(LocalDateTime v)     { this.createdAt = v; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private final ItemResponse r = new ItemResponse();
            public Builder id(Long v)               { r.id = v; return this; }
            public Builder title(String v)          { r.title = v; return this; }
            public Builder type(String v)           { r.type = v; return this; }
            public Builder status(String v)         { r.status = v; return this; }
            public Builder category(String v)       { r.category = v; return this; }
            public Builder location(String v)       { r.location = v; return this; }
            public Builder reportedByName(String v) { r.reportedByName = v; return this; }
            public Builder reportedByEmail(String v){ r.reportedByEmail = v; return this; }
            public Builder dateLostFound(LocalDate v){ r.dateLostFound = v; return this; }
            public Builder createdAt(LocalDateTime v){ r.createdAt = v; return this; }
            public ItemResponse build()             { return r; }
        }
    }

    // ── Request bodies ───────────────────────────────────
    public static class RoleUpdateRequest {
        private Role role;
        public Role getRole()       { return role; }
        public void setRole(Role r) { this.role = r; }
    }

    public static class StatusUpdateRequest {
        private ItemStatus status;
        public ItemStatus getStatus()            { return status; }
        public void setStatus(ItemStatus status) { this.status = status; }
    }
}

