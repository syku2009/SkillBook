package com.skillbook.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 플레이어별 스킬 데이터 저장 클래스
 */
public class PlayerSkillData {
    
    private final UUID playerUuid;
    private final Set<String> learnedSkills;
    private final Map<KeyType, String> keyBindings;

    public PlayerSkillData(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.learnedSkills = new HashSet<>();
        this.keyBindings = new HashMap<>();
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Set<String> getLearnedSkills() {
        return learnedSkills;
    }

    public boolean hasSkill(String skillId) {
        return learnedSkills.contains(skillId.toLowerCase());
    }

    public void addSkill(String skillId) {
        learnedSkills.add(skillId.toLowerCase());
    }

    public void removeSkill(String skillId) {
        learnedSkills.remove(skillId.toLowerCase());
    }

    public Map<KeyType, String> getKeyBindings() {
        return keyBindings;
    }

    public String getSkillForKey(KeyType keyType) {
        return keyBindings.get(keyType);
    }

    public void bindSkill(KeyType keyType, String skillId) {
        keyBindings.put(keyType, skillId);
    }

    public void unbindKey(KeyType keyType) {
        keyBindings.remove(keyType);
    }

    public KeyType getKeyForSkill(String skillId) {
        for (Map.Entry<KeyType, String> entry : keyBindings.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(skillId)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
