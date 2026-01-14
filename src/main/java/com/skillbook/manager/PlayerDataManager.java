package com.skillbook.manager;

import com.skillbook.SkillBookPlugin;
import com.skillbook.data.KeyType;
import com.skillbook.data.PlayerSkillData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * 플레이어 데이터 관리 클래스
 */
public class PlayerDataManager {

    private final SkillBookPlugin plugin;
    private final Map<UUID, PlayerSkillData> playerDataMap;
    private final File dataFolder;

    public PlayerDataManager(SkillBookPlugin plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    /**
     * 플레이어 데이터 가져오기 (없으면 생성)
     */
    public PlayerSkillData getPlayerData(UUID uuid) {
        if (!playerDataMap.containsKey(uuid)) {
            loadPlayerData(uuid);
        }
        return playerDataMap.get(uuid);
    }

    /**
     * 플레이어 데이터 로드
     */
    public void loadPlayerData(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        PlayerSkillData data = new PlayerSkillData(uuid);
        
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            
            // 습득한 스킬 로드
            List<String> skills = config.getStringList("skills");
            for (String skill : skills) {
                data.addSkill(skill);
            }
            
            // 키 바인딩 로드
            if (config.contains("bindings")) {
                for (String keyName : config.getConfigurationSection("bindings").getKeys(false)) {
                    try {
                        KeyType keyType = KeyType.valueOf(keyName);
                        String skillId = config.getString("bindings." + keyName);
                        if (skillId != null && !skillId.isEmpty()) {
                            data.bindSkill(keyType, skillId);
                        }
                    } catch (IllegalArgumentException ignored) {
                        // 잘못된 키 타입 무시
                    }
                }
            }
        }
        
        playerDataMap.put(uuid, data);
    }

    /**
     * 플레이어 데이터 저장
     */
    public void savePlayerData(UUID uuid) {
        PlayerSkillData data = playerDataMap.get(uuid);
        if (data == null) return;
        
        File file = new File(dataFolder, uuid.toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        
        // 습득한 스킬 저장
        config.set("skills", data.getLearnedSkills().stream().toList());
        
        // 키 바인딩 저장
        for (Map.Entry<KeyType, String> entry : data.getKeyBindings().entrySet()) {
            config.set("bindings." + entry.getKey().name(), entry.getValue());
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "플레이어 데이터 저장 실패: " + uuid, e);
        }
    }

    /**
     * 모든 플레이어 데이터 저장
     */
    public void saveAllData() {
        for (UUID uuid : playerDataMap.keySet()) {
            savePlayerData(uuid);
        }
    }

    /**
     * 플레이어 데이터 언로드
     */
    public void unloadPlayerData(UUID uuid) {
        savePlayerData(uuid);
        playerDataMap.remove(uuid);
    }
}
