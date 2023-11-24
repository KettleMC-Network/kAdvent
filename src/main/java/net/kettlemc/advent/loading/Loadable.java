package net.kettlemc.advent.loading;

public interface Loadable {

    default void onLoad() {
    }

    default void onEnable() {
    }

    default void onDisable() {
    }
}
