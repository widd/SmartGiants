package me.jjm_223.smartgiants.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.jjm_223.smartgiants.LangManager.getLang;

public class CommandBase implements CommandExecutor {
    private static List<CommandBase> subCommands = new ArrayList<>();

    private final String name;
    private final String permission;
    private final boolean playerOnly;
    private final int minArgs;

    CommandBase(final String name, final String permission, final boolean playerOnly, final int minArgs) {
        this.name = name;
        this.permission = permission;
        this.playerOnly = playerOnly;
        this.minArgs = minArgs;

        subCommands.add(this);
    }

    public CommandBase() {
        this.name = null;
        this.permission = null;
        this.playerOnly = false;
        this.minArgs = 0;
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender commandSender, final @NotNull Command command, final @NotNull String s, final String[] strings) {
        if (strings.length < 1) {
            lecture(commandSender);

            return true;
        }

        final String commandName = strings[0];
        final List<String> args = Arrays.asList(strings).subList(1, strings.length);
        return subCommands
                .stream()
                .filter(c -> c.getName().equalsIgnoreCase(commandName))
                .findFirst()
                .map(subCommand -> {
                    if (!(commandSender instanceof Player) && subCommand.getPlayerRequired()) {
                        commandSender.sendMessage(getLang("mustBePlayer"));
                        return true;
                    }

                    if (subCommand.getPermission() != null && !commandSender.hasPermission(subCommand.getPermission())) {
                        commandSender.sendMessage(getLang("noPermission"));
                        return true;
                    }

                    if (args.size() < subCommand.getMinArgs()) {
                        lecture(commandSender);
                        return true;
                    }

                    final String[] newArgs = new String[args.size()];
                    return subCommand.execute(commandSender, command, s, args.toArray(newArgs));
                }).orElseGet(() -> {
                    lecture(commandSender);
                    return true;
                });
    }

    public boolean execute(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        return true;
    }

    private String getName() {
        return name;
    }

    private String getPermission() {
        return permission;
    }

    private boolean getPlayerRequired() {
        return playerOnly;
    }

    private int getMinArgs() {
        return minArgs;
    }

    private void lecture(final CommandSender sender) {
        sender.sendMessage(getLang("lectureBar"));
        sender.sendMessage(getLang("lectureAdd"));
        sender.sendMessage(getLang("lectureRemove"));
        sender.sendMessage(getLang("lectureReset"));
        sender.sendMessage(getLang("lectureReload"));
        sender.sendMessage(getLang("lectureReloadConfig"));
        sender.sendMessage(getLang("lectureBar"));
    }
}
