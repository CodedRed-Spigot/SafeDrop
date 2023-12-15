package dev.codedred.safedrop.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class User {

  private UUID uniqueId;
  private boolean enabled;
}
