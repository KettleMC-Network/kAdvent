package net.kettlemc.advent.config.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AdventPlayer {

    private UUID uuid;
    private List<Integer> openedDays;

    public AdventPlayer() {

    }

    public AdventPlayer(UUID uuid) {
        this.uuid = uuid;
        this.openedDays = new ArrayList<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<Integer> getOpenedDays() {
        return Collections.unmodifiableList(this.openedDays);
    }

    public AdventPlayer setOpenedDays(List<Integer> openedDays) {
        this.openedDays = openedDays;
        return this;
    }

    public AdventPlayer setUUID(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public AdventPlayer setOpen(int day) {
        this.openedDays.add(day);
        return this;
    }

    public AdventPlayer reset() {
        this.openedDays.clear();
        return this;
    }

    public AdventPlayer remove(int day) {
        this.openedDays.remove((Integer) day);
        return this;
    }

    public boolean hasOpened(int day) {
        return this.openedDays.contains(day);
    }

}
