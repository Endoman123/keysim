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

    private int tick = 0;

    public KeyReset(KeyBinding keybind, int systemVK, int tickCount, int dummyKeyCode, int bindKeyCode) {
        this.keyBinding = keybind;
        this.systemVK = systemVK;
        this.tickCount = tickCount;
        this.dummyKeyCode = dummyKeyCode;
        this.bindKeyCode = bindKeyCode;
    }

    public Boolean incrementTick() {
        tick++;
        return tick >= tickCount;
    }
    public int getCurrentTick() { return tick; }

    public KeyBinding getKeyBinding() { return keyBinding; }

    public Boolean doSystemInput() { return systemVK != -1; }
    public int getSystemVK() { return systemVK; }

    public Boolean doKeySwap() { return dummyKeyCode != -1 && bindKeyCode != -1; }
    public int getDummyKeyCode() { return dummyKeyCode; }
    public int getBindKeyCode() { return bindKeyCode; }
}
