package necesse.level.maps.layers;

import java.util.Arrays;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public abstract class ByteLevelLayer extends LevelLayer {
   protected byte[] data;

   public ByteLevelLayer(Level var1) {
      super(var1);
      this.data = new byte[var1.width * var1.height];
      if (this.getDefault() != 0) {
         Arrays.fill(this.data, this.getDefault());
      }

   }

   protected byte get(int var1, int var2) {
      return var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height ? this.data[this.getDataIndex(var1, var2)] : this.getDefault();
   }

   protected void set(int var1, int var2, byte var3) {
      if (var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height) {
         this.data[this.getDataIndex(var1, var2)] = var3;
      }
   }

   protected int getDataIndex(int var1, int var2) {
      return var1 + var2 * this.level.width;
   }

   protected byte getDefault() {
      return 0;
   }

   public void writeRegionPacket(Region var1, PacketWriter var2) {
      for(int var3 = 0; var3 < var1.regionWidth; ++var3) {
         int var4 = var1.getLevelX(var3);

         for(int var5 = 0; var5 < var1.regionHeight; ++var5) {
            int var6 = var1.getLevelY(var5);
            var2.putNextByte(this.get(var4, var6));
         }
      }

   }

   public boolean readRegionPacket(Region var1, PacketReader var2) {
      for(int var3 = 0; var3 < var1.regionWidth; ++var3) {
         int var4 = var1.getLevelX(var3);

         for(int var5 = 0; var5 < var1.regionHeight; ++var5) {
            int var6 = var1.getLevelY(var5);
            byte var7 = var2.getNextByte();
            if (!this.isValidRegionValue(var4, var6, var7)) {
               return false;
            }

            this.data[this.getDataIndex(var4, var6)] = var7;
         }
      }

      return true;
   }

   public void unloadRegion(Region var1) {
      byte var2 = this.getDefault();

      for(int var3 = 0; var3 < var1.regionWidth; ++var3) {
         int var4 = var1.getLevelX(var3);

         for(int var5 = 0; var5 < var1.regionHeight; ++var5) {
            int var6 = var1.getLevelY(var5);
            this.data[this.getDataIndex(var4, var6)] = var2;
         }
      }

   }

   protected abstract boolean isValidRegionValue(int var1, int var2, byte var3);

   public void addSaveData(SaveData var1) {
      var1.addByteArray(this.getSaveDataName(), this.data);
   }

   public void loadSaveData(LoadData var1) {
      try {
         LoadData var2 = var1.getFirstLoadDataByName(this.getSaveDataName());
         if (var2 == null) {
            this.handleSaveNotFound();
            return;
         }

         byte[] var3 = LoadData.getByteArray(var2);
         if (var3.length != this.data.length) {
            this.data = Arrays.copyOf(var3, this.data.length);
         } else {
            this.data = var3;
         }
      } catch (Exception var4) {
         this.handleLoadException(var4);
      }

   }

   protected String getSaveDataName() {
      return this.getStringID();
   }

   protected void handleSaveNotFound() {
      GameLog.warn.println("Could not find level " + this.getSaveDataName() + " data");
   }

   protected void handleLoadException(Exception var1) {
      System.err.println("Failed to load level " + this.getSaveDataName() + " data resulting in a wipe");
      this.data = new byte[this.level.width * this.level.height];
      if (this.getDefault() != 0) {
         Arrays.fill(this.data, this.getDefault());
      }

   }
}
