package com.lostfound.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MessageDto {

    // ── Send Request ──────────────────────────────────────────────────────────
    public static class SendRequest {

        // FIX: content is no longer @NotBlank — messages can be image/location only
        private String content;

        @NotNull(message = "Receiver ID is required")
        private Long receiverId;

        private Long itemId;

        // NEW fields
        private String imageUrl;
        private String locationText;

        public SendRequest() {}

        public String getContent()           { return content; }
        public void setContent(String c)     { this.content = c; }

        public Long getReceiverId()          { return receiverId; }
        public void setReceiverId(Long r)    { this.receiverId = r; }

        public Long getItemId()              { return itemId; }
        public void setItemId(Long i)        { this.itemId = i; }

        public String getImageUrl()          { return imageUrl; }
        public void setImageUrl(String u)    { this.imageUrl = u; }

        public String getLocationText()       { return locationText; }
        public void setLocationText(String l) { this.locationText = l; }
    }

    // ── Response ──────────────────────────────────────────────────────────────
    public static class Response {

        private Long id;
        private String content;
        private String imageUrl;      // NEW
        private String locationText;  // NEW
        private Long senderId;
        private String senderName;
        private Long receiverId;
        private String receiverName;
        private Long itemId;
        private String itemTitle;
        private boolean isRead;
        private LocalDateTime sentAt;

        public Response() {}

        public Long getId()                    { return id; }
        public void setId(Long id)             { this.id = id; }

        public String getContent()             { return content; }
        public void setContent(String c)       { this.content = c; }

        public String getImageUrl()            { return imageUrl; }
        public void setImageUrl(String u)      { this.imageUrl = u; }

        public String getLocationText()        { return locationText; }
        public void setLocationText(String l)  { this.locationText = l; }

        public Long getSenderId()              { return senderId; }
        public void setSenderId(Long s)        { this.senderId = s; }

        public String getSenderName()          { return senderName; }
        public void setSenderName(String s)    { this.senderName = s; }

        public Long getReceiverId()            { return receiverId; }
        public void setReceiverId(Long r)      { this.receiverId = r; }

        public String getReceiverName()        { return receiverName; }
        public void setReceiverName(String r)  { this.receiverName = r; }

        public Long getItemId()                { return itemId; }
        public void setItemId(Long i)          { this.itemId = i; }

        public String getItemTitle()           { return itemTitle; }
        public void setItemTitle(String t)     { this.itemTitle = t; }

        public boolean isRead()                { return isRead; }
        public void setRead(boolean r)         { this.isRead = r; }

        public LocalDateTime getSentAt()       { return sentAt; }
        public void setSentAt(LocalDateTime t) { this.sentAt = t; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Long id;
            private String content;
            private String imageUrl;
            private String locationText;
            private Long senderId;
            private String senderName;
            private Long receiverId;
            private String receiverName;
            private Long itemId;
            private String itemTitle;
            private boolean isRead;
            private LocalDateTime sentAt;

            public Builder id(Long v)              { this.id = v; return this; }
            public Builder content(String v)       { this.content = v; return this; }
            public Builder imageUrl(String v)      { this.imageUrl = v; return this; }
            public Builder locationText(String v)  { this.locationText = v; return this; }
            public Builder senderId(Long v)        { this.senderId = v; return this; }
            public Builder senderName(String v)    { this.senderName = v; return this; }
            public Builder receiverId(Long v)      { this.receiverId = v; return this; }
            public Builder receiverName(String v)  { this.receiverName = v; return this; }
            public Builder itemId(Long v)          { this.itemId = v; return this; }
            public Builder itemTitle(String v)     { this.itemTitle = v; return this; }
            public Builder isRead(boolean v)       { this.isRead = v; return this; }
            public Builder sentAt(LocalDateTime v) { this.sentAt = v; return this; }

            public Response build() {
                Response r = new Response();
                r.id           = this.id;
                r.content      = this.content;
                r.imageUrl     = this.imageUrl;
                r.locationText = this.locationText;
                r.senderId     = this.senderId;
                r.senderName   = this.senderName;
                r.receiverId   = this.receiverId;
                r.receiverName = this.receiverName;
                r.itemId       = this.itemId;
                r.itemTitle    = this.itemTitle;
                r.isRead       = this.isRead;
                r.sentAt       = this.sentAt;
                return r;
            }
        }
    }
}