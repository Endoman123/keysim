package dev.deltin.keysim;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {
    public static KeyBinding keepForward;
    public static void init() {
        keepForward = new KeyBinding("key.keepForward", Keyboard.KEY_O, "key.categories.Autowalk");
        ClientRegistry.registerKeyBinding(keepForward);
    }
}