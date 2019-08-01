package me.jjm_223.smartgiants.api.util;

public interface INaturalSpawns {
    void load(boolean hostile, boolean daylight, int frequency, int minGroupAmount, int maxGroupAmount);

    void cleanup();
}
