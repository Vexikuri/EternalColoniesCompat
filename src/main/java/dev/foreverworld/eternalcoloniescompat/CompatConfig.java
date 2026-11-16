package dev.foreverworld.eternalcoloniescompat;

import net.neoforged.neoforge.common.ModConfigSpec;

/** A fixed MineColonies tool tier, independent from player-owned Eternal Tool mastery. */
public final class CompatConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue EQUIPMENT_TIER = BUILDER
        .comment("MineColonies minimum equipment tier for Eternal Tool (0=wood, 1=stone, 2=iron, 3=diamond, 4=enchanted, 5=highest).",
                 "This is an eligibility tier only; it does not change Eternal Tool damage or mastery.",
                 "Default 1 means early workers and guards can equip the tool once stone-tier gear is allowed.")
        .defineInRange("equipmentTier", 1, 0, 5);
    public static final ModConfigSpec.BooleanValue ENABLE_MINING_XP = BUILDER
        .comment("Award mastery XP for confirmed successful MineColonies AI mining (includes some builders/lumberjacks).")
        .define("enableMiningXp", true);
    public static final ModConfigSpec.IntValue BLOCK_XP = BUILDER.defineInRange("xpPerMinedBlock", 2, 0, 1000);
    public static final ModConfigSpec.IntValue LOG_XP = BUILDER.defineInRange("xpPerLog", 2, 0, 1000);
    public static final ModConfigSpec.IntValue ORE_XP = BUILDER.defineInRange("xpPerOre", 8, 0, 1000);
    public static final ModConfigSpec.BooleanValue ENABLE_KILL_XP = BUILDER.define("enableKillXp", true);
    public static final ModConfigSpec.IntValue KILL_XP = BUILDER.defineInRange("xpPerHostileMobKill", 10, 0, 1000);
    public static final ModConfigSpec.BooleanValue LOG_EACH_AWARD = BUILDER
        .comment("Log every NPC tool XP award for testing (can produce lots of log messages).")
        .define("logEachAward", false);
    public static final ModConfigSpec SPEC = BUILDER.build();
    private CompatConfig() { }
}
