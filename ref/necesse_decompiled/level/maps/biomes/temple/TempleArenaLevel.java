package necesse.level.maps.biomes.temple;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.level.gameObject.GameObject;

public class TempleArenaLevel extends TempleLevel {
   public static final int ARENA_SIZE = 40;
   public static final int LAVA_EDGE_SIZE = 7;
   public static final int EDGE_SIZE = 40;
   private static final int TOTAL_SIZE = 134;

   public TempleArenaLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public TempleArenaLevel(int var1, int var2, int var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, var3), 134, 134, var4);
      this.isCave = true;
      this.isProtected = true;
      this.generateLevel();
   }

   public void generateLevel() {
      GameRandom var1 = new GameRandom(this.getSeed());
      int var2 = ObjectRegistry.getObjectID("deepsandstonewall");
      int var3 = TileRegistry.getTileID("sandbrick");
      int var4 = TileRegistry.getTileID("woodfloor");
      int var5 = TileRegistry.getTileID("lavatile");
      int var6 = ObjectRegistry.getObjectID("templefirechalice");
      TicketSystemList var7 = new TicketSystemList();
      var7.addObject(100, var3);
      var7.addObject(50, var4);

      int var8;
      int var9;
      for(var8 = 0; var8 < this.width; ++var8) {
         for(var9 = 0; var9 < this.height; ++var9) {
            this.setTile(var8, var9, (Integer)var7.getRandomObject(var1));
         }
      }

      var8 = this.width / 2;
      var9 = this.height / 2;

      for(int var10 = 0; var10 < this.width; ++var10) {
         for(int var11 = 0; var11 < this.height; ++var11) {
            double var12 = (new Point2D.Float((float)var8, (float)var9)).distance((double)var10, (double)var11);
            if (var12 <= 20.5) {
               this.setObject(var10, var11, 0);
            } else if (var12 <= 27.5) {
               this.setObject(var10, var11, 0);
               this.setTile(var10, var11, var5);
            } else {
               this.setObject(var10, var11, var2);
            }
         }
      }

      this.placeObjectAngle(var8, var9, 14.0F, -90.0F, var6, 0, 0.0F, 0.0F);
      this.placeObjectAngle(var8, var9, 14.0F, 45.0F, var6, 0, 0.0F, 0.0F);
      this.placeObjectAngle(var8, var9, 14.0F, -45.0F, var6, 0, 0.0F, 0.0F);
      this.placeObjectAngle(var8, var9, 14.0F, 0.0F, var6, 0, 0.0F, 0.0F);
      this.placeObjectAngle(var8, var9, 14.0F, 180.0F, var6, 0, -1.0F, 0.0F);
      this.placeObjectAngle(var8, var9, 14.0F, 135.0F, var6, 0, 0.0F, 0.0F);
      this.placeObjectAngle(var8, var9, 14.0F, -135.0F, var6, 0, 0.0F, 0.0F);
      Mob var14 = MobRegistry.getMob("fallenwizard", this);
      Point2D.Float var15 = getBossPosition();
      this.entityManager.addMob(var14, var15.x, var15.y);
   }

   private void placeObjectAngle(int var1, int var2, float var3, float var4, int var5, int var6, float var7, float var8) {
      GameObject var9 = ObjectRegistry.getObject(var5);
      Point2D.Float var10 = GameMath.getAngleDir(var4);
      var9.placeObject(this, (int)((float)var1 + var10.x * var3 + var7), (int)((float)var2 + var10.y * var3 + var8), var6);
   }

   public static Point getExitPosition() {
      return new Point(67, 82);
   }

   public static Point2D.Float getBossPosition() {
      return new Point2D.Float(2160.0F, 2160.0F);
   }
}
