package net.kettlemc.advent.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

    private InventoryUtil() {
    }


    public static boolean canHold(Inventory inventory, ItemStack itemStack) {
        int amount = itemStack.getAmount();
        System.out.println(itemStack.getAmount() + " " + itemStack.getMaxStackSize());

        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                System.out.println("found empty slot");
                amount -= itemStack.getMaxStackSize();
            } else if (item.isSimilar(itemStack)) {
                System.out.println("found similar: " + item.getAmount());
                amount -= item.getMaxStackSize() - item.getAmount();
            }
            if (amount <= 0) {
                System.out.println("returning true");
                return true;
            }
        }
        return false;
    }

}
