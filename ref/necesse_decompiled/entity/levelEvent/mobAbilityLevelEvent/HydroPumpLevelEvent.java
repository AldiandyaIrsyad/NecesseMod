package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.particle.HydroPumpParticle;
import necesse.entity.particle.Particle;
import necesse.level.gameTile.LiquidTile;
import necesse.level.maps.LevelObjectHit;

public class HydroPumpLevelEvent extends GroundEffectEvent {
   public GameDamage damage;
   protected int tickCounter;
   protected MobHitCooldowns hitCooldowns;

   public HydroPumpLevelEvent() {
   }

   public HydroPumpLevelEvent(Mob var1, int var2, int var3, GameRandom var4, GameDamage var5) {
      super(var1, var2, var3, var4);
      this.damage = var5;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      this.damage.writePacket(var1);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.damage = GameDamage.fromReader(var1);
   }

   public void init() {
      super.init();
      this.tickCounter = 0;
      this.hitCooldowns = new MobHitCooldowns();
      if (this.isClient()) {
         this.level.entityManager.addParticle((Particle)(new HydroPumpParticle(this.level, (float)this.x, (float)this.y, this.getWaterColor(), 500, 4000, 500)), Particle.GType.CRITICAL);
      }

   }

   public Shape getHitBox() {
      if (this.tickCounter >= 10 && this.tickCounter <= 90) {
         byte var1 = 40;
         byte var2 = 30;
         return new Rectangle(this.x - var1 / 2, this.y - var2 / 2, var1, var2);
      } else {
         return null;
      }
   }

   public void clientHit(Mob var1) {
      var1.startHitCooldown();
      this.hitCooldowns.startCooldown(var1);
   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || this.hitCooldowns.canHit(var1)) {
         var1.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this.owner);
         this.hitCooldowns.startCooldown(var1);
      }

   }

   public void hitObject(LevelObjectHit var1) {
      var1.getLevelObject().attackThrough(this.damage, this.owner);
   }

   public boolean canHit(Mob var1) {
      return super.canHit(var1) && this.hitCooldowns.canHit(var1);
   }

   public Color getWaterColor() {
      return ((LiquidTile)TileRegistry.getTile(TileRegistry.waterID)).getLiquidColor(this.level, 0, 0);
   }

   public void clientTick() {
      ++this.tickCounter;
      if (this.tickCounter > 100) {
         this.over();
      } else {
         if (this.tickCounter >= 10 && this.tickCounter <= 90) {
            Color var1 = this.getWaterColor();

            for(int var2 = 0; var2 < 5; ++var2) {
               AtomicReference var3 = new AtomicReference(GameRandom.globalRandom.nextFloat() * 360.0F);
               AtomicReference var4 = new AtomicReference(16.0F);
               this.level.entityManager.addParticle((float)this.x + GameMath.sin((Float)var3.get()) * (Float)var4.get(), (float)this.y + GameMath.cos((Float)var3.get()) * (Float)var4.get() * 0.75F, Particle.GType.CRITICAL).color(var1).heightMoves(0.0F, 80.0F).moves((var3x, var4x, var5, var6, var7) -> {
                  float var8 = (Float)var3.accumulateAndGet(var4x * 150.0F / 250.0F, Float::sum);
                  float var9 = (Float)var4.accumulateAndGet(var4x * 6.0F / 250.0F, Float::sum);
                  float var10 = var9 * 0.75F;
                  var3x.x = (float)this.x + GameMath.sin(var8) * var9;
                  var3x.y = (float)this.y + GameMath.cos(var8) * var10 * 0.75F;
               }).lifeTime(800).sizeFades(12, 20);
            }
         }

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
}
