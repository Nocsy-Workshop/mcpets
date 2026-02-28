package fr.nocsy.mcpets.data;

import lombok.Getter;

public enum SpawnResult {

    BLOCKED(2),
    MOB_SPAWN(0),
    DESPAWNED_PREVIOUS(1),
    OWNER_NULL(-1),
    MYTHIC_MOB_NULL(-2),
    NO_MOB_MATCH(-3),
    NOT_ALLOWED(-4);

    @Getter
    private final int value;

    SpawnResult(int value) {
        this.value = value;
    }
}
