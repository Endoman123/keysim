package dev.deltin.keysim;

import net.minecraft.client.settings.KeyBinding;

public class KeyReset {

    // Global
    private Boolean isKeybind;
    private int tick = 0;
    private int tickCount;

    private KeyReset(Boolean keybind, int tickCount)
    {
        this.isKeybind = keybind;
        this.tickCount = tickCount;
    }
    public void incrementTick() { tick++; }
    public int getCurrentTick() { return tick; }
    public int getTickCount() { return tickCount; }
    public Boolean isKeybind() { return isKeybind; }

    // Keybind
    private KeyBinding keyBinding;
    public KeyReset(KeyBinding keyBinding, int tickCount) {
        this(true, tickCount);
        this.keyBinding = keyBinding;
    }
    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    // Sim
    private int keyCode;
    public KeyReset(int keyCode, int tickCount) {
        this(false, tickCount);
        this.keyCode = keyCode;
    }
    public int getKeyCode() {
        return keyCode;
    }
}
