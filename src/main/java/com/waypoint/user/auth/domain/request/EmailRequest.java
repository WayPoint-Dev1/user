package com.waypoint.user.auth.domain.request;

import lombok.*;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
  String recipient;
  String msgBody;
  String subject;
  String attachment;
}
