package necesse.level.maps.presets;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;

public class AncientVultureArenaPreset extends Preset {
   public AncientVultureArenaPreset(int var1, GameRandom var2) {
      super(var1, var1);
      int var3 = var1 / 2;
      int var4 = var1 / 2 * 32;
      int var5 = TileRegistry.getTileID("woodfloor");
      int var6 = TileRegistry.getTileID("sandstonetile");
      int var7 = TileRegistry.getTileID("sandbrick");
      int[] var8 = new int[]{ObjectRegistry.getObjectID("crate"), ObjectRegistry.getObjectID("vase")};

      int var9;
      float var11;
      for(var9 = 0; var9 < this.width; ++var9) {
         for(int var10 = 0; var10 < this.height; ++var10) {
            var11 = (float)(new Point(var3 * 32 + 16, var3 * 32 + 16)).distance((double)(var9 * 32 + 16), (double)(var10 * 32 + 16));
            float var12 = var11 / (float)var4;
            if (var12 < 0.5F) {
               this.setTile(var9, var10, var5);
               if (var2.getChance(0.8F)) {
                  this.setTile(var9, var10, var7);
               }

               this.setObject(var9, var10, 0);
            } else if (var12 <= 1.0F) {
               float var13 = Math.abs((var12 - 0.5F) * 2.0F - 1.0F) * 2.0F;
               if (var2.getChance(var13)) {
                  if (var2.getChance(0.75F)) {
                     this.setTile(var9, var10, var2.getChance(0.75F) ? var7 : var5);
                  } else {
                     this.setTile(var9, var10, var6);
                  }

                  this.setObject(var9, var10, 0);
               }
            }

            if (var12 <= 1.0F && this.getObject(var9, var10) != -1 && var2.getChance(0.1F)) {
               this.setObject(var9, var10, var8[var2.nextInt(var8.length)]);
            }
         }
      }

      var9 = var2.getIntBetween(8, 10);
      float var18 = (float)var2.nextInt(360);
      var11 = 360.0F / (float)var9;
      int var19 = ObjectRegistry.getObjectID("sandstonecolumn");

      for(int var20 = 0; var20 < var9; ++var20) {
         var18 += var2.getFloatOffset(var11, var11 / 10.0F);
         Point2D.Float var14 = GameMath.getAngleDir(var18);
         float var15 = (float)var1 / 3.0F;
         int var16 = (int)((float)var3 + var14.x * var15);
         int var17 = (int)((float)var3 + var14.y * var15);
         this.setObject(var16, var17, var19);
      }

      this.setObject(var3, var3, ObjectRegistry.getObjectID("ancienttotem"));
   }
}
