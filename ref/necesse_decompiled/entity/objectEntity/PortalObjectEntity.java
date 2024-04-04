package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TeleportResult;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class PortalObjectEntity extends ObjectEntity {
   public LevelIdentifier destinationIdentifier;
   public int destinationTileX;
   public int destinationTileY;
   public int clearHostileMobsRadius = 160;
   public int clearLevelGeneratedHostileMobsRadius = 640;

   public PortalObjectEntity(Level var1, String var2, int var3, int var4, LevelIdentifier var5, int var6, int var7) {
      super(var1, "portal." + var2, var3, var4);
      this.destinationIdentifier = var5;
      this.destinationTileX = var6;
      this.destinationTileY = var7;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addUnsafeString("destination", this.destinationIdentifier.stringID);
      var1.addInt("destinationTileX", this.destinationTileX);
      var1.addInt("destinationTileY", this.destinationTileY);
      if (this.destinationIdentifier.isIslandPosition()) {
         var1.addInt("dIslandX", this.destinationIdentifier.getIslandX());
         var1.addInt("dIslandY", this.destinationIdentifier.getIslandY());
         var1.addInt("dDimension", this.destinationIdentifier.getIslandDimension());
         var1.addInt("dX", this.destinationTileX);
         var1.addInt("dY", this.destinationTileY);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);

      try {
         this.destinationIdentifier = new LevelIdentifier(var1.getUnsafeString("destination", (String)null, false));
      } catch (InvalidLevelIdentifierException var8) {
         int var3 = var1.getInt("dIslandX");
         int var4 = var1.getInt("dIslandY");
         int var5 = var1.getInt("dDimension");
         this.destinationIdentifier = new LevelIdentifier(var3, var4, var5);
      }

      try {
         this.destinationTileX = var1.getInt("destinationTileX");
      } catch (Exception var7) {
         this.destinationTileX = var1.getInt("dX");
      }

      try {
         this.destinationTileY = var1.getInt("destinationTileY");
      } catch (Exception var6) {
         this.destinationTileY = var1.getInt("dY");
      }

   }

   public boolean shouldRequestPacket() {
      return false;
   }

   public void use(Server var1, ServerClient var2) {
      var2.changeLevel(this.getDestinationIdentifier(), (var1x) -> {
         return new Point(this.getDestinationX(), this.getDestinationY());
      }, true);
   }

   public void runClearMobs(ServerClient var1) {
      if (this.clearHostileMobsRadius > 0 || this.clearLevelGeneratedHostileMobsRadius > 0) {
         Iterator var2 = var1.getLevel().entityManager.mobs.getInRegionByTileRange(this.getX(), this.getY(), Math.max(this.clearHostileMobsRadius, this.clearLevelGeneratedHostileMobsRadius) / 32 + 1).iterator();

         while(true) {
            Mob var3;
            float var4;
            do {
               do {
                  do {
                     if (!var2.hasNext()) {
                        return;
                     }

                     var3 = (Mob)var2.next();
                  } while(var3.isBoss());
               } while(!var3.isHostile);

               var4 = var3.getDistance((float)(this.getX() * 32 + 16), (float)(this.getY() * 32 + 16));
            } while(!(var4 < (float)this.clearHostileMobsRadius) && (var3.canDespawn || !(var4 < (float)this.clearLevelGeneratedHostileMobsRadius)));

            var3.remove();
         }
      }
   }

   public LevelIdentifier getDestinationIdentifier() {
      return this.destinationIdentifier;
   }

   public int getDestinationX() {
      return this.destinationTileX * 32 + 16;
   }

   public int getDestinationY() {
      return this.destinationTileY * 32 + 16;
   }

   protected void teleportClientToAroundDestination(ServerClient var1, Predicate<Level> var2, boolean var3) {
      this.teleportClientToAroundDestination(var1, (Function)null, var2, var3);
   }

   protected void teleportClientToAroundDestination(ServerClient var1, Function<LevelIdentifier, Level> var2, Predicate<Level> var3, boolean var4) {
      var1.changeLevelCheck(this.getDestinationIdentifier(), var2, (var3x) -> {
         boolean var4 = var3 == null || var3.test(var3x);
         if (!var4) {
            return new TeleportResult(false, (Point)null);
         } else {
            Point var5 = getTeleportDestinationAroundObject(var3x, var1.playerMob, this.destinationTileX, this.destinationTileY, true);
            if (var5 == null) {
               var5 = new Point(this.destinationTileX * 32 + 16, this.destinationTileY * 32 + 16);
            }

            return new TeleportResult(true, var5);
         }
      }, var4);
   }

   public static Point getTeleportDestinationAroundObject(Level var0, Mob var1, int var2, int var3, boolean var4) {
      ArrayList var5 = new ArrayList(8);
      ArrayList var6 = new ArrayList(8);
      ArrayList var7 = new ArrayList(8);

      int var10;
      int var11;
      for(Iterator var8 = var0.getLevelObject(var2, var3).getMultiTile().getAdjacentTiles(var2, var3, var4).iterator(); var8.hasNext(); var7.add(new Point(var10, var11))) {
         Point var9 = (Point)var8.next();
         var10 = var9.x * 32 + 16;
         var11 = var9.y * 32 + 16;
         if (!var1.collidesWith(var0, var10, var11)) {
            if (!var1.collidesWithAnyMob(var0, var10, var11)) {
               var5.add(new Point(var10, var11));
            }

            var6.add(new Point(var10, var11));
         }
      }

      if (!var5.isEmpty()) {
         return (Point)GameRandom.globalRandom.getOneOf((List)var5);
      } else if (!var6.isEmpty()) {
         return (Point)GameRandom.globalRandom.getOneOf((List)var6);
      } else {
         return (Point)GameRandom.globalRandom.getOneOf((List)var7);
      }
   }
}
