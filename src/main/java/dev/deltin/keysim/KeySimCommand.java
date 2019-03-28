package dev.deltin.keysim;

import net.minecraft.client.Minecraft;
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

import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;

public class KeySimCommand implements IClientCommand {

    // Input methods
    public static final String TICK = "ontick";
    public static final String WORLD = "world";
    public static final String SIM = "sim";
    // Press methods
    public static final String TOGGLE = "toggle";
    public static final String PRESS = "press";
    // Command info
    public static final String COMMAND_NAME = "keysim";
    public static final String ALIAS = "/" + COMMAND_NAME;
    public static final String USAGE = TextFormatting.RED + "Usage: /" + COMMAND_NAME + " <key> [" + SIM + "|" + TICK + "|" + WORLD + "] [" + TOGGLE + "|" + PRESS + " (ticks)] (switch [true|false])";
    // Reset info
    private static final List<KeyReset> KeyResets = new ArrayList<>();
    // private static final int TICKS_UNTIL_RESET = 40;
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

        if (args.length < 2) {
            printUsage();
            return;
        }

        String keyArg = args[0];
        String methodArg = args[1];

        String pressArg = PRESS;
        int ticksArg = 40;

        if (args.length >= 3)
        {
            pressArg = args[2];
            if (!pressArg.equals(PRESS) && !pressArg.equals(TOGGLE))
            {
                printUsage();
                return;
            }

            if (args.length >= 4)
            {
                try
                {
                    ticksArg = Integer.parseInt(args[3]);
                    if (ticksArg <= 0)
                        ticksArg = 40;
                }
                catch (NumberFormatException e) {}
            }
        }

        Minecraft mc = Minecraft.getMinecraft();

        for (KeyBinding bind : mc.gameSettings.keyBindings) {
            if (bind.getKeyDescription().equalsIgnoreCase(keyArg)) {
                System.out.println("Keycode: " + bind.getKeyCode());
                switch(methodArg) {

                    case TICK:
                    case WORLD:

                        if (pressArg.equals(PRESS))
                            KeyResets.add(new KeyReset(bind, ticksArg));

                        if (methodArg.equals(TICK)) KeyBinding.onTick(bind.getKeyCode());
                        else if (methodArg.equals(WORLD)) trigger = bind;

                        return;

                    case SIM:
                        if (ROBOT == null)
                        {
                            error("Error: ROBOT failed to initialized.");
                            return;
                        }

                        try {
                            int key = -1;

                            for (Field f : VK_KEYCODES) {
                                if (Modifier.isStatic(f.getModifiers()) && f.getName().equals("VK_" + bind.getDisplayName())) {
                                    key = (int)f.get(null);
                                }
                            }

                            if (key == -1) {
                                error("Error: Invalid key \"" + bind.getDisplayName() + "\" (\"" + "VK_" + bind.getDisplayName() + "\")");
                                return;
                            }

                            ROBOT.keyPress(key);
                            KeyResets.add(new KeyReset(key, ticksArg));
                        }
                        catch (IllegalAccessException e)
                        {
                            error("Error: \"" + e.getMessage() + "\"");
                        }
                        return;

                    default:
                        printUsage();
                        return;
                }
            }
        }

        error("Error: Could not find key \"" + args[0] + "\"");
    }

    @SubscribeEvent
    public void keyUpdate(TickEvent event) { // TickEvent  /  ClientTickEvent?
        if (trigger != null) {
            //KeyBinding bind = FMLClientHandler.instance().getClient().gameSettings.keyBindJump;
            KeyBinding.setKeyBindState(trigger.getKeyCode(), true);
            trigger = null;
        }

        if (event.phase == TickEvent.Phase.START)
            for (int i = KeyResets.size() - 1; i >= 0; i--)
            {
                KeyReset kr = KeyResets.get(i);
                kr.incrementTick();
                if (kr.getCurrentTick() >= kr.getTickCount()) {
                    System.out.println("Released key after " + kr.getCurrentTick() + " ticks.");

                    if (kr.isKeybind())
                        KeyBinding.setKeyBindState(kr.getKeyBinding().getKeyCode(), false);
                    else
                        ROBOT.keyRelease(kr.getKeyCode());

                    KeyResets.remove(kr);
                }
            }
    }
    private KeyBinding trigger = null;

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
                    if (args[0].length() < bind.getKeyDescription().length() && bind.getKeyDescription().substring(0, args[0].length()).equalsIgnoreCase(args[0])
                            || bind.getKeyDescription().contains(args[0]))
                        aliases.add(bind.getKeyDescription());
                break;

            case 2:
                aliases.add(TICK);
                aliases.add(WORLD);
                aliases.add(SIM);
                break;
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