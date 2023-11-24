package net.kettlemc.advent;

import com.samjakob.spigui.SpiGUI;
import net.kettlemc.advent.command.AdventCalenderCommand;
import net.kettlemc.advent.config.Configuration;
import net.kettlemc.advent.config.Messages;
import net.kettlemc.advent.config.data.AdventDataHandler;
import net.kettlemc.advent.listener.JoinQuitListener;
import net.kettlemc.advent.loading.Loadable;
import net.kettlemc.kcommon.bukkit.ContentManager;
import net.kettlemc.kcommon.language.MessageManager;
import net.kettlemc.klanguage.api.LanguageAPI;
import net.kettlemc.klanguage.bukkit.BukkitLanguageAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class KAdvent implements Loadable {

    public static final LanguageAPI<Player> LANGUAGE_API = BukkitLanguageAPI.of();
    private static KAdvent instance;
    private static SpiGUI spigui;
    private final ContentManager contentManager;
    private final JavaPlugin plugin;
    private BukkitAudiences adventure;
    private MessageManager messageManager;
    private AdventDataHandler adventDataHandler;
    private final BukkitRunnable autoSaveTask = new BukkitRunnable() {
        @Override
        public void run() {
            adventDataHandler.saveAll();
        }
    };


    public KAdvent(JavaPlugin plugin) {
        this.plugin = plugin;
        this.contentManager = new ContentManager(plugin);
    }

    public static KAdvent instance() {
        return instance;
    }

    public static SpiGUI spigui() {
        return spigui;
    }

    @Override
    public void onEnable() {

        instance = this;
        spigui = new SpiGUI(this.plugin);

        this.plugin.getLogger().info("Loading adventure support...");
        this.adventure = BukkitAudiences.create(this.plugin);

        if (!Configuration.load()) {
            this.plugin.getLogger().severe("Failed to load config!");
        }

        if (!Messages.load()) {
            this.plugin.getLogger().severe("Failed to load messages!");
        }

        this.messageManager = new MessageManager(Messages.PREFIX, LANGUAGE_API, adventure());
        this.adventDataHandler = new AdventDataHandler();
        this.adventDataHandler.loadCalendar();
        this.autoSaveTask.runTaskTimerAsynchronously(this.plugin, Configuration.AUTO_SAVE_INTERVAL_SECONDS.getValue() * 20, Configuration.AUTO_SAVE_INTERVAL_SECONDS.getValue() * 20);

        this.contentManager.registerListener(new JoinQuitListener());
        this.contentManager.registerCommand("advent", new AdventCalenderCommand());
    }

    @Override
    public void onDisable() {
        this.plugin.getLogger().info("Goodbye, World!");
        Configuration.unload();
        this.adventDataHandler.saveAll();
        this.autoSaveTask.cancel();
    }

    public BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public MessageManager messages() {
        return messageManager;
    }

    public ContentManager contentManager() {
        return contentManager;
    }

    public AdventDataHandler adventDataHandler() {
        return this.adventDataHandler;
    }
}
