package necesse.engine.util;

import necesse.engine.playerStats.PlayerStats;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class WorldDeathLocation {
   public final int deathTime;
   public final LevelIdentifier levelIdentifier;
   public final int x;
   public final int y;

   public WorldDeathLocation(PlayerStats var1, LevelIdentifier var2, int var3, int var4) {
      this.deathTime = var1.time_played.get();
      this.levelIdentifier = var2;
      this.x = var3;
      this.y = var4;
   }

   public int getSecondsSince(PlayerStats var1) {
      return var1.time_played.get() - this.deathTime;
   }

   public WorldDeathLocation(LoadData var1) {
      this.deathTime = var1.getInt("time");

      LevelIdentifier var2;
      try {
         var2 = new LevelIdentifier(var1.getUnsafeString("level", (String)null, false));
      } catch (InvalidLevelIdentifierException var7) {
         int var4 = var1.getInt("islandX");
         int var5 = var1.getInt("islandY");
         int var6 = var1.getInt("dimension");
         var2 = new LevelIdentifier(var4, var5, var6);
      }

      this.levelIdentifier = var2;
      this.x = var1.getInt("x");
      this.y = var1.getInt("y");
   }

   public void addSaveData(SaveData var1) {
      var1.addInt("time", this.deathTime);
      var1.addUnsafeString("level", this.levelIdentifier.stringID);
      var1.addInt("x", this.x);
      var1.addInt("y", this.y);
   }
}
