package com.skillbook;

import com.skillbook.commands.SkillBookCommand;
import com.skillbook.commands.SkillCommand;
import com.skillbook.commands.SkillListCommand;
import com.skillbook.listeners.GUIListener;
import com.skillbook.listeners.KeyInputListener;
import com.skillbook.listeners.SkillBookListener;
import com.skillbook.manager.CooldownManager;
import com.skillbook.manager.PlayerDataManager;
import com.skillbook.manager.SkillManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillBookPlugin extends JavaPlugin {

    private static SkillBookPlugin instance;
    private SkillManager skillManager;
    private PlayerDataManager playerDataManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // config.yml 저장
        saveDefaultConfig();
        
        // 매니저 초기화
        this.skillManager = new SkillManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.cooldownManager = new CooldownManager();
        
        // 스킬 로드
        skillManager.loadSkills();
        
        // 명령어 등록
        getCommand("스킬북").setExecutor(new SkillBookCommand(this));
        getCommand("스킬리스트").setExecutor(new SkillListCommand(this));
        getCommand("스킬").setExecutor(new SkillCommand(this));
        
        // 이벤트 리스너 등록
        getServer().getPluginManager().registerEvents(new SkillBookListener(this), this);
        getServer().getPluginManager().registerEvents(new KeyInputListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        
        getLogger().info("§aSkillBookPlugin이 활성화되었습니다!");
    }

    @Override
    public void onDisable() {
        // 플레이어 데이터 저장
        if (playerDataManager != null) {
            playerDataManager.saveAllData();
        }
        
        // CMD 맵 저장
        if (skillManager != null) {
            skillManager.saveCmdMap();
        }
        
        getLogger().info("§cSkillBookPlugin이 비활성화되었습니다!");
    }

    public static SkillBookPlugin getInstance() {
        return instance;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
