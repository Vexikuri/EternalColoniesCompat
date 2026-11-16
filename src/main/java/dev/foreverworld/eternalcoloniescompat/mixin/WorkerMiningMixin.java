package dev.foreverworld.eternalcoloniescompat.mixin;

import dev.foreverworld.eternalcoloniescompat.CompatConfig;
import dev.foreverworld.eternalcoloniescompat.NpcMastery;
import dev.foreverworld.eternalcoloniescompat.WorkerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/**
 * Hook a MineColonies *completed* mineBlock action, not a generic player break event.
 * Verified against MineColonies 1.1.1420-1.21.1 bytecode; do not claim all job AIs use this method.
 */
@Mixin(targets = "com.minecolonies.core.entity.ai.workers.AbstractEntityAIInteract", remap = false)
public abstract class WorkerMiningMixin {
    @Unique private BlockState eternalcoloniescompat$prior;
    @Inject(method = "mineBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;ZZLjava/lang/Runnable;)Z", at = @At("HEAD"), remap = false)
    private void eternalcoloniescompat$before(BlockPos target, BlockPos from, boolean collectDrops, boolean useTool,
            Runnable callback, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity citizen = WorkerAccess.worker(this);
        BlockState prior = citizen != null && citizen.level() instanceof ServerLevel server && server.hasChunkAt(target)
            ? citizen.level().getBlockState(target) : null;
        eternalcoloniescompat$prior = prior;
    }
    @Inject(method = "mineBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;ZZLjava/lang/Runnable;)Z", at = @At("RETURN"), remap = false)
    private void eternalcoloniescompat$after(BlockPos target, BlockPos from, boolean collectDrops, boolean useTool,
            Runnable callback, CallbackInfoReturnable<Boolean> cir) {
        BlockState prior = eternalcoloniescompat$prior;
        eternalcoloniescompat$prior = null;
        if (!CompatConfig.ENABLE_MINING_XP.get() || !cir.getReturnValueZ() || prior == null || prior.isAir()) return;
        LivingEntity citizen = WorkerAccess.worker(this);
        if (citizen == null || !(citizen.level() instanceof ServerLevel server) || !server.hasChunkAt(target)) return;
        // A successful state change is required; simply swinging the tool never awards XP.
        BlockState now = server.getBlockState(target);
        if (now.equals(prior)) return;
        NpcMastery.credit(citizen, NpcMastery.xpFor(prior), "completed mining");
    }
}
