package net.kettlemc.advent.command;

import io.github.almightysatan.slams.Placeholder;
import net.kettlemc.advent.KAdvent;
import net.kettlemc.advent.config.Configuration;
import net.kettlemc.advent.config.Messages;
import net.kettlemc.advent.gui.AdventCalenderGUI;
import net.kettlemc.kcommon.java.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AdventCalenderCommand implements CommandExecutor, TabCompleter {

    private static final List<String> MATERIALS = Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList());

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                KAdvent.instance().messages().sendMessage(sender, Messages.PLAYER_ONLY);
                return true;
            }
            AdventCalenderGUI.open((Player) sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("system.advent.reload")) {
                KAdvent.instance().messages().sendMessage(sender, Messages.NO_PERMISSION);
                return true;
            }

            KAdvent.instance().messages().sendMessage(sender, Messages.RELOADING);
            try {
                if (!Configuration.reload()) throw new Exception("Could not load config!");
                KAdvent.instance().adventDataHandler().loadCalendar();
                KAdvent.instance().messages().sendMessage(sender, Messages.RELOADED);
            } catch (Exception e) {
                KAdvent.instance().messages().sendMessage(sender, Messages.RELOAD_FAILED);
                e.printStackTrace();
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("save")) {

            if (!sender.hasPermission("system.advent.save")) {
                KAdvent.instance().messages().sendMessage(sender, Messages.NO_PERMISSION);
                return true;
            }

            KAdvent.instance().messages().sendMessage(sender, Messages.SAVING);
            KAdvent.instance().adventDataHandler().saveAll();
            return true;
        }

        if (args[0].equalsIgnoreCase("material")) {

            if (!sender.hasPermission("system.advent.material")) {
                KAdvent.instance().messages().sendMessage(sender, Messages.NO_PERMISSION);
                return true;
            }

            if (args.length < 2) {
                KAdvent.instance().messages().sendMessage(sender, Messages.DOOR_MATERIAL_USAGE);
                return true;
            }

            Material material = Material.getMaterial(args[1]);

            if (material == null) {
                KAdvent.instance().messages().sendMessage(sender, Messages.DOOR_MATERIAL_INVALID);
                return true;
            }


            Configuration.DOOR_MATERIAL.setValue(material.name());
            Configuration.write();

            KAdvent.instance().messages().sendMessage(sender, Messages.DOOR_MATERIAL_SET, Placeholder.of("material", (ctx, value) -> material.name()));
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {

            if (!(sender instanceof Player)) {
                KAdvent.instance().messages().sendMessage(sender, Messages.PLAYER_ONLY);
                return true;
            }

            if (!sender.hasPermission("system.advent.set")) {
                KAdvent.instance().messages().sendMessage(sender, Messages.NO_PERMISSION);
                return true;
            }

            if (args.length >= 2 && NumberUtil.isInteger(args[1])) {
                int day = Integer.parseInt(args[1]);

                if (day < 1 || day > 24) {
                    KAdvent.instance().messages().sendMessage(sender, Messages.DOOR_INVALID);
                    return true;
                }

                List<ItemStack> items = Arrays.stream(((Player) sender).getInventory().getContents()).filter(Objects::nonNull).collect(Collectors.toList());
                KAdvent.instance().adventDataHandler().setItems(day, items);
                KAdvent.instance().messages().sendMessage(sender, Messages.DOOR_SET);

            } else {
                KAdvent.instance().messages().sendMessage(sender, Messages.SET_USAGE);
            }
            return true;
        }

        if (args[0].equals("give")) {

            if (!(sender instanceof Player)) {
                KAdvent.instance().messages().sendMessage(sender, Messages.PLAYER_ONLY);
                return true;
            }

            if (!sender.hasPermission("system.advent.give")) {
                KAdvent.instance().messages().sendMessage(sender, Messages.NO_PERMISSION);
                return true;
            }

            if (args.length >= 2 && NumberUtil.isInteger(args[1])) {
                int day = Integer.parseInt(args[1]);

                if (day < 1 || day > 24) {
                    KAdvent.instance().messages().sendMessage(sender, Messages.DOOR_INVALID);
                    return true;
                }

                List<ItemStack> items = KAdvent.instance().adventDataHandler().getItems(day);
                if (items == null) {
                    KAdvent.instance().messages().sendMessage(sender, Messages.DOOR_NOT_CONFIGURED);
                    return true;
                }

                items.forEach(item -> ((Player) sender).getInventory().addItem(item));
                KAdvent.instance().messages().sendMessage(sender, Messages.DOOR_GIVEN);

            } else {
                KAdvent.instance().messages().sendMessage(sender, Messages.GIVE_USAGE);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {

            if (!sender.hasPermission("system.advent.reset")) {
                KAdvent.instance().messages().sendMessage(sender, Messages.NO_PERMISSION);
                return true;
            }

            if (args.length >= 2) {
                Player player = Bukkit.getPlayer(args[1]);

                if (player == null) {
                    KAdvent.instance().messages().sendMessage(sender, Messages.RESET_USAGE);
                    return true;
                }

                if (args.length >= 3 && NumberUtil.isInteger(args[2])) {
                    int day = Integer.parseInt(args[2]);
                    if (day < 1 || day > 24) {
                        KAdvent.instance().messages().sendMessage(sender, Messages.DOOR_INVALID);
                        return true;
                    }

                    KAdvent.instance().adventDataHandler().loadPlayer(player.getUniqueId()).remove(day);
                    KAdvent.instance().messages().sendMessage(sender, Messages.RESET_DOOR, Placeholder.of("door", (ctx, value) -> String.valueOf(day)), Placeholder.of("player", (ctx, value) -> player.getName()));
                    return true;
                } else {

                    KAdvent.instance().adventDataHandler().loadPlayer(player.getUniqueId()).reset();
                    KAdvent.instance().messages().sendMessage(sender, Messages.RESET_PLAYER, Placeholder.of("player", (ctx, value) -> player.getName()));
                    return true;
                }
            } else {
                KAdvent.instance().messages().sendMessage(sender, Messages.RESET_USAGE);
            }
            return true;
        }

        KAdvent.instance().messages().sendMessage(sender, Messages.USAGE);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            return StringUtil.copyPartialMatches(args.length == 1 ? args[0] : "", Arrays.asList("reload", "save", "set", "give", "reset", "material"), new ArrayList<>());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            return null;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("set")) {
            return IntStream.range(1, 25).boxed().sorted().map(String::valueOf).collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("material")) {
            return MATERIALS;
        }

        return Collections.emptyList();
    }
}
