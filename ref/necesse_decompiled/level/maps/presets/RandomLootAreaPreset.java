package necesse.level.maps.presets;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;

public class RandomLootAreaPreset extends Preset {
   public RandomLootAreaPreset(GameRandom var1, int var2, String var3, String... var4) {
      super(var2, var2);
      int var5 = var2 / 2;
      int var6 = var2 / 2 * 32;
      int var7 = TileRegistry.getTileID("woodfloor");
      int[] var8 = new int[]{ObjectRegistry.getObjectID("crate"), ObjectRegistry.getObjectID("vase"), ObjectRegistry.getObjectID("coinstack")};

      int var9;
      float var11;
      for(var9 = 0; var9 < this.width; ++var9) {
         for(int var10 = 0; var10 < this.height; ++var10) {
            var11 = (float)(new Point(var5 * 32 + 16, var5 * 32 + 16)).distance((double)(var9 * 32 + 16), (double)(var10 * 32 + 16));
            float var12 = var11 / (float)var6;
            if (var12 < 0.5F) {
               this.setTile(var9, var10, var7);
               this.setObject(var9, var10, 0);
            } else if (var12 <= 1.0F) {
               float var13 = Math.abs((var12 - 0.5F) * 2.0F - 1.0F) * 2.0F;
               if (var1.getChance(var13)) {
                  if (var1.getChance(0.75F)) {
                     this.setTile(var9, var10, var7);
                  }

                  this.setObject(var9, var10, 0);
               }
            }

            if (var12 <= 1.0F && this.getObject(var9, var10) != -1 && var1.getChance(0.12F)) {
               this.setObject(var9, var10, var8[var1.nextInt(var8.length)]);
            }
         }
      }

      var9 = var1.getIntBetween(3, 4);
      float var21 = (float)var1.nextInt(360);
      var11 = 360.0F / (float)var9;
      int var22 = ObjectRegistry.getObjectID(var3);

      float var15;
      int var23;
      for(var23 = 0; var23 < var9; ++var23) {
         var21 += var1.getFloatOffset(var11, var11 / 10.0F);
         Point2D.Float var14 = GameMath.getAngleDir(var21);
         var15 = (float)var2 / 4.0F;
         int var16 = (int)((float)var5 + var14.x * var15);
         int var17 = (int)((float)var5 + var14.y * var15);
         this.setObject(var16, var17, var22);
      }

      if (var4.length > 0) {
         var23 = var2 / 3;

         for(int var24 = 0; var24 < var23; ++var24) {
            var15 = var1.nextFloat() * 360.0F;
            float var25 = (float)Math.cos(Math.toRadians((double)var15));
            float var26 = (float)Math.sin(Math.toRadians((double)var15));
            int var18 = var2 / 4 * 32;
            int var19 = (int)(((float)(var5 * 32 + 16) + var25 * (float)var18) / 32.0F);
            int var20 = (int)(((float)(var5 * 32 + 16) + var26 * (float)var18) / 32.0F);
            if (this.getObject(var19, var20) == 0) {
               this.addMob((String)var1.getOneOf((Object[])var4), var19, var20, false);
            }
         }
      }

   }
}
