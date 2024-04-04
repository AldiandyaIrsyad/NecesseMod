package necesse.level.maps.layers;

import necesse.engine.Settings;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLevelLayerData;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class RainingLevelLayer extends LevelLayer {
   public static final long rainFadeTime = 10000L;
   private boolean isRaining;
   private long rainTimer;
   private long rainSet;

   public RainingLevelLayer(Level var1) {
      super(var1);
   }

   public void init() {
   }

   public void onLoadingComplete() {
      if (!this.level.isClient()) {
         this.tickRainTimer();
      }

      this.rainSet = this.level.getWorldEntity().getWorldTime() - 10000L;
   }

   public void serverTick() {
      super.serverTick();
      this.tickRainTimer();
   }

   public float getAverageRainingPercent() {
      return this.level.biome.getAverageRainingPercent(this.level);
   }

   protected void tickRainTimer() {
      if (!this.level.biome.canRain(this.level)) {
         this.isRaining = false;
      } else {
         if (this.rainTimer == 0L) {
            this.resetRainTimer();
         }

         long var1 = this.level.getWorldEntity().getWorldTime();
         boolean var3 = this.isRaining;
         int var4 = 0;

         while(true) {
            if (var4 >= 10) {
               int var5 = this.level.biome.getRainTimeInSeconds(this.level, GameRandom.globalRandom);
               int var6 = this.level.biome.getDryTimeInSeconds(this.level, GameRandom.globalRandom);
               int var7 = GameRandom.globalRandom.nextInt(var5 + var6);
               if (var7 < var5) {
                  this.setRaining(true, true, false);
                  this.resetRainTimer(var1 - (long)(var7 * 1000));
               } else {
                  this.setRaining(false, true, false);
                  this.resetRainTimer(var1 - (long)((var7 - var5) * 1000));
               }
               break;
            }

            if (this.rainTimer > var1) {
               break;
            }

            this.setRaining(!this.isRaining, true, false);
            this.resetRainTimer(this.rainTimer);
            ++var4;
         }

         if (this.level.isServer() && this.isRaining != var3) {
            this.level.getServer().network.sendToClientsAt(new PacketLevelLayerData(this), (Level)this.level);
         }

      }
   }

   public void resetRainTimer() {
      this.resetRainTimer(this.level.getWorldEntity().getWorldTime());
   }

   protected void resetRainTimer(long var1) {
      if (this.isRaining) {
         this.rainTimer = var1 + (long)(this.level.biome.getRainTimeInSeconds(this.level, GameRandom.globalRandom) * 1000);
      } else {
         this.rainTimer = var1 + (long)(this.level.biome.getDryTimeInSeconds(this.level, GameRandom.globalRandom) * 1000);
      }

   }

   public void setRaining(boolean var1) {
      this.setRaining(var1, true, true);
   }

   private void setRaining(boolean var1, boolean var2, boolean var3) {
      boolean var4 = this.isRaining;
      this.isRaining = var1;
      this.rainSet = this.level.getWorldEntity().getWorldTime() - (var2 ? 0L : 10000L);
      if (var3 && var4 != var1 && this.level.isServer()) {
         this.level.getServer().network.sendToClientsAt(new PacketLevelLayerData(this), (Level)this.level);
      }

   }

   public boolean isRaining() {
      return this.isRaining;
   }

   public long getRemainingRainTime() {
      return this.rainTimer - this.level.getWorldEntity().getWorldTime();
   }

   public float getRainAlpha() {
      if (Settings.alwaysRain) {
         return 1.0F;
      } else if (!this.level.biome.canRain(this.level)) {
         return 0.0F;
      } else {
         long var1 = this.level.getWorldEntity().getWorldTime() - this.rainSet;
         if (var1 >= 0L && var1 < 10000L) {
            float var3 = (float)var1 / 10000.0F;
            if (!this.isRaining()) {
               var3 = Math.abs(var3 - 1.0F);
            }

            return var3;
         } else {
            return this.isRaining() ? 1.0F : 0.0F;
         }
      }
   }

   public void writeLevelDataPacket(PacketWriter var1) {
      super.writeLevelDataPacket(var1);
      var1.putNextBoolean(this.isRaining);
   }

   public void readLevelDataPacket(PacketReader var1) {
      super.readLevelDataPacket(var1);
      this.setRaining(var1.getNextBoolean(), true, false);
   }

   public void writeRegionPacket(Region var1, PacketWriter var2) {
   }

   public boolean readRegionPacket(Region var1, PacketReader var2) {
      return true;
   }

   public void unloadRegion(Region var1) {
   }

   public void addSaveData(SaveData var1) {
      var1.addBoolean("isRaining", this.isRaining);
      var1.addLong("rainTimer", this.rainTimer);
   }

   public void loadSaveData(LoadData var1) {
      this.isRaining = var1.getBoolean("isRaining", false, false);
      this.rainTimer = var1.getLong("rainTimer", 0L, false);
   }
}
