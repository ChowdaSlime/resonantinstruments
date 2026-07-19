package net.chowdaslime.resonantinstruments.event;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = ResonantInstruments.MODID)
public class DivinationGuideTracker {

    private static final DustParticleOptions GUIDE_PARTICLE =
            new DustParticleOptions(0xB266FF, 1.4F);

    private static final List<GuideParticle> ACTIVE = new ArrayList<>();

    public static void spawn(ServerLevel level, Vec3 origin, Vec3 direction, int lifetimeTicks, double speed) {
        ACTIVE.add(new GuideParticle(level, origin, direction.normalize().scale(speed), lifetimeTicks));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (ACTIVE.isEmpty()) return;

        Iterator<GuideParticle> it = ACTIVE.iterator();
        while (it.hasNext()) {
            GuideParticle guide = it.next();

            guide.pos = guide.pos.add(guide.velocity);
            guide.ticksRemaining--;

            guide.level.sendParticles(GUIDE_PARTICLE,
                    guide.pos.x, guide.pos.y, guide.pos.z,
                    3, 0.05, 0.05, 0.05, 0.0);

            if (guide.ticksRemaining <= 0) {
                it.remove();
            }
        }
    }

    private static class GuideParticle {
        final ServerLevel level;
        Vec3 pos;
        final Vec3 velocity;
        int ticksRemaining;

        GuideParticle(ServerLevel level, Vec3 pos, Vec3 velocity, int ticksRemaining) {
            this.level = level;
            this.pos = pos;
            this.velocity = velocity;
            this.ticksRemaining = ticksRemaining;
        }
    }
}