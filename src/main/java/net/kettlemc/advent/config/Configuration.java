package net.kettlemc.advent.config;

import io.github.almightysatan.jaskl.Config;
import io.github.almightysatan.jaskl.entries.BooleanConfigEntry;
import io.github.almightysatan.jaskl.entries.IntegerConfigEntry;
import io.github.almightysatan.jaskl.entries.StringConfigEntry;
import io.github.almightysatan.jaskl.hocon.HoconConfig;
import org.bukkit.Material;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration {

    public static final Path CONFIG_DIRECTORY = Paths.get("plugins", "kAdvent");
    private static final Config CONFIG = HoconConfig.of(CONFIG_DIRECTORY.resolve("kadvent.conf").toFile(), "Config for kAdvent");

    public static final StringConfigEntry DOOR_MATERIAL = StringConfigEntry.of(CONFIG, "settings.material.type", Material.CHEST.name());
    public static final IntegerConfigEntry DOOR_DATA = IntegerConfigEntry.of(CONFIG, "settings.material.data", 0);
    public static final StringConfigEntry DOOR_SKULL_OWNER = StringConfigEntry.of(CONFIG, "settings.material.skull-owner", "_ruuzZ");
    public static final IntegerConfigEntry AUTO_SAVE_INTERVAL_SECONDS = IntegerConfigEntry.of(CONFIG, "settings.auto-save-interval-seconds", 300);

    public static final BooleanConfigEntry ANY_MONTH = BooleanConfigEntry.of(CONFIG, "settings.debug.any-month", false);
    public static final BooleanConfigEntry ANY_DAY = BooleanConfigEntry.of(CONFIG, "settings.debug.any-day", false);

    public static final IntegerConfigEntry MAX_DAYS_LATE = IntegerConfigEntry.of(CONFIG, "settings.max-days-late", "The maximum amount of days a player can be late before they can't open any more doors.", 2);

    private Configuration() {
    }

    public static boolean load() {
        try {
            CONFIG.load();
            CONFIG.write();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean reload() {
        try {
            CONFIG.reload();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void unload() {
        CONFIG.close();
    }

    public static boolean write() {
        try {
            CONFIG.write();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
