package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public class ExplosiveModifierChargeUpLevelEvent extends LevelEvent {
   long startTime;
   float chargeUpDuration;
   int x;
   int y;
   boolean mainParticleSpawned;

   public ExplosiveModifierChargeUpLevelEvent() {
   }

   public ExplosiveModifierChargeUpLevelEvent(int var1, int var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.chargeUpDuration = var3;
   }

   public void init() {
      super.init();
      this.startTime = this.level.getTime();
      this.mainParticleSpawned = false;
      if (this.isServer()) {
         this.over();
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.chargeUpDuration);
      var1.putNextLong(this.startTime);
      var1.putNextInt(this.x);
      var1.putNextInt(this.y);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.chargeUpDuration = var1.getNextFloat();
      this.startTime = var1.getNextLong();
      this.x = var1.getNextInt();
      this.y = var1.getNextInt();
   }

   public void clientTick() {
      super.clientTick();
      if ((float)this.level.getTime() < (float)this.startTime + this.chargeUpDuration) {
         if (!this.mainParticleSpawned) {
            float var1 = 25.0F;
            float var10001 = (float)this.x;
            float var10002 = (float)this.y;
            this.getLevel().entityManager.addParticle(var10001, var10002, Particle.GType.CRITICAL).sprite(GameResources.explosiveModifierChargeUp.sprite(0, 0, 32)).rotation(new ParticleOption.FloatGetter() {
               public float get(int var1, int var2, float var3) {
                  return (float)var2 * var3 + 0.25F;
               }
            }).givesLight(75.0F, 0.5F).fadesAlphaTime(1500, 250).lifeTime(1750).height(var1).size(new ParticleOption.DrawModifier() {
               public void modify(SharedTextureDrawOptions.Wrapper var1, int var2, int var3, float var4) {
                  var1.size(32, 32);
               }
            });
            this.mainParticleSpawned = true;
         }

         for(int var15 = 0; var15 < 3; ++var15) {
            int var2 = GameRandom.globalRandom.nextInt(360);
            Point2D.Float var3 = GameMath.getAngleDir((float)var2);
            float var4 = GameRandom.globalRandom.getFloatBetween(25.0F, 40.0F);
            float var5 = (float)this.x + var3.x * var4;
            float var6 = (float)(this.y + 4);
            float var7 = 29.0F;
            float var8 = var7 + var3.y * var4;
            int var9 = GameRandom.globalRandom.getIntBetween(200, 500);
            float var10 = var3.x * var4 * 250.0F / (float)var9;
            Color var11 = new Color(156, 51, 39);
            Color var12 = new Color(191, 90, 62);
            Color var13 = new Color(233, 134, 39);
            Color var14 = (Color)GameRandom.globalRandom.getOneOf((Object[])(var11, var12, var13));
            this.getLevel().entityManager.addParticle(var5, var6, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().givesLight(75.0F, 0.5F).heightMoves(var8, var7).movesConstant(-var10, 0.0F).color(var14).fadesAlphaTime(100, 50).lifeTime(var9);
         }
      } else {
         this.over();
      }

   }
}
