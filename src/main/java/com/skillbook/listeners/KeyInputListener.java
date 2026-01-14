package com.skillbook.listeners;

import com.skillbook.SkillBookPlugin;
import com.skillbook.data.KeyType;
import com.skillbook.data.PlayerSkillData;
import com.skillbook.util.SkillExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 키 입력 감지 및 스킬 실행 리스너
 */
public class KeyInputListener implements Listener {

    private final SkillBookPlugin plugin;
    private final Map<UUID, Long> lastShiftTime;
    private final long doubleShiftInterval;

    public KeyInputListener(SkillBookPlugin plugin) {
        this.plugin = plugin;
        this.lastShiftTime = new HashMap<>();
        this.doubleShiftInterval = plugin.getConfig().getLong("double-shift-interval", 300);
    }

    /**
     * 클릭 이벤트 처리
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        Action action = event.getAction();
        
        KeyType keyType = null;

        // 우클릭
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                keyType = KeyType.SHIFT_RIGHT_CLICK;
            } else {
                keyType = KeyType.RIGHT_CLICK;
            }
        }
        // 좌클릭
        else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                keyType = KeyType.SHIFT_LEFT_CLICK;
            } else {
                keyType = KeyType.LEFT_CLICK;
            }
        }

        if (keyType != null) {
            executeSkillForKey(player, keyType);
        }
    }

    /**
     * 더블 쉬프트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        Long lastTime = lastShiftTime.get(uuid);
        lastShiftTime.put(uuid, currentTime);

        if (lastTime != null) {
            long diff = currentTime - lastTime;
            if (diff <= doubleShiftInterval) {
                // 더블 쉬프트 감지
                executeSkillForKey(player, KeyType.DOUBLE_SHIFT);
                lastShiftTime.remove(uuid); // 초기화
            }
        }
    }

    /**
     * 키에 바인딩된 스킬 실행
     */
    private void executeSkillForKey(Player player, KeyType keyType) {
        PlayerSkillData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        String skillId = playerData.getSkillForKey(keyType);

        if (skillId != null && !skillId.isEmpty()) {
            SkillExecutor.executeSkill(player, skillId);
        }
    }
}
