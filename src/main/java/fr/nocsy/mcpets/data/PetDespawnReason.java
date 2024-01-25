package fr.nocsy.mcpets.data;

import lombok.Getter;

public enum PetDespawnReason {

    TELEPORT("传送"),
    DEATH("死亡"),
    REVOKE("撤销"),
    REPLACED("替换"),
    SETPET_REPLACED("设置宠物替换"),
    OWNER_NOT_HERE("主人不在"),
    RELOAD("重新加载"),
    FLAG("标志"),
    GAMEMODE("游戏模式"),
    MYTHICMOBS("神话生物"),
    SPAWN_ISSUE("生成问题"),
    LOOP_SPAWN("循环生成"),
    DISCONNECTION("主人断开连接"),
    DISMOUNT("解骑宠物消失"),
    SKIN("更改皮肤"),
    RESPAWN_TIMER("重生计时器"),
    REVOKE_TIMER("撤销计时器"),
    EVOLUTION("进化"),
    CANCELLED("取消"),
    CHANGING_TO_NULL_ACTIVEMOB("切换到空的活跃生物"),
    DONT_HAVE_PERM("没有权限"),
    NO_OWNER("未找到所有者"),
    ACTIVE_MOB_LINKAGE_FAILED("活跃生物链接失败"),
    PETDESPAWN_SKILL("宠物消失技能"),
    AI_TRACK_DESPAWN("AI跟踪无法找到实体"),
    UNKNOWN("未知");


    @Getter
    private final String reason;

    PetDespawnReason(String reason) {
        this.reason = reason;
    }

    public boolean equals(PetDespawnReason reason) {
        return this.getReason().equals(reason.getReason());
    }


}
