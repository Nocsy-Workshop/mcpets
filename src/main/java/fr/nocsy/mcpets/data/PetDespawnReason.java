package fr.nocsy.mcpets.data;

import lombok.Getter;

public enum PetDespawnReason {

    TELEPORT("teleport"),
    DEATH("death"),
    REVOKE("revoke"),
    REPLACED("replaced"),
    OWNER_NOT_HERE("owner not here"),
    RELOAD("reload"),
    FLAG("flag"),
    GAMEMODE("gamemode"),
    MYTHICMOBS("mythicmobs"),
    SPAWN_ISSUE("spawn issue"),
    LOOP_SPAWN("loop spawn"),
    DISCONNECTION("owner disconnected"),
    UNKNOWN("unkown");


    @Getter
    private final String reason;

    PetDespawnReason(String reason) {
        this.reason = reason;
    }

    public boolean equals(PetDespawnReason reason) {
        return this.getReason().equals(reason.getReason());
    }


}
