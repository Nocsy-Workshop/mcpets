package fr.nocsy.mcpets;

import lombok.Getter;

public enum PPermission {

    USE("mcpets.use"),
    ADMIN("mcpets.admin"),
    COLOR("mcpets.color");

    @Getter
    private final String permission;

    PPermission(String permission) {
        this.permission = permission;
    }

}
