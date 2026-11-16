package dev.foreverworld.eternalcoloniescompat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Optional integration bridge: the original mod JARs remain unchanged.
 *
 * Jar inspections performed against Eternal Tool 1.5.0 and MineColonies
 * 1.1.1420-1.21.1 revealed:
 * - EternalToolItem.canPerformAction supports pickaxe/shovel/axe/sword/hoe.
 * - MineColonies detects those ItemAbilities but uses a separate tier map.
 * - Compatibility.registerItemTier(Item, int) is a public MineColonies API.
 *
 * Reflection avoids redistributing third-party binaries or requiring them
 * on this project's Gradle compile classpath.
 */
@Mod(EternalColoniesCompat.MOD_ID)
public final class EternalColoniesCompat {
    public static final String MOD_ID = "eternalcoloniescompat";
    private static final Logger LOG = LoggerFactory.getLogger("EternalColoniesCompat");
    private static final ResourceLocation TOOL_ID = ResourceLocation.fromNamespaceAndPath("eternaltool", "eternal_tool");

    public EternalColoniesCompat(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, CompatConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
    }

    private void onServerStarted(ServerStartedEvent event) {
        Item tool = BuiltInRegistries.ITEM.get(TOOL_ID);
        if (tool == Items.AIR) {
            LOG.error("Eternal Tool item {} was not found; compatibility is inactive", TOOL_ID);
            return;
        }
        int tier = CompatConfig.EQUIPMENT_TIER.get();
        try {
            Class<?> compatibility = Class.forName("com.minecolonies.api.compatibility.Compatibility");
            Method register = compatibility.getMethod("registerItemTier", Item.class, int.class);
            register.invoke(null, tool, tier);

            // Read back the result for a clear diagnostic. This public API
            // recognizes components of the actual item stack, including its
            // Eternal Tool progress components, through MineColonies matching.
            Method getLevel = compatibility.getMethod("getItemLevel", ItemStack.class);
            int observed = (Integer) getLevel.invoke(null, new ItemStack(tool));
            if (observed < 0) {
                LOG.error("MineColonies did not recognize the Eternal Tool tier after registration. Expected {}, got {}.", tier, observed);
            } else {
                LOG.info("Eternal Tool registered with MineColonies: tier {} (read-back {}). Citizens may use it where their job allows swords/pickaxes/axes/shovels/hoes.", tier, observed);
            }
        } catch (ReflectiveOperationException | LinkageError e) {
            LOG.error("Could not register Eternal Tool tier with MineColonies 1.1.1420 API. Neither original mod was modified.", e);
        }
    }
}
