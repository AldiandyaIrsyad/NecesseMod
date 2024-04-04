package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.WaystoneObjectEntity;
import necesse.level.maps.Level;

public class Waystone {
   public LevelIdentifier destination;
   public int tileX;
   public int tileY;
   public String name;

   public Waystone(LevelIdentifier var1, int var2, int var3) {
      this.destination = var1;
      this.tileX = var2;
      this.tileY = var3;
      this.name = "";
   }

   public Waystone(LoadData var1) {
      try {
         this.destination = new LevelIdentifier(var1.getUnsafeString("destination", (String)null, false));
      } catch (InvalidLevelIdentifierException var6) {
         int var3 = var1.getInt("islandX");
         int var4 = var1.getInt("islandY");
         int var5 = var1.getInt("dimension");
         this.destination = new LevelIdentifier(var3, var4, var5);
      }

      this.tileX = var1.getInt("tileX");
      this.tileY = var1.getInt("tileY");
      if (var1.hasLoadDataByName("name")) {
         this.name = var1.getSafeString("name");
      } else {
         this.name = "";
      }

   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("destination", this.destination.stringID);
      if (this.destination.isIslandPosition()) {
         var1.addInt("islandX", this.destination.getIslandX());
         var1.addInt("islandY", this.destination.getIslandY());
         var1.addInt("dimension", this.destination.getIslandDimension());
      }

      var1.addInt("tileX", this.tileX);
      var1.addInt("tileY", this.tileY);
      if (this.name != null && !this.name.isEmpty()) {
         var1.addSafeString("name", this.name);
      }

   }

   public Waystone(PacketReader var1) {
      this.destination = new LevelIdentifier(var1);
      this.tileX = var1.getNextInt();
      this.tileY = var1.getNextInt();
      this.name = var1.getNextString();
   }

   public void writePacket(PacketWriter var1) {
      this.destination.writePacket(var1);
      var1.putNextInt(this.tileX);
      var1.putNextInt(this.tileY);
      var1.putNextString(this.name);
   }

   public boolean checkIsValid(Level var1, Server var2) {
      Level var3 = var2.world.getLevel(this.destination);
      if (var3.getObject(this.tileX, this.tileY).getStringID().equals("waystone")) {
         ObjectEntity var4 = var3.entityManager.getObjectEntity(this.tileX, this.tileY);
         if (var4 instanceof WaystoneObjectEntity) {
            WaystoneObjectEntity var5 = (WaystoneObjectEntity)var4;
            return var5.homeIsland != null && var1.getIdentifier().isSameIsland(var5.homeIsland);
         }
      }

      return false;
   }

   public boolean matches(Level var1, int var2, int var3) {
      return var1.getIdentifier().equals(this.destination) && this.tileX == var2 && this.tileY == var3;
   }

   public Point findTeleportLocation(Server var1, Mob var2) {
      return findTeleportLocation(var1.world.getLevel(this.destination), this.tileX, this.tileY, var2);
   }

   public static Point findTeleportLocation(Level var0, int var1, int var2, Mob var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = var2 + 1; var5 >= var2 - 1; --var5) {
         for(int var6 = var1 - 1; var6 <= var1 + 1; ++var6) {
            Point var7 = new Point(var6 * 32 + 16, var5 * 32 + 16);
            if (!var3.collidesWith(var0, var7.x, var7.y)) {
               var4.add(var7);
            }
         }
      }

      return (Point)var4.stream().min(Comparator.comparingDouble((var2x) -> {
         return var2x.distance((double)(var1 * 32 + 16), (double)(var2 * 32 + 16));
      })).orElse(new Point(var1 * 32 + 16, var2 * 32 + 16));
   }
}
