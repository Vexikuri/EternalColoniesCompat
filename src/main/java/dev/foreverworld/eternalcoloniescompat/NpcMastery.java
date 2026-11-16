package dev.foreverworld.eternalcoloniescompat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Method;

/** Uses the real Eternal Tool data-component API and XP curve; never invents new mastery state. */
public final class NpcMastery {
    private static final ResourceLocation ETERNAL_TOOL = ResourceLocation.fromNamespaceAndPath("eternaltool", "eternal_tool");
    private static volatile Method progress, addXp, saveProgress, level, xp;
    private static volatile boolean lookupAttempted;

    private NpcMastery() { }

    public static boolean isEternal(ItemStack stack) {
        return stack != null && !stack.isEmpty() && ETERNAL_TOOL.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public static void credit(LivingEntity worker, int points, String activity) {
        if (points <= 0 || !(worker.level() instanceof ServerLevel)) return;
        ItemStack stack = worker.getMainHandItem();
        if (!isEternal(stack)) return;
        try {
            if (!resolve()) return;
            Object before = progress.invoke(null, stack);
            Object after = addXp.invoke(before, points);
            int oldLevel = (Integer) level.invoke(before);
            int newLevel = (Integer) level.invoke(after);
            saveProgress.invoke(null, stack, after);
            if (newLevel > oldLevel) {
                EternalColoniesCompat.LOG.info("MineColonies citizen {} raised Eternal Tool mastery {} -> {} after {}", worker.getName().getString(), oldLevel, newLevel, activity);
            } else if (CompatConfig.LOG_EACH_AWARD.get()) {
                EternalColoniesCompat.LOG.info("Citizen {} awarded {} Eternal Tool XP for {} (level {}, XP {})", worker.getName().getString(), points, activity, newLevel, xp.invoke(after));
            }
        } catch (ReflectiveOperationException | LinkageError error) {
            EternalColoniesCompat.LOG.error("Could not credit Eternal Tool NPC mastery using Eternal Tool 1.5.0 API", error);
        }
    }

    private static boolean resolve() {
        if (!lookupAttempted) {
            synchronized (NpcMastery.class) {
                if (!lookupAttempted) {
                    try {
                        Class<?> tool = Class.forName("dev.foreverworld.eternaltool.EternalToolItem");
                        Class<?> record = Class.forName("dev.foreverworld.eternaltool.ToolProgress");
                        progress = tool.getMethod("progress", ItemStack.class);
                        addXp = record.getMethod("addXp", int.class);
                        saveProgress = tool.getMethod("saveProgress", ItemStack.class, record);
                        level = record.getMethod("level");
                        xp = record.getMethod("xp");
                    } catch (ReflectiveOperationException | LinkageError e) {
                        EternalColoniesCompat.LOG.error("Eternal Tool XP API not found; NPC XP disabled", e);
                    } finally {
                        lookupAttempted = true;
                    }
                }
            }
        }
        return progress != null && addXp != null && saveProgress != null && level != null;
    }

    public static int xpFor(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String path = id.getPath();
        if (path.endsWith("_ore") || path.equals("ancient_debris") || path.contains("ore_")) return CompatConfig.ORE_XP.get();
        if (state.is(BlockTags.LOGS)) return CompatConfig.LOG_XP.get();
        return CompatConfig.BLOCK_XP.get();
    }
}
