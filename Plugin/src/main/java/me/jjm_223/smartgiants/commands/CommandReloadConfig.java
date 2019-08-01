package me.jjm_223.smartgiants.commands;

import me.jjm_223.smartgiants.api.util.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

import static me.jjm_223.smartgiants.LangManager.getLang;

public class CommandReloadConfig extends CommandBase {
    public CommandReloadConfig() {
        super("ReloadConfig", "smartgiants.configure", false, 0);
    }

    @Override
    public boolean execute(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        try {
            Configuration.getInstance().reload();
            sender.sendMessage(getLang("configReloadSuccess"));
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().warning(e.getMessage());
            sender.sendMessage(getLang("configReloadFail"));
        }

        return true;
    }
}
