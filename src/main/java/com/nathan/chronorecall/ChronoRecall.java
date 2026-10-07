package com.nathan.chronorecall;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.Optional;

@Mod("chrono_recall")
public class ChronoRecall {

    public static final String MODID = "chrono_recall";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public ChronoRecall() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(PlayerStateTracker.class);
        MinecraftForge.EVENT_BUS.register(RecallHandler.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Chrono Recall initializing...");

        CHANNEL.registerMessage(0, RecallPacket.class,
                RecallPacket::encode,
                RecallPacket::decode,
                RecallPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        KeyBindings.register();
        MinecraftForge.EVENT_BUS.register(KeyBindings.class);
        LOGGER.info("Chrono Recall keybindings registered.");
    }
}
