package dev.deltin.keysim;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.client.settings.KeyBinding;

public class KeyReset {

    // Global
    private KeyBinding keyBinding;
    private int systemVK;
    private int tickCount;
    private int dummyKeyCode;
    private int bindKeyCode;
    private Boolean hold;

    private int tick = 0;

    public KeyReset(KeyBinding keybind, int systemVK, int tickCount, int dummyKeyCode, int bindKeyCode, Boolean hold) {
        this.keyBinding = keybind;
        this.systemVK = systemVK;
        this.tickCount = tickCount;
        this.dummyKeyCode = dummyKeyCode;
        this.bindKeyCode = bindKeyCode;
        this.hold = hold;
    }

    public Boolean incrementTick() {
        tick++;
        return tick >= tickCount;
    }

    public KeyBinding getKeyBinding() { return keyBinding; }
    public int getSystemVK() { return systemVK; }
    public int getDummyKeyCode() { return dummyKeyCode; }
    public int getBindKeyCode() { return bindKeyCode; }
    public Boolean shouldHold() { return hold; }
}
