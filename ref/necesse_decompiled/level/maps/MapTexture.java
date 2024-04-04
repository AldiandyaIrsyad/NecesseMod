package necesse.level.maps;

import java.awt.Color;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.light.GameLight;

public class MapTexture extends GameTexture {
   private final LevelMap map;
   public final int rX;
   public final int rY;
   public final int startX;
   public final int startY;
   public final int tileWidth;
   public final int tileHeight;

   public MapTexture(LevelMap var1, int var2, int var3, int var4, int var5) {
      super("maptexture" + var2 + "x" + var3, var4 * 8, var5 * 8);
      this.map = var1;
      this.rX = var2;
      this.rY = var3;
      this.startX = var2 * 30;
      this.startY = var3 * 30;
      this.tileWidth = var4;
      this.tileHeight = var5;
   }

   public void updateMap(int var1, int var2, boolean var3) {
      if (this.map.includeTextures) {
         int var4 = this.startX + var1;
         int var5 = this.startY + var2;
         if (var4 >= 0 && var5 >= 0 && var4 < this.map.level.width && var5 < this.map.level.height) {
            Color var6 = new Color(150, 150, 150);
            if (!var3 && !this.map.isTileKnown(var4, var5)) {
               var6 = new Color(0, 0, 0);
            } else {
               Color var7 = this.map.level.getTile(var4, var5).getMapColor(this.map.level, var4, var5);
               if (var7 != null) {
                  var6 = var7;
               }

               if (this.map.level.getObjectID(var4, var5) != 0) {
                  Color var8 = this.map.level.getObject(var4, var5).getMapColor(this.map.level, var4, var5);
                  if (var8 != null) {
                     var6 = var8;
                  }
               }

               if (!var3) {
                  float var9;
                  GameLight var13;
                  if (this.map.level.isCave) {
                     var13 = this.map.level.lightManager.getStaticLight(var4, var5);
                     var9 = var13.getFloatLevel();
                     var6 = new Color((int)((float)var6.getRed() * var13.getFloatRed() * var9), (int)((float)var6.getGreen() * var13.getFloatGreen() * var9), (int)((float)var6.getBlue() * var13.getFloatBlue() * var9), var6.getAlpha());
                  } else if (!this.map.level.isOutside(var4, var5) && !this.map.level.isOutside(var4 - 1, var5) && !this.map.level.isOutside(var4 + 1, var5) && !this.map.level.isOutside(var4, var5 - 1) && !this.map.level.isOutside(var4, var5 + 1)) {
                     var13 = this.map.level.lightManager.getStaticLight(var4, var5);
                     var9 = Math.max(var13.getFloatLevel(), 0.7F);
                     var6 = new Color((int)((float)var6.getRed() * var13.getFloatRed() * var9), (int)((float)var6.getGreen() * var13.getFloatGreen() * var9), (int)((float)var6.getBlue() * var13.getFloatBlue() * var9), var6.getAlpha());
                  }
               }
            }

            for(int var12 = 0; var12 < 8; ++var12) {
               int var14 = var1 * 8 + var12;

               for(int var15 = 0; var15 < 8; ++var15) {
                  int var10 = var2 * 8 + var15;
                  Color var11 = this.getColor(var14, var10);
                  if (var11.getRGB() != var6.getRGB()) {
                     this.setPixel(var14, var10, var6.getRed(), var6.getGreen(), var6.getBlue(), 255);
                  }
               }
            }

         }
      }
   }

   public void updateMapTile(int var1, int var2, boolean var3) {
      this.updateMap(var1 - this.startX, var2 - this.startY, var3);
   }
}
