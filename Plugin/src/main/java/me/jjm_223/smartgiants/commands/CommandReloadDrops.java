package me.jjm_223.smartgiants.commands;

import me.jjm_223.smartgiants.SmartGiants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static me.jjm_223.smartgiants.LangManager.getLang;

public class CommandReloadDrops extends CommandBase {
    private SmartGiants plugin;

    public CommandReloadDrops(final SmartGiants plugin) {
        super("ReloadDrops", "smartgiants.configure", false, 0);

        this.plugin = plugin;
    }

    @Override
    public boolean execute(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (plugin.reloadDrops()) {
            sender.sendMessage(getLang("dropsReloaded"));
        } else {
            sender.sendMessage(getLang("dropsFailedReload"));
        }

        return true;
    }
}
