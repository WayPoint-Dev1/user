package com.waypoint.user.auth.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_info")
@Getter
@Setter
@Builder
@Entity
public class User {
  @Id
  @org.springframework.data.annotation.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;
  private Integer userId;
  private String userName;
  private String email;
  private String firstName;
  private String middleName;
  private String lastName;
  private String countryCode;
  private String mobileNo;
  private String hashedPassword;
  private String secret;
  private boolean emailVerified;
  private boolean mfaEnabled;
  private boolean isActive;
  private String createdBy;
  private LocalDateTime createdOn;
  private String updatedBy;
  private LocalDateTime updatedOn;
}
