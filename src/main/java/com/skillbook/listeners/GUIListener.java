package com.skillbook.listeners;

import com.skillbook.SkillBookPlugin;
import com.skillbook.data.KeyType;
import com.skillbook.data.PlayerSkillData;
import com.skillbook.gui.SkillListGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * GUI 클릭 이벤트 처리
 */
public class GUIListener implements Listener {

    private final SkillBookPlugin plugin;
    private final Map<UUID, String> selectedSkill;

    public GUIListener(SkillBookPlugin plugin) {
        this.plugin = plugin;
        this.selectedSkill = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getView().getTitle().equals(SkillListGUI.GUI_TITLE)) {
            event.setCancelled(true);
            handleSkillListClick(player, event);
        }
    }

    private void handleSkillListClick(Player player, InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= 54) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        PlayerSkillData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        if (slot < 27) {
            handleSkillClick(player, clicked);
            return;
        }

        if (SkillListGUI.isKeySlot(slot)) {
            KeyType keyType = SkillListGUI.getKeyTypeBySlot(slot);
            if (keyType == null) return;
            
            if (event.getClick() == ClickType.RIGHT) {
                playerData.unbindKey(keyType);
                plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
                player.sendMessage("§e" + keyType.getDisplayName() + " §7바인딩 해제");
                SkillListGUI.openMainGUI(player);
            } else if (event.getClick() == ClickType.LEFT) {
                String selected = selectedSkill.get(player.getUniqueId());
                if (selected != null && playerData.hasSkill(selected)) {
                    playerData.bindSkill(keyType, selected);
                    plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
                    player.sendMessage("§a" + selected + " §7→ §e" + keyType.getDisplayName());
                    selectedSkill.remove(player.getUniqueId());
                    SkillListGUI.openMainGUI(player);
                } else {
                    player.sendMessage("§c먼저 스킬을 선택하세요.");
                }
            }
        }
    }

    private void handleSkillClick(Player player, ItemStack item) {
        if (item.getType() != Material.ENCHANTED_BOOK) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return;

        for (String line : meta.getLore()) {
            if (line.contains("스킬 ID:")) {
                String skillId = line.replace("§7스킬 ID: ", "").trim();
                selectedSkill.put(player.getUniqueId(), skillId);
                player.sendMessage("§a" + skillId + " §7선택됨. 키 슬롯 클릭");
                return;
            }
        }
    }
}
