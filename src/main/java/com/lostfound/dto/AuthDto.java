package com.lostfound.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDto {

    // ── Register Request ──────────────────────────────────────
    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        private String phone;

        public RegisterRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    // ── Login Request ─────────────────────────────────────────
    public static class LoginRequest {
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public LoginRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    // ── Auth Response ─────────────────────────────────────────
    public static class AuthResponse {
        private String token;
        private String type;
        private Long id;
        private String name;
        private String email;
        private String role;

        public AuthResponse() {}

        public AuthResponse(String token, String type, Long id,
                            String name, String email, String role) {
            this.token = token;
            this.type = type;
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String token;
            private String type = "Bearer";
            private Long id;
            private String name;
            private String email;
            private String role;

            public Builder token(String token) { this.token = token; return this; }
            public Builder type(String type) { this.type = type; return this; }
            public Builder id(Long id) { this.id = id; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder email(String email) { this.email = email; return this; }
            public Builder role(String role) { this.role = role; return this; }

            public AuthResponse build() {
                return new AuthResponse(token, type, id, name, email, role);
            }
        }
    }
}

