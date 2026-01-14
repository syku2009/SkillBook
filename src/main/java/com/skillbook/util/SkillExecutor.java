package com.skillbook.util;

import com.skillbook.SkillBookPlugin;
import com.skillbook.data.PlayerSkillData;
import com.skillbook.manager.CooldownManager;
import com.skillbook.manager.SkillManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.entity.Player;

/**
 * 스킬 실행 유틸리티
 */
public class SkillExecutor {

    /**
     * 스킬 실행
     * @return 실행 성공 여부
     */
    public static boolean executeSkill(Player player, String skillId) {
        SkillBookPlugin plugin = SkillBookPlugin.getInstance();
        SkillManager skillManager = plugin.getSkillManager();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        PlayerSkillData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        
        // 스킬 존재 확인
        if (!skillManager.skillExists(skillId)) {
            player.sendMessage(plugin.getConfig().getString("messages.skill-not-found", "§c존재하지 않는 스킬입니다."));
            return false;
        }
        
        // 스킬 습득 여부 확인
        if (!playerData.hasSkill(skillId)) {
            player.sendMessage("§c습득하지 않은 스킬입니다.");
            return false;
        }
        
        // 쿨타임 확인
        if (cooldownManager.isOnCooldown(player.getUniqueId(), skillId)) {
            double remaining = cooldownManager.getRemainingCooldown(player.getUniqueId(), skillId);
            String message = plugin.getConfig().getString("messages.cooldown-remaining", "§c아직 %time%초 남았습니다.");
            message = message.replace("%time%", String.format("%.1f", remaining));
            player.sendMessage(message);
            return false;
        }
        
        // MythicMobs 스킬 실행
        try {
            MythicBukkit.inst().getAPIHelper().castSkill(player, skillId);
            
            // 쿨타임 설정
            SkillManager.SkillInfo skillInfo = skillManager.getSkillInfo(skillId);
            if (skillInfo != null && skillInfo.getCooldown() > 0) {
                cooldownManager.setCooldown(player.getUniqueId(), skillId, skillInfo.getCooldown());
            }
            
            return true;
        } catch (Exception e) {
            player.sendMessage("§c스킬 실행 중 오류가 발생했습니다.");
            plugin.getLogger().warning("스킬 실행 오류: " + skillId + " - " + e.getMessage());
            return false;
        }
    }
}
