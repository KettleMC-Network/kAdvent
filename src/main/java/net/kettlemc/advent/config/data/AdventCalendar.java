package net.kettlemc.advent.config.data;

import java.util.List;
import java.util.Map;

public class AdventCalendar {

    private Map<Integer, List<Map<String, Object>>> days;


    public Map<Integer, List<Map<String, Object>>> getDays() {
        return this.days;
    }

    public void setDays(Map<Integer, List<Map<String, Object>>> days) {
        this.days = days;
    }
}
