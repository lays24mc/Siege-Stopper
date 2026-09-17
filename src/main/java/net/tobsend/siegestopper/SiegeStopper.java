package net.tobsend.siegestopper;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.village.VillageSiegeEvent;
import org.slf4j.Logger;

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
        LOGGER.debug("[SiegeStopper] commonSetup loaded.");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[SiegeStopper] Mod activated, sieges are blocked.");
    }

    public static class SiegeEventHandler {

        // Vanilla fires VillageSiegeEvent repeatedly (once per tick) for the same siege attempt,
        // so we only log once per burst instead of once per tick.
        private static final long LOG_COOLDOWN_MS = 2000L;
        private long lastCancelLogTime = 0L;

        @SubscribeEvent
        public void onSiegeEvent(VillageSiegeEvent event) {
            LOGGER.debug("[SiegeStopper] Receive VillageSiegeEvent: {}", event);

            event.setCanceled(true);

            long now = System.currentTimeMillis();
            if (now - lastCancelLogTime > LOG_COOLDOWN_MS) {
                LOGGER.info("[SiegeStopper] Siege canceled!");
            }
            lastCancelLogTime = now;
        }
    }
}
