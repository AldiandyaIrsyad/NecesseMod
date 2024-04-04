package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.hostile.GiantCaveSpiderMob;
import necesse.entity.particle.CaveSpiderSpitParticle;
import necesse.entity.particle.Particle;
import necesse.level.maps.LevelObjectHit;

public class CaveSpiderSpitEvent extends GroundEffectEvent {
   public GiantCaveSpiderMob.Variant variant;
   public GameDamage damage;
   protected int tickCounter;
   protected int hitsRemaining;
   protected MobHitCooldowns hitCooldowns;
   protected CaveSpiderSpitParticle particle;

   public CaveSpiderSpitEvent() {
   }

   public CaveSpiderSpitEvent(Mob var1, int var2, int var3, GameRandom var4, GiantCaveSpiderMob.Variant var5, GameDamage var6, int var7) {
      super(var1, var2, var3, var4);
      this.variant = var5;
      this.damage = var6;
      this.hitsRemaining = Math.min(var7, 65535);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextShortUnsigned(this.variant.ordinal());
      this.damage.writePacket(var1);
      var1.putNextShortUnsigned(this.hitsRemaining);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.variant = GiantCaveSpiderMob.Variant.values()[var1.getNextShortUnsigned()];
      this.damage = GameDamage.fromReader(var1);
      this.hitsRemaining = var1.getNextShortUnsigned();
   }

   public void init() {
      super.init();
      this.tickCounter = 0;
      this.hitCooldowns = new MobHitCooldowns();
      if (this.isClient()) {
         this.level.entityManager.addParticle(this.particle = new CaveSpiderSpitParticle(this.level, (float)this.x, (float)this.y, 5000L, this.variant), true, Particle.GType.CRITICAL);
      }

   }

   public Shape getHitBox() {
      byte var1 = 40;
      byte var2 = 30;
      return new Rectangle(this.x - var1 / 2, this.y - var2 / 2, var1, var2);
   }

   public void clientHit(Mob var1) {
      var1.startHitCooldown();
      this.hitCooldowns.startCooldown(var1);
      --this.hitsRemaining;
      if (this.hitsRemaining <= 0) {
         this.over();
      }

   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || this.hitCooldowns.canHit(var1)) {
         var1.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this.owner);
         this.hitCooldowns.startCooldown(var1);
         --this.hitsRemaining;
         if (this.hitsRemaining <= 0) {
            this.over();
         }
      }

   }

   public void hitObject(LevelObjectHit var1) {
      var1.getLevelObject().attackThrough(this.damage, this.owner);
   }

   public boolean canHit(Mob var1) {
      return super.canHit(var1) && this.hitCooldowns.canHit(var1);
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

   public void over() {
      super.over();
      if (this.particle != null) {
         this.particle.despawnNow();
      }

   }
}
