package dev.deltin.keysim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.awt.event.InputEvent;

@Mod(modid = KeySim.MODID, name = KeySim.NAME, version = KeySim.VERSION)
public class KeySim
{
    public static final String MODID = "dev.deltin.keysim";
    public static final String NAME = "Key Sim";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // Register the command
        KeySimCommand sim = new KeySimCommand();
        ClientCommandHandler.instance.registerCommand(sim);
        MinecraftForge.EVENT_BUS.register(sim);

        ClientCommandHandler.instance.registerCommand(new ListKeysCommand());

        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        KeyBindings.init();


    }
}
