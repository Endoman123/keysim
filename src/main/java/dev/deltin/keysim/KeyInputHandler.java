package dev.deltin.keysim;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyInputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(KeyBindings.keepForward.isPressed())
        {
            KeyBinding bind = FMLClientHandler.instance().getClient().gameSettings.keyBindJump;
            if (bind.isKeyDown()) { //Player is going forwards, make them stop
                KeyBinding.setKeyBindState(bind.getKeyCode(), false);
            } else { //Player is not going forwards, make them start
                KeyBinding.setKeyBindState(bind.getKeyCode(), true);
            }
        }
    }
}