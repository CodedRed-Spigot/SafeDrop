package dev.codedred.safedrop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class User {

    private UUID uniqueId;
    private boolean enabled;

}
