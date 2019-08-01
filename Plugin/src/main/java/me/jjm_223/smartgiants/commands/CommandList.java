package me.jjm_223.smartgiants.commands;

import me.jjm_223.smartgiants.LangManager;
import me.jjm_223.smartgiants.SmartGiants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandList extends CommandBase {
    private SmartGiants plugin;

    public CommandList(final SmartGiants plugin) {
        super("List", "smartgiants.configure", true, 0);

        this.plugin = plugin;
    }

    @Override
    public boolean execute(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        plugin.getDropManager()
                .getDrops()
                .forEach(d -> sender.sendMessage(LangManager.getLang("dropDisplay", d.getItem())));

        return true;
    }
}
