package necesse.level.maps.presets;

import java.awt.Point;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;

public class DungeonEntrancePreset extends Preset {
   public DungeonEntrancePreset(GameRandom var1) {
      this(20, var1);
   }

   private DungeonEntrancePreset(int var1, GameRandom var2) {
      super(var1, var1);
      int var3 = var1 / 2;
      int var4 = var1 / 2 * 32;
      int var5 = TileRegistry.getTileID("dungeonfloor");
      int var6 = ObjectRegistry.getObjectID("dungeonwall");

      for(int var7 = 0; var7 < this.width; ++var7) {
         for(int var8 = 0; var8 < this.height; ++var8) {
            float var9 = (float)(new Point(var3 * 32 + 16, var3 * 32 + 16)).distance((double)(var7 * 32 + 16), (double)(var8 * 32 + 16));
            float var10 = var9 / (float)var4;
            if (var10 < 0.4F) {
               this.setTile(var7, var8, var5);
               this.setObject(var7, var8, 0);
            } else if (var10 <= 1.0F) {
               float var11 = Math.abs((var10 - 0.5F) * 2.0F - 1.0F) * 2.0F;
               if (var2.getChance(var11)) {
                  this.setTile(var7, var8, var5);
                  this.setObject(var7, var8, 0);
               }
            }

            if (var9 < (float)var4 && var9 >= (float)(var4 - 40) && var2.getChance(0.4F)) {
               this.setObject(var7, var8, var6);
            }
         }
      }

      byte var12 = 3;
      this.setFireChalice(var3 - var12 - 1, var3 - var12 - 1);
      this.setFireChalice(var3 - var12 - 1, var3 + var12);
      this.setFireChalice(var3 + var12, var3 - var12 - 1);
      this.setFireChalice(var3 + var12, var3 + var12);
      this.setObject(var3, var3, ObjectRegistry.getObjectID("dungeonentrance"));
      byte var13 = 5;
      this.addMob("voidapprentice", var3 - var13, var3 - var13, false);
      this.addMob("voidapprentice", var3 + var13, var3 - var13, false);
      this.addMob("voidapprentice", var3 - var13, var3 + var13, false);
      this.addMob("voidapprentice", var3 + var13, var3 + var13, false);
   }

   private void setFireChalice(int var1, int var2) {
      this.setObject(var1, var2, ObjectRegistry.getObjectID("firechalice"));
      this.setObject(var1 + 1, var2, ObjectRegistry.getObjectID("firechalice2"));
      this.setObject(var1, var2 + 1, ObjectRegistry.getObjectID("firechalice3"));
      this.setObject(var1 + 1, var2 + 1, ObjectRegistry.getObjectID("firechalice4"));
   }
}
