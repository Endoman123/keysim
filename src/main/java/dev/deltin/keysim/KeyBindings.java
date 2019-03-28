package dev.deltin.keysim;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {
    public static KeyBinding dummyKey;
    public static void init() {
        dummyKey = new KeyBinding("key.dummyKey", Keyboard.KEY_DIVIDE, "key.categories.KeySim");
        ClientRegistry.registerKeyBinding(dummyKey);
    }
}