package com.skillbook.commands;

import com.skillbook.SkillBookPlugin;
import com.skillbook.gui.SkillListGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /스킬리스트 명령어 처리
 */
public class SkillListCommand implements CommandExecutor {

    private final SkillBookPlugin plugin;

    public SkillListCommand(SkillBookPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c플레이어만 사용할 수 있는 명령어입니다.");
            return true;
        }

        if (!player.hasPermission("skillbook.use")) {
            player.sendMessage(plugin.getConfig().getString("messages.no-permission", "§c권한이 없습니다."));
            return true;
        }

        // GUI 열기
        SkillListGUI.openMainGUI(player);
        return true;
    }
}
