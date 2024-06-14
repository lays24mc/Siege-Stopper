package net.tobsend.siegestopper;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.entity.ai.village.VillageSiege;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.village.VillageSiegeEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SiegeStopper.MODID)
public class SiegeStopper {

  // Define mod id in a common place for everything to reference
  public static final String MODID = "siegestopper";
  // Directly reference a slf4j logger
  private static final Logger LOGGER = LogUtils.getLogger();

  public SiegeStopper(IEventBus modEventBus) {
    // Register the commonSetup method for modloading
    modEventBus.addListener(this::commonSetup);

    // Register ourselves for server and other game events we are interested in
    NeoForge.EVENT_BUS.register(this);
    NeoForge.EVENT_BUS.register(SiegeEventHandler.class);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {}

  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    LOGGER.info("Siege stopper enabled.");
  }

  @EventBusSubscriber(modid = MODID, value = Dist.DEDICATED_SERVER)
  public class SiegeEventHandler {

    @SubscribeEvent
    public static void onSiegeEvent(VillageSiegeEvent event) {
      LOGGER.info("Siege was detected.");
      event.getSiege().siegeState = VillageSiege.State.SIEGE_DONE;
      event.setCanceled(true);
      LOGGER.info("Siege stopped.");
    }
  }
}
