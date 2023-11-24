package net.kettlemc.advent.listener;

import net.kettlemc.advent.KAdvent;
import net.kettlemc.advent.config.Messages;
import net.kettlemc.advent.config.data.AdventPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDate;
import java.time.Month;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        AdventPlayer player = KAdvent.instance().adventDataHandler().loadPlayer(event.getPlayer().getUniqueId());
        int day = LocalDate.now().getDayOfMonth();
        int month = LocalDate.now().getMonthValue();
        if (month == Month.DECEMBER.getValue() && day <= 24 && !player.hasOpened(day))
            KAdvent.instance().messages().sendMessage(event.getPlayer(), Messages.DOOR_AVAILABLE);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        KAdvent.instance().adventDataHandler().unloadPlayer(event.getPlayer().getUniqueId());
    }
}
