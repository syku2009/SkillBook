package com.skillbook.commands;

import com.skillbook.SkillBookPlugin;
import com.skillbook.manager.SkillManager;
import com.skillbook.util.SkillBookItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * /스킬북 명령어 처리
 */
public class SkillBookCommand implements CommandExecutor, TabCompleter {

    private final SkillBookPlugin plugin;

    public SkillBookCommand(SkillBookPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("스킬북").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c플레이어만 사용할 수 있는 명령어입니다.");
            return true;
        }

        if (!player.hasPermission("skillbook.admin")) {
            player.sendMessage(plugin.getConfig().getString("messages.no-permission", "§c권한이 없습니다."));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§e사용법: /스킬북 <스킬ID>");
            return true;
        }

        String skillId = args[0].toLowerCase();
        SkillManager skillManager = plugin.getSkillManager();

        // 정확히 일치하는 스킬 확인
        if (skillManager.skillExists(skillId)) {
            ItemStack skillBook = SkillBookItem.createSkillBook(skillId);
            if (skillBook != null) {
                player.getInventory().addItem(skillBook);
                player.sendMessage("§a[스킬북] " + skillId + " 아이템을 받았습니다.");
            } else {
                player.sendMessage("§c스킬북 생성에 실패했습니다.");
            }
            return true;
        }

        // 비슷한 스킬 검색
        List<String> similar = skillManager.searchSkills(skillId);
        
        if (similar.isEmpty()) {
            player.sendMessage(plugin.getConfig().getString("messages.skill-not-found", "§c존재하지 않는 스킬입니다."));
        } else {
            player.sendMessage(plugin.getConfig().getString("messages.similar-skills", "§e비슷한 스킬 목록:"));
            for (String skill : similar) {
                player.sendMessage("§7- " + skill);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return plugin.getSkillManager().getAllSkillIds().stream()
                    .filter(id -> id.startsWith(input))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
