package fr.nocsy.mcpets.data;

import lombok.Getter;

public enum PetDespawnReason {

    TELEPORT("teleport"),
    DEATH("death"),
    REVOKE("revoke"),
    REPLACED("replaced"),
    SETPET_REPLACED("setpet replaced"),
    OWNER_NOT_HERE("owner not here"),
    RELOAD("reload"),
    FLAG("flag"),
    GAMEMODE("gamemode"),
    MYTHICMOBS("mythicmobs"),
    SPAWN_ISSUE("spawn issue"),
    LOOP_SPAWN("loop spawn"),
    DISCONNECTION("owner disconnected"),
    DISMOUNT("pet despawn on dismount"),
    SKIN("changing skin"),
    RESPAWN_TIMER("respawn timer"),
    REVOKE_TIMER("revoke timer"),
    EVOLUTION("evolution"),
    CANCELLED("cancelled"),
    CHANGING_TO_NULL_ACTIVEMOB("changing to null active mob"),
    DONT_HAVE_PERM("don't have permission"),
    NO_OWNER("No owner found"),
    ACTIVE_MOB_LINKAGE_FAILED("link to the activeMob failed"),
    PETDESPAWN_SKILL("petdespawn skill"),
    AI_TRACK_DESPAWN("AI track could not find the entity"),
    UNKNOWN("unknown");


    @Getter
    private final String reason;

    PetDespawnReason(String reason) {
        this.reason = reason;
    }

    public boolean equals(PetDespawnReason reason) {
        return this.getReason().equals(reason.getReason());
    }


}
