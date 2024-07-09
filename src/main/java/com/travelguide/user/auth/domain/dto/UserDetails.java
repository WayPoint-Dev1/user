package com.travelguide.user.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UserDetails {
  private String firstName;
  private String middleName;
  private String lastName;
  private String userId;
  private String email;
  private String mobileNo;
  private String password;
  private String userIdStatus;
  @JsonIgnore private String secret;
  private String otp;
  private boolean mfa;
}
