package dev.deltin.keysim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.Sys;

import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;

public class KeySimCommand implements IClientCommand {

    // Command info
    public static final String COMMAND_NAME = "keysim";
    public static final String ALIAS = "/" + COMMAND_NAME;
    public static final String USAGE = TextFormatting.RED + "Usage: /" + COMMAND_NAME + " <key> (hold [true|false])";
    // Reset info
    private static KeyReset KeyResetInfo = null;
    private static final int DEFAULT_TICK_RESET = 20;
    // Input
    private static Robot ROBOT = null;
    private static final Field[] VK_KEYCODES = java.awt.event.KeyEvent.class.getDeclaredFields();

    public KeySimCommand() {
        try
        {
            ROBOT = new Robot();
        }
        catch (Exception e)
        {
            System.out.println("Could not initialize sim robot.");
        }
    }

    // Probably means is / required?
    // false = "/help", true = "help"
    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    // The name of the command.
    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    // Used in /help?
    @Override
    public String getUsage(ICommandSender sender) {
        return USAGE;
    }

    // IDK
    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add(ALIAS);
        return aliases;
    }

    // On Command Execute
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (ROBOT == null)
        {
            error("Error: ROBOT failed to initialized.");
            return;
        }

        if (args.length < 1) {
            printUsage();
            return;
        }

        if (KeyResetInfo != null)
            return;

        // Get the key name argument.
        String keyName = args[0];

        // Get the hold argument.
        Boolean hold = false;
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("true"))
                hold = true;
            else if (!args[1].equalsIgnoreCase("false"))
            {
                printUsage();
                return;
            }
        }

        Minecraft mc = Minecraft.getMinecraft();

        // Look for the key in the game settings
        for (KeyBinding bind : mc.gameSettings.keyBindings) {
            if (bind.getKeyDescription().equalsIgnoreCase(keyName)) {

                // Get the dummy key code...
                int dummyKeyCode = KeyBindings.dummyKey.getKeyCode();
                if (dummyKeyCode == 0)
                {
                    error("Error: " + KeyBindings.dummyKey.getKeyDescription() + " is not set.");
                    return;
                }
                // ...then set it's value to none.
                KeyBindings.dummyKey.setKeyCode(0);

                // Set the target key code to the old dummy key code.
                int bindKeyCode = bind.getKeyCode();
                bind.setKeyCode(dummyKeyCode);

                // Update the client's key bindings.
                KeyBinding.resetKeyBindingArrayAndHash();

                // Get the system VK equivalent of the keycode.
                int vk = -1;
                for (Field f : VK_KEYCODES) {
                    if (Modifier.isStatic(f.getModifiers()) && f.getName().equals("VK_" + bind.getDisplayName())) {
                        try {
                            vk = (int) f.get(null);
                        }
                        catch(IllegalAccessException e) {
                            error("Error: \"" + e.getMessage() + "\"");
                        }
                        break;
                    }
                }

                if (vk == -1) {
                    error("Error: Invalid key " + bind.getDisplayName() + " (" + "VK_" + bind.getDisplayName() + "), rebind it to something else!");
                    return;
                }

                // Close whatever GUI that might be opened.
                Minecraft.getMinecraft().displayGuiScreen(null);

                // Press down.
                System.out.println("Pressing down on " + vk);
                ROBOT.keyPress(vk);

                KeyResetInfo = new KeyReset(bind, vk, DEFAULT_TICK_RESET, dummyKeyCode, bindKeyCode, hold);
                return;
            }
        }

        error("Error: Could not find key \"" + args[0] + "\"");
    }

    @SubscribeEvent
    public void keyUpdate(TickEvent event) { // TickEvent  /  ClientTickEvent?

        if (event.phase == TickEvent.Phase.START)
            if (KeyResetInfo != null && KeyResetInfo.incrementTick())
            {
                System.out.println("Releasing " + KeyResetInfo.getSystemVK());

                // Release the key, or key up.
                ROBOT.keyRelease(KeyResetInfo.getSystemVK());

                // Reset bindings.
                KeyBindings.dummyKey.setKeyCode(KeyResetInfo.getDummyKeyCode());
                KeyResetInfo.getKeyBinding().setKeyCode(KeyResetInfo.getBindKeyCode());

                // Update the key states.
                if (!KeyResetInfo.shouldHold()) {
                    KeyBinding.setKeyBindState(KeyResetInfo.getBindKeyCode(), false);
                    KeyBinding.setKeyBindState(KeyResetInfo.getDummyKeyCode(), false);
                }

                // Update the client's key bindings.
                KeyBinding.resetKeyBindingArrayAndHash();

                // Done.
                KeyResetInfo = null;
            }
    }

    // Checks if the player can execute the command.
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    // Autocompletes
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> aliases = new ArrayList<>();

        switch (args.length)
        {
            case 1:
                for (KeyBinding bind : Minecraft.getMinecraft().gameSettings.keyBindings)
                    if ((args[0].length() < bind.getKeyDescription().length() && bind.getKeyDescription().substring(0, args[0].length()).equalsIgnoreCase(args[0]))
                            || bind.getKeyDescription().contains(args[0]))
                        aliases.add(bind.getKeyDescription());
                break;

            case 2:
                aliases.add("true");
                aliases.add("false");
        }

        return aliases;
    }

    // IDK
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    private void printUsage() {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(USAGE));
    }

    private void error(String text) {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.RED.toString() + text));
    }
}