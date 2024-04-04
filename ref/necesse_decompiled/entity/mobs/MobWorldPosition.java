package necesse.entity.mobs;

import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.Level;

public class MobWorldPosition {
   public final LevelIdentifier levelIdentifier;
   public final int mobUniqueID;

   public MobWorldPosition(Mob var1) {
      Level var2 = var1.getLevel();
      this.levelIdentifier = var2.getIdentifier();
      this.mobUniqueID = var1.getUniqueID();
   }

   public MobWorldPosition(LevelIdentifier var1, int var2) {
      this.levelIdentifier = var1;
      this.mobUniqueID = var2;
   }

   public MobWorldPosition(LoadData var1) {
      LevelIdentifier var2;
      try {
         var2 = new LevelIdentifier(var1.getUnsafeString("level", (String)null));
      } catch (InvalidLevelIdentifierException var7) {
         int var4 = var1.getInt("islandX");
         int var5 = var1.getInt("islandY");
         int var6 = var1.getInt("dimension");
         var2 = new LevelIdentifier(var4, var5, var6);
      }

      this.levelIdentifier = var2;
      this.mobUniqueID = var1.getInt("mobUniqueID");
   }

   public SaveData getSaveData(String var1) {
      SaveData var2 = new SaveData(var1);
      this.addSaveData(var2);
      return var2;
   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("level", this.levelIdentifier.stringID);
      if (this.levelIdentifier.isIslandPosition()) {
         var1.addInt("islandX", this.levelIdentifier.getIslandX());
         var1.addInt("islandY", this.levelIdentifier.getIslandY());
         var1.addInt("dimension", this.levelIdentifier.getIslandDimension());
      }

      var1.addInt("mobUniqueID", this.mobUniqueID);
   }

   public Mob getMob(Server var1, boolean var2) {
      Level var3;
      if (!var2) {
         if (!var1.world.levelManager.isLoaded(this.levelIdentifier)) {
            return null;
         }

         var3 = var1.world.getLevel(this.levelIdentifier, () -> {
            return null;
         });
         if (var3 == null) {
            return null;
         }
      } else {
         var3 = var1.world.getLevel(this.levelIdentifier, () -> {
            return null;
         });
         if (var3 == null) {
            return null;
         }
      }

      return (Mob)var3.entityManager.mobs.get(this.mobUniqueID, false);
   }

   public boolean isLevel(Level var1) {
      return var1.getIdentifier().equals(this.levelIdentifier);
   }
}
