package com.skillbook.listeners;

import com.skillbook.SkillBookPlugin;
import com.skillbook.data.PlayerSkillData;
import com.skillbook.manager.PlayerDataManager;
import com.skillbook.util.SkillBookItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * 스킬북 관련 이벤트 리스너
 */
public class SkillBookListener implements Listener {

    private final SkillBookPlugin plugin;

    public SkillBookListener(SkillBookPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 플레이어 접속 시 데이터 로드
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getPlayerDataManager().loadPlayerData(event.getPlayer().getUniqueId());
    }

    /**
     * 플레이어 퇴장 시 데이터 저장 및 언로드
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().unloadPlayerData(event.getPlayer().getUniqueId());
    }

    /**
     * 스킬북 우클릭으로 스킬 습득
     */
    @EventHandler
    public void onSkillBookUse(PlayerInteractEvent event) {
        // 우클릭만 처리
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // 메인 핸드만 처리
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // 스킬북인지 확인
        if (!SkillBookItem.isSkillBook(item)) {
            return;
        }

        event.setCancelled(true);

        String skillId = SkillBookItem.getSkillId(item);
        if (skillId == null) {
            return;
        }

        PlayerDataManager dataManager = plugin.getPlayerDataManager();
        PlayerSkillData playerData = dataManager.getPlayerData(player.getUniqueId());

        // 이미 습득한 스킬인지 확인
        if (playerData.hasSkill(skillId)) {
            player.sendMessage(plugin.getConfig().getString("messages.skill-already-learned", "§c이미 습득한 스킬입니다."));
            return;
        }

        // 스킬 습득
        playerData.addSkill(skillId);
        dataManager.savePlayerData(player.getUniqueId());

        // 스킬북 1개 차감
        item.setAmount(item.getAmount() - 1);

        // 메시지 전송
        String message = plugin.getConfig().getString("messages.skill-learned", "§a스킬을 습득했습니다: %skill%");
        message = message.replace("%skill%", skillId);
        player.sendMessage(message);
    }
}
