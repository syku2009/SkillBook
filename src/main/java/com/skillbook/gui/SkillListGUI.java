package com.skillbook.gui;

import com.skillbook.SkillBookPlugin;
import com.skillbook.data.KeyType;
import com.skillbook.data.PlayerSkillData;
import com.skillbook.manager.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * 스킬 리스트 GUI
 */
public class SkillListGUI {

    public static final String GUI_TITLE = "§6§l스킬 목록";

    // 키 바인딩 슬롯
    public static final int SLOT_RIGHT_CLICK = 37;
    public static final int SLOT_LEFT_CLICK = 38;
    public static final int SLOT_SHIFT_RIGHT_CLICK = 39;
    public static final int SLOT_SHIFT_LEFT_CLICK = 40;
    public static final int SLOT_DOUBLE_SHIFT = 41;

    /**
     * 메인 GUI 열기
     */
    public static void openMainGUI(Player player) {
        SkillBookPlugin plugin = SkillBookPlugin.getInstance();
        PlayerSkillData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE);
        
        // 보유 스킬 표시 (상단 3줄)
        List<String> learnedSkills = new ArrayList<>(playerData.getLearnedSkills());
        int slot = 0;
        for (String skillId : learnedSkills) {
            if (slot >= 27) break;
            
            SkillManager.SkillInfo skillInfo = plugin.getSkillManager().getSkillInfo(skillId);
            if (skillInfo != null) {
                ItemStack item = createSkillItem(skillInfo, playerData);
                gui.setItem(slot, item);
                slot++;
            }
        }
        
        // 구분선
        ItemStack divider = createDivider();
        for (int i = 27; i < 36; i++) {
            gui.setItem(i, divider);
        }
        
        // 키 바인딩 슬롯
        gui.setItem(SLOT_RIGHT_CLICK, createKeySlot(KeyType.RIGHT_CLICK, playerData));
        gui.setItem(SLOT_LEFT_CLICK, createKeySlot(KeyType.LEFT_CLICK, playerData));
        gui.setItem(SLOT_SHIFT_RIGHT_CLICK, createKeySlot(KeyType.SHIFT_RIGHT_CLICK, playerData));
        gui.setItem(SLOT_SHIFT_LEFT_CLICK, createKeySlot(KeyType.SHIFT_LEFT_CLICK, playerData));
        gui.setItem(SLOT_DOUBLE_SHIFT, createKeySlot(KeyType.DOUBLE_SHIFT, playerData));
        
        // 빈 슬롯 채우기
        ItemStack filler = createFiller();
        for (int i = 36; i < 54; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }
        
        player.openInventory(gui);
    }

    /**
     * 스킬 아이템 생성
     */
    private static ItemStack createSkillItem(SkillManager.SkillInfo skillInfo, PlayerSkillData playerData) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§a" + skillInfo.getDisplayName());
            
            List<String> lore = new ArrayList<>();
            lore.add("§7스킬 ID: " + skillInfo.getSkillId());
            
            // 바인딩된 키 표시
            KeyType boundKey = playerData.getKeyForSkill(skillInfo.getSkillId());
            if (boundKey != null) {
                lore.add("§e바인딩: " + boundKey.getDisplayName());
            } else {
                lore.add("§8바인딩 없음");
            }
            
            lore.add("");
            lore.add("§b클릭하여 키 바인딩");
            
            meta.setLore(lore);
            meta.setCustomModelData(skillInfo.getCustomModelData());
            item.setItemMeta(meta);
        }
        
        return item;
    }

    /**
     * 키 슬롯 아이템 생성
     */
    private static ItemStack createKeySlot(KeyType keyType, PlayerSkillData playerData) {
        String boundSkill = playerData.getSkillForKey(keyType);
        
        ItemStack item;
        if (boundSkill != null && !boundSkill.isEmpty()) {
            item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        } else {
            item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e" + keyType.getDisplayName());
            
            List<String> lore = new ArrayList<>();
            if (boundSkill != null && !boundSkill.isEmpty()) {
                lore.add("§a등록된 스킬: " + boundSkill);
                lore.add("");
                lore.add("§c우클릭으로 해제");
            } else {
                lore.add("§7등록된 스킬 없음");
                lore.add("");
                lore.add("§b스킬 클릭 후 이 슬롯 클릭");
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }

    /**
     * 구분선 아이템
     */
    private static ItemStack createDivider() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * 빈 칸 아이템
     */
    private static ItemStack createFiller() {
        ItemStack item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * 키 타입으로 슬롯 번호 가져오기
     */
    public static KeyType getKeyTypeBySlot(int slot) {
        return switch (slot) {
            case SLOT_RIGHT_CLICK -> KeyType.RIGHT_CLICK;
            case SLOT_LEFT_CLICK -> KeyType.LEFT_CLICK;
            case SLOT_SHIFT_RIGHT_CLICK -> KeyType.SHIFT_RIGHT_CLICK;
            case SLOT_SHIFT_LEFT_CLICK -> KeyType.SHIFT_LEFT_CLICK;
            case SLOT_DOUBLE_SHIFT -> KeyType.DOUBLE_SHIFT;
            default -> null;
        };
    }

    /**
     * 키 슬롯인지 확인
     */
    public static boolean isKeySlot(int slot) {
        return slot == SLOT_RIGHT_CLICK || slot == SLOT_LEFT_CLICK ||
               slot == SLOT_SHIFT_RIGHT_CLICK || slot == SLOT_SHIFT_LEFT_CLICK ||
               slot == SLOT_DOUBLE_SHIFT;
    }
}
