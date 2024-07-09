package com.travelguide.user.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_details")
@Getter
@Setter
@Builder
@Entity
public class User {
  @Id
  @org.springframework.data.annotation.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;

  private String userId;
  private String firstName;
  private String middleName;
  private String lastName;
  private String email;
  private String mobileNo;
  private String password;
  private String secret;
  private boolean mfa;
}
