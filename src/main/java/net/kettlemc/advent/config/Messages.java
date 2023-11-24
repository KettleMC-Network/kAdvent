package net.kettlemc.advent.config;

import io.github.almightysatan.slams.Slams;
import io.github.almightysatan.slams.minimessage.AdventureMessage;
import io.github.almightysatan.slams.parser.JacksonParser;
import net.kettlemc.kcommon.java.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Messages {

    private static final String DEFAULT_LANGUAGE = "de";

    private static final Path LANGUAGE_PATH = Paths.get("plugins", "kAdvent", "languages");
    private static final Slams LANGUAGE_MANAGER = Slams.create(DEFAULT_LANGUAGE);

    public static final AdventureMessage PREFIX = AdventureMessage.of("prefix", LANGUAGE_MANAGER);
    public static final AdventureMessage NO_PERMISSION = AdventureMessage.of("no-permission", LANGUAGE_MANAGER);
    public static final AdventureMessage PLAYER_ONLY = AdventureMessage.of("player-only", LANGUAGE_MANAGER);

    public static final AdventureMessage DOOR_OPENED = AdventureMessage.of("calender.door.opened", LANGUAGE_MANAGER);
    public static final AdventureMessage DOOR_TOO_EARLY = AdventureMessage.of("calender.door.too-early", LANGUAGE_MANAGER);
    public static final AdventureMessage DOOR_TOO_LATE = AdventureMessage.of("calender.door.too-late", LANGUAGE_MANAGER);
    public static final AdventureMessage DOOR_ALREADY_OPENED = AdventureMessage.of("calender.door.already-opened", LANGUAGE_MANAGER);
    public static final AdventureMessage DOOR_INVENTORY_FULL = AdventureMessage.of("calender.door.inventory-full", LANGUAGE_MANAGER);
    public static final AdventureMessage DOOR_NOT_CONFIGURED = AdventureMessage.of("calender.door.not-configured", LANGUAGE_MANAGER);
    public static final AdventureMessage DOOR_INVALID = AdventureMessage.of("calender.door.invalid", LANGUAGE_MANAGER);
    public static final AdventureMessage DOOR_SET = AdventureMessage.of("calender.door.set", LANGUAGE_MANAGER);
    public static final AdventureMessage DOOR_GIVEN = AdventureMessage.of("calender.door.given", LANGUAGE_MANAGER);

    public static final AdventureMessage USAGE = AdventureMessage.of("usage.main", LANGUAGE_MANAGER);
    public static final AdventureMessage SET_USAGE = AdventureMessage.of("usage.set", LANGUAGE_MANAGER);
    public static final AdventureMessage GIVE_USAGE = AdventureMessage.of("usage.give", LANGUAGE_MANAGER);
    public static final AdventureMessage RESET_USAGE = AdventureMessage.of("usage.reset", LANGUAGE_MANAGER);

    public static final AdventureMessage RESET_DOOR = AdventureMessage.of("reset.door", LANGUAGE_MANAGER);
    public static final AdventureMessage RESET_PLAYER = AdventureMessage.of("reset.player", LANGUAGE_MANAGER);

    public static final AdventureMessage DOOR_TITLE_OPENED = AdventureMessage.of("calender.title.opened", LANGUAGE_MANAGER);
    public static final AdventureMessage DOOR_TITLE_CLOSED = AdventureMessage.of("calender.title.closed", LANGUAGE_MANAGER);
    public static final AdventureMessage CALENDER_TITLE = AdventureMessage.of("calender.title.main", LANGUAGE_MANAGER);
    public static final AdventureMessage DOOR_AVAILABLE = AdventureMessage.of("calender.door-available", LANGUAGE_MANAGER);

    public static final AdventureMessage RELOADING = AdventureMessage.of("reload.reloading", LANGUAGE_MANAGER);
    public static final AdventureMessage RELOADED = AdventureMessage.of("reload.reloaded", LANGUAGE_MANAGER);
    public static final AdventureMessage RELOAD_FAILED = AdventureMessage.of("reload.failed", LANGUAGE_MANAGER);
    public static final AdventureMessage SAVING = AdventureMessage.of("save.saving", LANGUAGE_MANAGER);

    private Messages() {
    }

    /**
     * Loads all messages from the language files.
     *
     * @return Whether the loading was successful.
     */
    public static boolean load() {
        if (!LANGUAGE_PATH.toFile().exists())
            LANGUAGE_PATH.toFile().mkdirs();

        FileUtil.saveResourceAsFile(Messages.class, "lang/de.json", LANGUAGE_PATH.resolve("de.json"));
        FileUtil.saveResourceAsFile(Messages.class, "lang/en.json", LANGUAGE_PATH.resolve("en.json"));

        try {
            loadFromFilesInDirectory(LANGUAGE_PATH.toFile());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Loads all json files in the given directory.
     *
     * @param directory The directory to load from.
     * @throws IOException If an error occurs while loading.
     */
    private static void loadFromFilesInDirectory(@NotNull File directory) throws IOException {
        if (!directory.isDirectory()) return;
        for (File file : Objects.requireNonNull(LANGUAGE_PATH.toFile().listFiles())) {
            if (file.isDirectory()) loadFromFilesInDirectory(file);
            else if (file.getName().endsWith(".json"))
                LANGUAGE_MANAGER.load(file.getName().replace(".json", ""), JacksonParser.createJsonParser(file));
        }
    }

}
