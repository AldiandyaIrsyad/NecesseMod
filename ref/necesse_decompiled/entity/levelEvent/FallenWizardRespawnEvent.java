package necesse.entity.levelEvent;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.hostile.bosses.FallenWizardMob;
import necesse.entity.particle.Particle;

public class FallenWizardRespawnEvent extends LevelEvent {
   public float posX;
   public float posY;
   public long spawnWorldTime;
   public double particleBuffer;

   public FallenWizardRespawnEvent() {
      super(true);
      this.shouldSave = true;
   }

   public FallenWizardRespawnEvent(float var1, float var2, long var3) {
      this();
      this.posX = var1;
      this.posY = var2;
      this.spawnWorldTime = var3;
   }

   public boolean isNetworkImportant() {
      return true;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addFloat("posX", this.posX);
      var1.addFloat("posY", this.posY);
      var1.addLong("spawnWorldTime", this.spawnWorldTime);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.posX = var1.getFloat("posX", Float.NEGATIVE_INFINITY);
      this.posY = var1.getFloat("posY", Float.NEGATIVE_INFINITY);
      this.spawnWorldTime = var1.getLong("spawnWorldTime", -1L);
      if (this.posX == Float.NEGATIVE_INFINITY || this.posY == Float.NEGATIVE_INFINITY || this.spawnWorldTime == -1L) {
         this.over();
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.posX);
      var1.putNextFloat(this.posY);
      var1.putNextLong(this.spawnWorldTime);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.posX = var1.getNextFloat();
      this.posY = var1.getNextFloat();
      this.spawnWorldTime = var1.getNextLong();
   }

   public void clientTick() {
      super.clientTick();
      if (this.spawnWorldTime <= this.level.getWorldEntity().getWorldTime()) {
         this.over();
      } else {
         long var1 = this.spawnWorldTime - this.level.getWorldEntity().getWorldTime();
         long var3 = 120000L;
         double var5 = 20.0;
         double var7 = 0.20000000298023224;
         if (var1 < var3) {
            double var9 = (double)var1 / (double)var3;
            double var11 = Math.abs(var9 - 1.0);
            var11 = Math.pow(var11, 2.0);
            this.particleBuffer += 0.05 * (var7 + (var5 - var7) * var11);
         } else {
            this.particleBuffer += 0.05 * var7;
         }

         while(this.particleBuffer > 0.0 && GameRandom.globalRandom.getChance(this.particleBuffer)) {
            --this.particleBuffer;
            int var14 = GameRandom.globalRandom.getIntBetween(500, 1000);
            float var10 = (float)var14 / 1000.0F;
            float var13 = 0.0F;
            float var12 = var13 + (float)GameRandom.globalRandom.getIntBetween(40, 50) * var10;
            this.level.entityManager.addParticle(this.posX + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), this.posY + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), Particle.GType.IMPORTANT_COSMETIC).sizeFades(10, 16).movesFriction(GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), 0.5F).heightMoves(var13, var12).givesLight(260.0F, 0.5F).colorRandom(270.0F, 0.8F, 0.5F, 10.0F, 0.1F, 0.1F).lifeTime(var14);
         }

      }
   }

   public void serverTick() {
      super.serverTick();
      if (this.level.tickManager().getTick() == 1 && this.level.entityManager.mobs.stream().anyMatch((var0) -> {
         return var0 instanceof FallenWizardMob;
      })) {
         this.over();
      } else {
         if (this.spawnWorldTime <= this.level.getWorldEntity().getWorldTime()) {
            if (this.level.entityManager.mobs.stream().noneMatch((var0) -> {
               return var0 instanceof FallenWizardMob;
            })) {
               FallenWizardMob var1 = (FallenWizardMob)MobRegistry.getMob("fallenwizard", this.level);
               var1.spawnParticles = true;
               this.level.entityManager.addMob(var1, this.posX, this.posY);
            }

            this.over();
         }

      }
   }
}
