package com.skillbook.util;

import com.skillbook.SkillBookPlugin;
import com.skillbook.manager.SkillManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

/**
 * 스킬북 아이템 생성 유틸리티
 */
public class SkillBookItem {

    private static final NamespacedKey SKILL_ID_KEY = new NamespacedKey(SkillBookPlugin.getInstance(), "skill_id");

    /**
     * 스킬북 아이템 생성
     */
    public static ItemStack createSkillBook(String skillId) {
        SkillManager skillManager = SkillBookPlugin.getInstance().getSkillManager();
        SkillManager.SkillInfo skillInfo = skillManager.getSkillInfo(skillId);
        
        if (skillInfo == null) {
            return null;
        }
        
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // 이름 설정
            meta.setDisplayName("§a[스킬북] " + skillInfo.getDisplayName());
            
            // 설명 설정
            List<String> lore = Arrays.asList(
                "§7우클릭하여 스킬을 습득합니다",
                "§e스킬ID: " + skillInfo.getSkillId()
            );
            meta.setLore(lore);
            
            // CustomModelData 설정
            meta.setCustomModelData(skillInfo.getCustomModelData());
            
            // PDC에 스킬 ID 저장
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(SKILL_ID_KEY, PersistentDataType.STRING, skillId.toLowerCase());
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    /**
     * 아이템이 스킬북인지 확인
     */
    public static boolean isSkillBook(ItemStack item) {
        if (item == null || item.getType() != Material.ENCHANTED_BOOK) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(SKILL_ID_KEY, PersistentDataType.STRING);
    }

    /**
     * 스킬북에서 스킬 ID 추출
     */
    public static String getSkillId(ItemStack item) {
        if (!isSkillBook(item)) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.get(SKILL_ID_KEY, PersistentDataType.STRING);
    }

    /**
     * NamespacedKey 반환
     */
    public static NamespacedKey getSkillIdKey() {
        return SKILL_ID_KEY;
    }
}
