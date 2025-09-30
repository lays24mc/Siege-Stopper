package net.tobsend.siegestopper;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.village.VillageSiegeEvent;
import org.slf4j.Logger;

import java.lang.reflect.Field;

@Mod(SiegeStopper.MODID)
public class SiegeStopper {

    public static final String MODID = "siegestopper";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SiegeStopper(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);

        // IMPORTANT: Register the handler manually (this prevents distribution issues)
        NeoForge.EVENT_BUS.register(new SiegeEventHandler());
        LOGGER.info("[SiegeStopper] SiegeEventHandler registered on EVENT_BUS");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("[SiegeStopper] commonSetup loaded.");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[SiegeStopper] Mod activated, sieges are blocked.");
    }

    public static class SiegeEventHandler {

        @SubscribeEvent
        public void onSiegeEvent(VillageSiegeEvent event) {
            LOGGER.info("[SiegeStopper] Receive VillageSiegeEvent: {}", event);

            Object siege = event.getSiege();
            try {
                // Search for field 'siegeState'
                Field stateField = siege.getClass().getDeclaredField("siegeState");
                stateField.setAccessible(true);

                // Load enum class
                Class<?> stateEnum = Class.forName("net.minecraft.world.entity.ai.village.VillageSiege$State");
                Object siegeDone = stateEnum.getField("SIEGE_DONE").get(null);

                // Overwrite state
                stateField.set(siege, siegeDone);
                LOGGER.info("[SiegeStopper] siegeState successfully set to SIEGE_DONE");
            } catch (Exception e) {
                LOGGER.error("[SiegeStopper] Error setting siegeState:", e);

                // Debug: Log all fields
                for (Field f : siege.getClass().getDeclaredFields()) {
                    LOGGER.error("[SiegeStopper] Field found: {} ({})", f.getName(), f.getType());
                }
            }

            // Cancel event
            event.setCanceled(true);
            LOGGER.info("[SiegeStopper] Siege canceled!");
        }
    }
}
