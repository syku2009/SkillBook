package com.skillbook.commands;

import com.skillbook.SkillBookPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * /스킬 명령어 처리
 */
public class SkillCommand implements CommandExecutor, TabCompleter {

    private final SkillBookPlugin plugin;

    public SkillCommand(SkillBookPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("스킬").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skillbook.admin")) {
            sender.sendMessage(plugin.getConfig().getString("messages.no-permission", "§c권한이 없습니다."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§e사용법: /스킬 리로드");
            return true;
        }

        if (args[0].equalsIgnoreCase("리로드") || args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getSkillManager().loadSkills();
            sender.sendMessage(plugin.getConfig().getString("messages.skill-reloaded", "§a스킬 정보가 리로드되었습니다."));
            return true;
        }

        sender.sendMessage("§c알 수 없는 명령어입니다. /스킬 리로드");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if ("리로드".startsWith(args[0].toLowerCase()) || args[0].isEmpty()) {
                completions.add("리로드");
            }
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
            return completions;
        }
        return new ArrayList<>();
    }
}
