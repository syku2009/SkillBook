package com.skillbook.manager;

import com.skillbook.SkillBookPlugin;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * MythicMobs 스킬 관리 클래스
 */
public class SkillManager {

    private final SkillBookPlugin plugin;
    private final Map<String, Integer> skillCmdMap; // 스킬ID -> CustomModelData
    private final Map<String, SkillInfo> skillInfoMap; // 스킬ID -> 스킬 정보
    private File cmdMapFile;
    private FileConfiguration cmdMapConfig;
    private int nextCmd;

    public SkillManager(SkillBookPlugin plugin) {
        this.plugin = plugin;
        this.skillCmdMap = new HashMap<>();
        this.skillInfoMap = new HashMap<>();
        this.nextCmd = plugin.getConfig().getInt("cmd-start", 30000);
        
        loadCmdMap();
    }

    /**
     * skill_cmd_map.yml 로드
     */
    private void loadCmdMap() {
        cmdMapFile = new File(plugin.getDataFolder(), "skill_cmd_map.yml");
        
        if (!cmdMapFile.exists()) {
            try {
                cmdMapFile.getParentFile().mkdirs();
                cmdMapFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "skill_cmd_map.yml 생성 실패", e);
            }
        }
        
        cmdMapConfig = YamlConfiguration.loadConfiguration(cmdMapFile);
        
        // 기존 CMD 맵 로드
        for (String key : cmdMapConfig.getKeys(false)) {
            int cmd = cmdMapConfig.getInt(key);
            skillCmdMap.put(key.toLowerCase(), cmd);
            if (cmd >= nextCmd) {
                nextCmd = cmd + 1;
            }
        }
    }

    /**
     * skill_cmd_map.yml 저장
     */
    public void saveCmdMap() {
        if (cmdMapConfig == null || cmdMapFile == null) return;
        
        for (Map.Entry<String, Integer> entry : skillCmdMap.entrySet()) {
            cmdMapConfig.set(entry.getKey(), entry.getValue());
        }
        
        try {
            cmdMapConfig.save(cmdMapFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "skill_cmd_map.yml 저장 실패", e);
        }
    }

    /**
     * MythicMobs에서 스킬 목록 로드
     */
    public void loadSkills() {
        skillInfoMap.clear();
        
        try {
            MythicBukkit mythic = MythicBukkit.inst();
            if (mythic == null) {
                plugin.getLogger().warning("MythicMobs가 로드되지 않았습니다.");
                return;
            }
            
            // MythicMobs의 모든 스킬 가져오기
            Collection<String> skillNames = mythic.getSkillManager().getSkillNames();
            
            for (String skillName : skillNames) {
                String skillId = skillName.toLowerCase();
                
                // CMD 할당 (없으면 새로 생성)
                if (!skillCmdMap.containsKey(skillId)) {
                    skillCmdMap.put(skillId, nextCmd++);
                }
                
                // 스킬 정보 생성
                SkillInfo info = new SkillInfo(skillId, skillName, skillCmdMap.get(skillId));
                skillInfoMap.put(skillId, info);
            }
            
            // CMD 맵 저장
            saveCmdMap();
            
            plugin.getLogger().info("MythicMobs 스킬 " + skillInfoMap.size() + "개를 로드했습니다.");
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "MythicMobs 스킬 로드 실패", e);
        }
    }

    /**
     * 스킬 존재 여부 확인
     */
    public boolean skillExists(String skillId) {
        return skillInfoMap.containsKey(skillId.toLowerCase());
    }

    /**
     * 스킬 정보 가져오기
     */
    public SkillInfo getSkillInfo(String skillId) {
        return skillInfoMap.get(skillId.toLowerCase());
    }

    /**
     * 모든 스킬 ID 목록
     */
    public Set<String> getAllSkillIds() {
        return skillInfoMap.keySet();
    }

    /**
     * 스킬 ID로 검색 (부분 일치)
     */
    public List<String> searchSkills(String query) {
        List<String> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (String skillId : skillInfoMap.keySet()) {
            if (skillId.contains(lowerQuery)) {
                results.add(skillId);
            }
        }
        
        return results;
    }

    /**
     * CustomModelData로 스킬 ID 찾기
     */
    public String getSkillIdByCmd(int cmd) {
        for (Map.Entry<String, Integer> entry : skillCmdMap.entrySet()) {
            if (entry.getValue() == cmd) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 스킬 정보 클래스
     */
    public static class SkillInfo {
        private final String skillId;
        private final String displayName;
        private final int customModelData;
        private double cooldown;

        public SkillInfo(String skillId, String displayName, int customModelData) {
            this.skillId = skillId;
            this.displayName = displayName;
            this.customModelData = customModelData;
            this.cooldown = 0;
        }

        public String getSkillId() {
            return skillId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getCustomModelData() {
            return customModelData;
        }

        public double getCooldown() {
            return cooldown;
        }

        public void setCooldown(double cooldown) {
            this.cooldown = cooldown;
        }
    }
}
