package me.jjm_223.smartgiants.commands;

import me.jjm_223.smartgiants.SmartGiants;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.jjm_223.smartgiants.LangManager.getLang;

public class CommandRemove extends CommandBase {
    private SmartGiants plugin;

    public CommandRemove(final SmartGiants plugin) {
        super("Remove", "smartgiants.configure", true, 0);

        this.plugin = plugin;
    }

    @Override
    public boolean execute(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        final Player player = (Player) sender;
        final ItemStack stackInHand = player.getInventory().getItemInMainHand();

        if (stackInHand.getType() == Material.AIR) {
            player.sendMessage(getLang("noItemInHand"));
        } else {
            final ItemStack itemToRemove = stackInHand.clone();

            if (!plugin.getDropManager().deleteDrop(itemToRemove)) {
                sender.sendMessage(getLang("dropNoExist"));
            } else {
                player.sendMessage(getLang("dropRemoved"));
            }
        }

        return true;
    }
}
