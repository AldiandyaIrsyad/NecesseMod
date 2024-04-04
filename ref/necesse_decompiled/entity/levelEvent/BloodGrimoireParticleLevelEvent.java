package necesse.entity.levelEvent;

import java.awt.Color;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class BloodGrimoireParticleLevelEvent extends LevelEvent {
   public Mob startMob;
   public Mob endMob;
   public long startTime;
   public int particlesSpawned = 0;
   public int particleCount = 4;
   public int eventLifeTime = 500;
   ParticleTypeSwitcher particleTypeSwitcher;

   public BloodGrimoireParticleLevelEvent() {
      this.particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC});
   }

   public BloodGrimoireParticleLevelEvent(Mob var1, Mob var2) {
      super(true);
      this.particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC});
      this.startMob = var1;
      this.endMob = var2;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.startMob.getUniqueID());
      var1.putNextInt(this.endMob.getUniqueID());
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.startMob = GameUtils.getLevelMob(var1.getNextInt(), this.level);
      this.endMob = GameUtils.getLevelMob(var1.getNextInt(), this.level);
   }

   public void init() {
      super.init();
      if (this.startMob == null || this.endMob == null || this.isServer()) {
         this.over();
      }

      this.startTime = this.getLocalTime();
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      long var2 = this.getLocalTime() - this.startTime;
      if (var2 > (long)this.eventLifeTime) {
         this.over();
      } else {
         float var4 = (float)var2 / (float)this.eventLifeTime;
         float var5 = var4 * (float)this.particleCount;

         while((float)this.particlesSpawned < var5) {
            ++this.particlesSpawned;
            float var6 = this.startMob.x;
            float var7 = this.startMob.y;
            this.level.entityManager.addParticle(var6, var7, this.particleTypeSwitcher.next()).moves((var3, var4x, var5x, var6x, var7x) -> {
               var3.x = GameMath.lerp(var7x, var6, this.endMob.x);
               var3.y = GameMath.lerp(var7x, var7, this.endMob.y);
            }).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).lifeTime(1200).color(new Color(115, 0, 0)).height(16.0F);
         }

      }
   }
}
