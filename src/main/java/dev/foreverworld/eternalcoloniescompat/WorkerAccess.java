package dev.foreverworld.eternalcoloniescompat;

import net.minecraft.world.entity.LivingEntity;
import java.lang.reflect.Field;

/** Extract the citizen reference from MineColonies AI without bundling its classes. */
public final class WorkerAccess {
    private static volatile Field workerField;
    private static volatile boolean attempted;
    private WorkerAccess() { }

    public static LivingEntity worker(Object ai) {
        try {
            if (!attempted) {
                synchronized (WorkerAccess.class) {
                    if (!attempted) {
                        Class<?> skeleton = Class.forName("com.minecolonies.core.entity.ai.workers.AbstractAISkeleton");
                        Field f = skeleton.getDeclaredField("worker");
                        f.setAccessible(true);
                        workerField = f;
                        attempted = true;
                    }
                }
            }
            Object result = workerField == null ? null : workerField.get(ai);
            return result instanceof LivingEntity living ? living : null;
        } catch (ReflectiveOperationException | LinkageError | SecurityException error) {
            EternalColoniesCompat.LOG.warn("MineColonies worker reference unavailable: {}", error.toString());
            return null;
        }
    }
}
