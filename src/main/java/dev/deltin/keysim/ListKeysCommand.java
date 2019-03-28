package dev.deltin.keysim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ArrayList;

import java.util.Set;

public class ListKeysCommand implements IClientCommand {

    public static final String COMMAND_NAME = "listkeys";
    public static final String USAGE = "/" + COMMAND_NAME;

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
        aliases.add(USAGE);
        return aliases;
    }

    // On Command Execute
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        Minecraft mc = Minecraft.getMinecraft();

        Set<String> binds = KeyBinding.getKeybinds();
        for (KeyBinding bind : mc.gameSettings.keyBindings)
        {
            mc.player.sendMessage(new TextComponentString(bind.getKeyDescription()));
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
        return null;
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
}