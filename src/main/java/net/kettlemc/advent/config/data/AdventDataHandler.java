package net.kettlemc.advent.config.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.kettlemc.advent.KAdvent;
import net.kettlemc.advent.config.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdventDataHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static final File CALENDAR_FILE = Configuration.CONFIG_DIRECTORY.resolve("data").resolve("calendar.json").toFile();

    private final HashMap<UUID, AdventPlayer> players = new HashMap<>();
    private final Map<Integer, List<ItemStack>> items = new HashMap<>();

    /**
     * Returns whether the door for a certain day can be opened today.
     *
     * @param day The day to check
     * @return Whether the door for the provided day can be opened today.
     */
    public static boolean allowedToOpenYet(int day) {
        LocalDate currentDate = LocalDate.now();

        if (!Configuration.ANY_MONTH.getValue() && currentDate.getMonth() != Month.DECEMBER) {
            return false;
        }

        int today = currentDate.getDayOfMonth();

        return Configuration.ANY_DAY.getValue() || day <= today && day + Configuration.MAX_DAYS_LATE.getValue() >= today;

    }

    /**
     * Loads the advent calendar data from the calendar file.
     */
    public void loadCalendar() {
        AdventCalendar adventCalendar;

        if (!CALENDAR_FILE.exists()) {
            this.saveCalender();
        }

        try {
            adventCalendar = OBJECT_MAPPER.readValue(CALENDAR_FILE, AdventCalendar.class);
        } catch (IOException e) {
            KAdvent.instance().plugin().getLogger().severe("Couldn't load calender from file!");
            throw new RuntimeException(e);
        }

        adventCalendar.getDays().forEach((day, deserializedItems) -> {
            List<ItemStack> itemsForDay = new ArrayList<>();
            deserializedItems.forEach((deserialized) -> itemsForDay.add(ItemStack.deserialize(deserialized)));
            this.items.put(day, itemsForDay);
        });
    }

    /**
     * Saves the calendar to the file.
     */
    public void saveCalender() {
        AdventCalendar adventCalendar = new AdventCalendar();

        Map<Integer, List<Map<String, Object>>> days = new HashMap<>();
        this.items.forEach((day, itemStacks) -> {
            List<Map<String, Object>> itemsForDay = new ArrayList<>();
            itemStacks.forEach(itemStack -> itemsForDay.add(itemStack.serialize()));
            days.put(day, itemsForDay);
        });

        adventCalendar.setDays(days);

        try {
            CALENDAR_FILE.getParentFile().mkdirs();
            OBJECT_MAPPER.writeValue(CALENDAR_FILE, adventCalendar);
        } catch (IOException e) {
            KAdvent.instance().plugin().getLogger().severe("Couldn't save calender to file!");
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to open a door for a player. If the player has already opened the door, the item stack will be null.
     * This will give the items to the player.
     *
     * @param player The player trying to open a door.
     * @param day    The day the player tries to open the door for.
     * @return The item stack to give to the player or null if the player already opened the door.
     */
    public OpenResult open(Player player, int day) {

        if (!allowedToOpenYet(day)) {
            return LocalDate.now().getDayOfMonth() > day ? OpenResult.TOO_LATE : OpenResult.TOO_EARLY;
        }

        if (!this.items.containsKey(day)) {
            return OpenResult.NOT_CONFIGURED;
        }

        AdventPlayer adventPlayer = loadPlayer(player.getUniqueId());

        if (adventPlayer.hasOpened(day)) return OpenResult.ALREADY_OPENED;

        AtomicBoolean dropped = new AtomicBoolean(false);

        for (ItemStack item : items.get(day)) {
            player.getInventory().addItem(item).values().forEach(itemStack -> {
                player.getWorld().dropItem(player.getLocation(), itemStack);
                dropped.set(true);
            });
        }

        adventPlayer.setOpen(day);
        return dropped.get() ? OpenResult.INVENTORY_FULL : OpenResult.SUCCESSFUL;

    }

    /**
     * Loads the corresponding advent player data for the uuid.
     * <p>
     * If the player doesn't exist yet, a new player will be created and saved.
     * If the player exists, it will be loaded from file.
     * If the player is already loaded, it will be returned without loading it.
     *
     * @param uuid The uuid of the player data to load.
     * @return The loaded player
     */
    public AdventPlayer loadPlayer(UUID uuid) {

        // Check if player is loaded
        if (this.players.containsKey(uuid)) return this.players.get(uuid);

        File file = Configuration.CONFIG_DIRECTORY.resolve("data").resolve(uuid + ".json").toFile();
        file.getParentFile().mkdirs();

        // Create and save a new player if it doesn't exist
        if (!file.exists()) {
            AdventPlayer newPlayer = new AdventPlayer(uuid);
            this.players.put(uuid, newPlayer);
            savePlayer(uuid);
            return newPlayer;
        }

        // Load the existing player
        AdventPlayer adventPlayer;
        try {
            adventPlayer = OBJECT_MAPPER.readValue(file, AdventPlayer.class);
        } catch (IOException e) {
            KAdvent.instance().plugin().getLogger().severe("Couldn't load player '" + uuid + "'!");
            throw new RuntimeException(e);
        }

        this.players.put(uuid, adventPlayer);

        return adventPlayer;

    }

    /**
     * Saves a player to their file or creates a new file if it doesn't exist.
     *
     * @param uuid The uuid of the player to save.
     */
    public void savePlayer(UUID uuid) {
        File file = Configuration.CONFIG_DIRECTORY.resolve("data").resolve(uuid + ".json").toFile();
        try {
            file.createNewFile();
            OBJECT_MAPPER.writeValue(file, this.players.get(uuid));
        } catch (IOException e) {
            KAdvent.instance().plugin().getLogger().severe("Couldn't save player '" + uuid + "'!");
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves and then unloads the player.
     *
     * @param uuid The uuid of the player to unload
     */
    public void unloadPlayer(UUID uuid) {
        savePlayer(uuid);
        this.players.remove(uuid);

    }

    public List<ItemStack> getItems(int day) {
        return this.items.get(day);
    }

    public void setItems(int day, List<ItemStack> itemStack) {
        this.items.put(day, itemStack);
    }

    public void setItem(int day, ItemStack itemStack) {
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(itemStack);
        setItems(day, items);
    }

    /**
     * Returns true if the items have been configured or this day.
     *
     * @param day The day to check
     * @return Whether the items have been configured for this day.
     */
    public boolean isSet(int day) {
        return this.items.containsKey(day);
    }

    public enum OpenResult {
        SUCCESSFUL,
        ALREADY_OPENED,
        NOT_CONFIGURED,
        INVENTORY_FULL,
        TOO_LATE,
        TOO_EARLY
    }

    public void saveAll() {
        saveCalender();
        this.players.forEach((uuid, player) -> savePlayer(uuid));
    }
}
