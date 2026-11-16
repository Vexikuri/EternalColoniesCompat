package dev.foreverworld.eternalcoloniescompat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/** Guard combat XP only for actual hostile mob deaths credited to a citizen holding the tool. */
public final class CitizenCombatXp {
    private CitizenCombatXp() { }
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!CompatConfig.ENABLE_KILL_XP.get() || !(event.getEntity() instanceof Monster)) return;
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity citizen)) return;
        // Is a real MineColonies citizen, not a player or another NPC with a similar name.
        if (!citizen.getClass().getName().startsWith("com.minecolonies.")) return;
        if (!NpcMastery.isEternal(citizen.getMainHandItem())) return;
        NpcMastery.credit(citizen, CompatConfig.KILL_XP.get(), "hostile mob defeat");
    }
}
