package me.jjm_223.smartgiants.commands;

import me.jjm_223.smartgiants.Drop;
import me.jjm_223.smartgiants.SmartGiants;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.jjm_223.smartgiants.LangManager.getLang;

public class CommandAdd extends CommandBase {
    private SmartGiants plugin;

    public CommandAdd(final SmartGiants plugin) {
        super("Add", "smartgiants.configure", true, 3);

        this.plugin = plugin;
    }

    @Override
    public boolean execute(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        final Player player = (Player) sender;
        final ItemStack stackInHand = player.getInventory().getItemInMainHand().clone();

        if (stackInHand.getType() == Material.AIR) {
            sender.sendMessage(getLang("noItemInHand"));
        } else {
            try {
                final int min = Integer.parseInt(args[0]);
                final int max = Integer.parseInt(args[1]);
                final int percent = Integer.parseInt(args[2]);

                plugin.getDropManager().addDrop(new Drop(stackInHand, min, max, percent));

                sender.sendMessage(getLang("dropAdded"));
            } catch (NumberFormatException e) {
                sender.sendMessage(getLang("lectureAdd"));
            }
        }

        return true;
    }
}
