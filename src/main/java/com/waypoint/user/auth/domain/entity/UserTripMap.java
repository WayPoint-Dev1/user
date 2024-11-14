package com.waypoint.user.auth.domain.entity;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_trip_map")
@Getter
@Setter
@Builder
@Entity
public class UserTripMap {
  @Id private UUID id;
  private Integer userTripMapId;
  private UUID userId;
  private UUID tripId;
  private Boolean isActive;
  private String createdBy;
  private LocalDateTime createdOn;
  private String updatedBy;
  private LocalDateTime updatedOn;
}
