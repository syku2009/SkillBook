package com.skillbook.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 스킬 쿨타임 관리 클래스
 */
public class CooldownManager {

    // 플레이어UUID -> (스킬ID -> 쿨타임 종료 시간)
    private final Map<UUID, Map<String, Long>> cooldowns;

    public CooldownManager() {
        this.cooldowns = new HashMap<>();
    }

    /**
     * 스킬 쿨타임 설정
     */
    public void setCooldown(UUID uuid, String skillId, double seconds) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>());
        long endTime = System.currentTimeMillis() + (long) (seconds * 1000);
        cooldowns.get(uuid).put(skillId.toLowerCase(), endTime);
    }

    /**
     * 쿨타임 중인지 확인
     */
    public boolean isOnCooldown(UUID uuid, String skillId) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) return false;
        
        Long endTime = playerCooldowns.get(skillId.toLowerCase());
        if (endTime == null) return false;
        
        return System.currentTimeMillis() < endTime;
    }

    /**
     * 남은 쿨타임 (초)
     */
    public double getRemainingCooldown(UUID uuid, String skillId) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) return 0;
        
        Long endTime = playerCooldowns.get(skillId.toLowerCase());
        if (endTime == null) return 0;
        
        long remaining = endTime - System.currentTimeMillis();
        return remaining > 0 ? remaining / 1000.0 : 0;
    }

    /**
     * 쿨타임 초기화
     */
    public void clearCooldown(UUID uuid, String skillId) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns != null) {
            playerCooldowns.remove(skillId.toLowerCase());
        }
    }

    /**
     * 플레이어의 모든 쿨타임 초기화
     */
    public void clearAllCooldowns(UUID uuid) {
        cooldowns.remove(uuid);
    }
}
