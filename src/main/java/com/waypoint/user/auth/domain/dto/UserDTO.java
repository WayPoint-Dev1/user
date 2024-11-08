package com.waypoint.user.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

import java.util.UUID;

@Getter
@Setter
@With
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UserDTO {
  private UUID id;
  private Integer userId;
  private String userName;
  private String firstName;
  private String middleName;
  private String lastName;
  private String email;
  private String countryCode;
  private String mobileNo;
  private String password;
  @JsonIgnore private String secret;
  private boolean emailVerified;
  private boolean mfaEnabled;

  @Override
  public String toString() {
    return "UserDTO{"
        + "userName='"
        + userName
        + '\''
        + ", firstName='"
        + firstName
        + '\''
        + ", middleName='"
        + middleName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", email='"
        + email
        + '\''
        + ", countryCode='"
        + countryCode
        + '\''
        + ", mobileNo='"
        + mobileNo
        + '\''
        + ", emailVerified="
        + emailVerified
        + ", mfaEnabled="
        + mfaEnabled
        + '}';
  }
}
