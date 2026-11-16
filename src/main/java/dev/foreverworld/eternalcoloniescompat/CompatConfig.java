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
    public static final ModConfigSpec SPEC = BUILDER.build();
    private CompatConfig() { }
}
