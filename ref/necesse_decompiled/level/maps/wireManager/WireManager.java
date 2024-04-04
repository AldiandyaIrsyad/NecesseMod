package necesse.level.maps.wireManager;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.registries.LevelLayerRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.level.maps.Level;
import necesse.level.maps.layers.LevelLayer;
import necesse.level.maps.layers.WireLevelLayer;

public class WireManager {
   public static final int redWire = 0;
   public static final int greenWire = 1;
   public static final int blueWire = 2;
   public static final int yellowWire = 3;
   private static final int bitSize = 8;
   public static final int totalWires = 4;
   public static final String wireIdentifiers = "RGBY";
   private Level level;
   private final WireLevelLayer wireLayer;
   private static Point[] connections = new Point[]{new Point(0, -1), new Point(1, 0), new Point(0, 1), new Point(-1, 0)};

   public WireManager(Level var1) {
      this.level = var1;
      this.wireLayer = (WireLevelLayer)var1.getLayer(LevelLayerRegistry.WIRE_LAYER, WireLevelLayer.class);
   }

   public void clientTick() {
   }

   public void serverTick() {
   }

   public void addWireDrawables(SharedTextureDrawOptions var1, int var2, int var3, GameCamera var4, TickManager var5) {
      if (this.getWireData(var2, var3) != 0) {
         Performance.record(var5, "wireDrawSetup", (Runnable)(() -> {
            for(int var5 = 0; var5 < 4; ++var5) {
               if (this.hasWire(var2, var3, var5)) {
                  this.addWireDrawablesPreset(var1, var2, var3, var4, var5);
               }
            }

         }));
      }
   }

   public void drawWirePreset(int var1, int var2, GameCamera var3, int var4, Color var5) {
      SharedTextureDrawOptions var6 = new SharedTextureDrawOptions(GameResources.wire);
      this.addWireDrawablesPreset(var6, var1, var2, var3, var4, var5);
      var6.draw();
   }

   public void drawWirePreset(int var1, int var2, GameCamera var3, int var4) {
      this.drawWirePreset(var1, var2, var3, var4, getWireColor(var4));
   }

   public void addWireDrawablesPreset(SharedTextureDrawOptions var1, int var2, int var3, GameCamera var4, int var5, Color var6) {
      float var7 = (float)(var5 - 2) + 0.5F;
      int var8 = (int)(var7 * 4.0F);
      this.addWireDrawables(var1, var2, var3, var4, var5, var8, var8, var6);
   }

   public void addWireDrawablesPreset(SharedTextureDrawOptions var1, int var2, int var3, GameCamera var4, int var5) {
      this.addWireDrawablesPreset(var1, var2, var3, var4, var5, getWireColor(var5));
   }

   private void addWireDrawables(SharedTextureDrawOptions var1, int var2, int var3, GameCamera var4, int var5, int var6, int var7, Color var8) {
      int var9 = var4.getTileDrawX(var2);
      int var10 = var4.getTileDrawY(var3);
      boolean var11 = false;
      float var12 = (float)var8.getRed() / 255.0F;
      float var13 = (float)var8.getGreen() / 255.0F;
      float var14 = (float)var8.getBlue() / 255.0F;
      float var15 = this.isWireActive(var2, var3, var5) ? 1.0F : 0.33F;
      if (this.hasWire(var2, var3 - 1, var5)) {
         var11 = true;
         var1.addSpriteSection(0, 0, 32, 0, 32, 0, 16 + var7).color(var12, var13, var14).brightness(var15).size(32, 16 + var7).pos(var9 + var6, var10);
      }

      if (this.hasWire(var2, var3 + 1, var5)) {
         var11 = true;
         var1.addSpriteSection(0, 0, 32, 0, 32, 0, 16 + var7).color(var12, var13, var14).brightness(var15).size(32, 16 - var7).pos(var9 + var6, var10 + var7 + 16);
      }

      if (this.hasWire(var2 + 1, var3, var5)) {
         var11 = true;
         var1.addSpriteSection(1, 0, 32, 0, 16 + var6, 0, 32).color(var12, var13, var14).brightness(var15).size(16 - var6, 32).pos(var9 + var6 + 16, var10 + var7);
      }

      if (this.hasWire(var2 - 1, var3, var5)) {
         var11 = true;
         var1.addSpriteSection(1, 0, 32, 0, 16 + var6, 0, 32).color(var12, var13, var14).brightness(var15).size(16 + var6, 32).pos(var9, var10 + var7);
      }

      if (var11) {
         var1.addSprite(3, 0, 32).color(var12, var13, var14).brightness(var15).pos(var9 + var6, var10 + var7);
      } else {
         var1.addSprite(2, 0, 32).color(var12, var13, var14).brightness(var15).pos(var9 + var6, var10 + var7);
      }

   }

   public static Color getWireColor(int var0) {
      if (var0 == 0) {
         return new Color(255, 0, 0);
      } else if (var0 == 1) {
         return new Color(0, 255, 0);
      } else if (var0 == 2) {
         return new Color(0, 0, 255);
      } else {
         return var0 == 3 ? new Color(255, 255, 0) : new Color(255, 255, 255);
      }
   }

   public void updateWire(int var1, int var2, boolean var3) {
      for(int var4 = 0; var4 < 4; ++var4) {
         this.updateWire(var1, var2, var4, var3);
      }

   }

   private boolean isTileActive(int var1, int var2, int var3) {
      return this.level.getLevelObject(var1, var2).isWireActive(var3) || this.level.logicLayer.isWireActive(var1, var2, var3);
   }

   public boolean updateWire(int var1, int var2, int var3, boolean var4) {
      if (!this.hasWire(var1, var2, var3)) {
         return false;
      } else if (!var4 && this.isTileActive(var1, var2, var3)) {
         return false;
      } else {
         boolean var5 = this.isWireActive(var1, var2, var3);
         ArrayList var6 = new ArrayList();
         ArrayList var7 = new ArrayList();
         var6.add(new Point(var1, var2));

         while(var6.size() != 0) {
            Point var8 = (Point)var6.remove(0);
            var7.add(var8);
            Point[] var9 = connections;
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               Point var12 = var9[var11];
               Point var13 = new Point(var8.x + var12.x, var8.y + var12.y);
               if (this.hasWire(var13.x, var13.y, var3) && !var7.contains(var13) && !var6.contains(var13)) {
                  var6.add(var13);
                  if (!var4 && this.isTileActive(var13.x, var13.y, var3)) {
                     return false;
                  }
               }
            }
         }

         var7.forEach((var3x) -> {
            if (this.isWireActive(var3x.x, var3x.y, var3) != var4) {
               this.setWireActive(var3x.x, var3x.y, var3, var4);
               LevelLayer[] var4x = this.level.layers;
               int var5 = var4x.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  LevelLayer var7 = var4x[var6];
                  var7.onWireUpdate(var3x.x, var3x.y, var3, var4);
               }
            }

         });
         return var5 != var4;
      }
   }

   public boolean hasWire(int var1, int var2, int var3) {
      return GameMath.getBit((long)this.getWireData(var1, var2), var3 * 2);
   }

   public boolean isWireActiveAny(int var1, int var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         if (this.isWireActive(var1, var2, var3)) {
            return true;
         }
      }

      return false;
   }

   public boolean isWireActive(int var1, int var2, int var3) {
      return GameMath.getBit((long)this.getWireData(var1, var2), var3 * 2 + 1);
   }

   private boolean hasSameWire(byte var1, byte var2, int var3) {
      return GameMath.getBit((long)var1, var3 * 2) == GameMath.getBit((long)var2, var3 * 2);
   }

   public void setWireActive(int var1, int var2, int var3, boolean var4) {
      if (!this.hasWire(var1, var2, var3)) {
         var4 = false;
      }

      byte var5 = this.getWireData(var1, var2);
      this.wireLayer.setWireData(var1, var2, GameMath.setBit(var5, var3 * 2 + 1, var4));
   }

   public void setWire(int var1, int var2, int var3, boolean var4) {
      byte var5 = this.getWireData(var1, var2);
      this.setWireData(var1, var2, GameMath.setBit(var5, var3 * 2, var4), false);
      this.setWireActive(var1, var2, var3, false);
      Point[] var6;
      int var7;
      int var8;
      Point var9;
      if (!var4) {
         var6 = connections;
         var7 = var6.length;

         for(var8 = 0; var8 < var7; ++var8) {
            var9 = var6[var8];
            this.updateWire(var1 + var9.x, var2 + var9.y, var3, false);
         }
      } else {
         if (this.isAnyNonWireLayerActive(var1, var2, var3)) {
            this.updateWire(var1, var2, var3, true);
            return;
         }

         var6 = connections;
         var7 = var6.length;

         for(var8 = 0; var8 < var7; ++var8) {
            var9 = var6[var8];
            Point var10 = new Point(var1 + var9.x, var2 + var9.y);
            if (this.hasWire(var10.x, var10.y, var3) && this.isWireActive(var10.x, var10.y, var3)) {
               this.updateWire(var1, var2, var3, true);
               return;
            }
         }
      }

   }

   protected boolean isAnyNonWireLayerActive(int var1, int var2, int var3) {
      return this.level.getLevelObject(var1, var2).isWireActive(var3) || this.level.logicLayer.isWireActive(var1, var2, var3);
   }

   public byte getWireData(int var1, int var2) {
      return this.wireLayer.getWireData(var1, var2);
   }

   public void setWireData(int var1, int var2, byte var3, boolean var4) {
      if (var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height) {
         byte var5 = this.getWireData(var1, var2);

         for(int var6 = 0; var6 < 4; ++var6) {
            boolean var7 = GameMath.getBit((long)var5, var6 * 2);
            boolean var8 = GameMath.getBit((long)var3, var6 * 2);
            if (var7 != var8) {
               if (var4) {
                  this.setWire(var1, var2, var6, var8);
               } else {
                  this.wireLayer.setWireData(var1, var2, GameMath.setBit(var5, var6 * 2, var8));
               }
            }
         }

      }
   }
}
