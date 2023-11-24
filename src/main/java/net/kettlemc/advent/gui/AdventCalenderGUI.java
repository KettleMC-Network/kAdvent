package net.kettlemc.advent.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import io.github.almightysatan.slams.Placeholder;
import io.github.almightysatan.slams.minimessage.AdventureMessage;
import net.kettlemc.advent.KAdvent;
import net.kettlemc.advent.config.Configuration;
import net.kettlemc.advent.config.Messages;
import net.kettlemc.advent.config.data.AdventDataHandler;
import net.kettlemc.kcommon.language.AdventureUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class AdventCalenderGUI {


    private static final int[] DOOR_SLOTS = new int[]{
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            39, 40, 41
    };

    private static final String TAG = "advent";

    private AdventCalenderGUI() {
    }

    public static void open(Player player) {
        SGMenu menu = KAdvent.spigui().create(AdventureUtil.componentToLegacy(Messages.CALENDER_TITLE.value()), 6, TAG);

        for (int i = 0; i < 24; i++) {
            int day = i + 1;

            SGButton button = new SGButton(itemStack(KAdvent.instance().adventDataHandler().loadPlayer(player.getUniqueId()).hasOpened(day), day));

            menu.setButton(DOOR_SLOTS[i], button.withListener(event -> {
                handleClick(player, day);
                button.setIcon(itemStack(KAdvent.instance().adventDataHandler().loadPlayer(player.getUniqueId()).hasOpened(day), day));
                menu.setButton(event.getSlot(), button);
                menu.refreshInventory(player);
            }));

            player.openInventory(menu.getInventory());
        }
    }

    private static ItemStack itemStack(boolean open, int day) {
        Placeholder placeholder = Placeholder.of("door", (ctx, value) -> String.valueOf(day));

        ItemBuilder item = new ItemBuilder(Material.getMaterial(Configuration.DOOR_MATERIAL.getValue()))
                .data((short) (int) Configuration.DOOR_DATA.getValue())
                .skullOwner(Configuration.DOOR_SKULL_OWNER.getValue())
                .name(AdventureUtil.componentToLegacy(open ? Messages.DOOR_TITLE_OPENED.value(placeholder) : Messages.DOOR_TITLE_CLOSED.value(placeholder)))
                .amount(day)
                .ifThen(itemBuilder -> open, itemBuilder -> itemBuilder.enchant(Enchantment.SILK_TOUCH, 1))
                .flag(ItemFlag.HIDE_ENCHANTS);

        return item.build();
    }

    private static void handleClick(Player player, int day) {
        AdventDataHandler.OpenResult result = KAdvent.instance().adventDataHandler().open(player, day);

        AdventureMessage message = null;

        switch (result) {
            case SUCCESSFUL:
                message = Messages.DOOR_OPENED;
                break;
            case TOO_EARLY:
                message = Messages.DOOR_TOO_EARLY;
                break;
            case TOO_LATE:
                message = Messages.DOOR_TOO_LATE;
                break;
            case ALREADY_OPENED:
                message = Messages.DOOR_ALREADY_OPENED;
                break;
            case INVENTORY_FULL:
                message = Messages.DOOR_INVENTORY_FULL;
                break;
            case NOT_CONFIGURED:
                message = Messages.DOOR_NOT_CONFIGURED;
                break;
        }
        KAdvent.instance().messages().sendMessage(player, message, Placeholder.of("door", (ctx, value) -> String.valueOf(day)));
    }


}
