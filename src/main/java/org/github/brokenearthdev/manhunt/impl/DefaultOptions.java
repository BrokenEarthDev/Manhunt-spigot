package org.github.brokenearthdev.manhunt.impl;

import org.github.brokenearthdev.manhunt.ManhuntPlugin;

public class DefaultOptions extends GameOptionsImpl {

    public DefaultOptions() {
        super(checkGracePeriodEnabled(), checkAllowTrackers(), checkGenerateMainWorld(), checkGenerateEndWorld(),
                checkGenerateEndWorld(), dumpToConfig(), playersMax(), playersMin(), checkGracePeriodEnabled() ?
                        checkGracePeriodTime() : 0);
    }

    private static boolean checkGracePeriodEnabled() {
        return ManhuntPlugin.getInstance().getOptionsConfig().getBoolean("allow_grace_period", true);
    }

    private static int checkGracePeriodTime() {
        return ManhuntPlugin.getInstance().getOptionsConfig().getInt("grace_period_seconds");
    }

    private static boolean checkAllowTrackers() {
        return ManhuntPlugin.getInstance().getOptionsConfig().getBoolean("allow_hunter_trackers");
    }

    private static boolean checkGenerateMainWorld() {
        return ManhuntPlugin.getInstance().getOptionsConfig().getBoolean("generator_settings.generate_world");
    }

    private static boolean checkGenerateNetherWorld() {
        return ManhuntPlugin.getInstance().getOptionsConfig().getBoolean("generator_settings.generate_nether");
    }

    private static boolean checkGenerateEndWorld() {
        return ManhuntPlugin.getInstance().getOptionsConfig().getBoolean("generator_settings.generate_end");
    }

    private static boolean allowHunterTrackers() {
        return ManhuntPlugin.getInstance().getOptionsConfig().getBoolean("allow_hunter_trackers");
    }

    private static int playersMax() {
        return ManhuntPlugin.getInstance().getOptionsConfig().getInt("players.max_players");
    }

    private static int playersMin() {
        return ManhuntPlugin.getInstance().getOptionsConfig().getInt("players.min_players");
    }

    private static boolean dumpToConfig() {
        return ManhuntPlugin.getInstance().getOptionsConfig().getBoolean("dump_game_info_to_config");
    }

}
