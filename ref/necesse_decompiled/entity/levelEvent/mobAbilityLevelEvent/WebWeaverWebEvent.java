package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.particle.Particle;
import necesse.entity.particle.WebWeaverWebParticle;
import necesse.level.maps.LevelObjectHit;

public class WebWeaverWebEvent extends GroundEffectEvent {
   private long startDelay = 1000L;
   private MobHitCooldowns hitCooldowns;
   private WebWeaverWebParticle particle;
   private GameDamage damage;
   private float resilienceGain;
   private int tickCounter;
   private long startTime;

   public WebWeaverWebEvent() {
   }

   public WebWeaverWebEvent(Mob var1, int var2, int var3, GameRandom var4, GameDamage var5, float var6, long var7) {
      super(var1, var2, var3, var4);
      this.damage = var5;
      this.resilienceGain = var6;
      this.startDelay = var7;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextLong(this.startDelay);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.startDelay = var1.getNextLong();
   }

   public void init() {
      super.init();
      this.hitCooldowns = new MobHitCooldowns();
      this.startTime = this.level.getWorldEntity().getTime();
      if (this.isClient()) {
         this.level.entityManager.addParticle(this.particle = new WebWeaverWebParticle(this.level, (float)this.x, (float)this.y, this.startDelay + 5000L, this.startDelay), true, Particle.GType.CRITICAL);
      }

   }

   public Shape getHitBox() {
      short var1 = 180;
      short var2 = 136;
      return new Rectangle(this.x - var1 / 2, this.y - var2 / 2, var1, var2);
   }

   public void clientHit(Mob var1) {
      var1.startHitCooldown();
      this.hitCooldowns.startCooldown(var1);
   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || this.hitCooldowns.canHit(var1)) {
         var1.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this.owner);
         this.hitCooldowns.startCooldown(var1);
         if (var1.canGiveResilience(this.owner) && this.resilienceGain != 0.0F) {
            this.owner.addResilience(this.resilienceGain);
            this.resilienceGain = 0.0F;
         }
      }

   }

   public void clientTick() {
      ++this.tickCounter;
      if (this.tickCounter > 100) {
         this.over();
      } else {
         super.clientTick();
      }

   }

   public void serverTick() {
      ++this.tickCounter;
      if (this.tickCounter > 100) {
         this.over();
      } else {
         super.serverTick();
      }

   }

   public void hitObject(LevelObjectHit var1) {
      var1.getLevelObject().attackThrough(this.damage, this.owner);
   }

   public boolean canHit(Mob var1) {
      if (!this.canDamageAnythingYet()) {
         return false;
      } else {
         return super.canHit(var1) && this.hitCooldowns.canHit(var1);
      }
   }

   public boolean canHit(LevelObjectHit var1) {
      return !this.canDamageAnythingYet() ? false : super.canHit(var1);
   }

   public boolean canDamageAnythingYet() {
      return this.level.getWorldEntity().getTime() >= this.startTime + this.startDelay - 200L;
   }

   public void over() {
      super.over();
      if (this.particle != null) {
         this.particle.despawnNow();
      }

   }
}
