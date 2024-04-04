package necesse.level.maps.splattingManager;

import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.LiquidTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

public class SplattingOptions {
   private final SplattingOption[] topLeft;
   private final SplattingOption[] topRight;
   private final SplattingOption[] botRight;
   private final SplattingOption[] botLeft;

   public SplattingOptions(Level var1, int var2, int var3) {
      GameTile var4 = var1.getTile(var2, var3);
      GameTile[] var5 = var1.getAdjacentTiles(var2, var3);
      GameObject[] var6 = var1.getAdjacentObjects(var2, var3);
      boolean[] var7 = new boolean[var5.length];

      for(int var8 = 0; var8 < var7.length; ++var8) {
         var7[var8] = this.splatsInto(var4, var5[var8], var6[var8]);
      }

      this.topLeft = this.generateSplattingTopLeft(var5, var6, var7);
      this.topRight = this.generateSplattingTopRight(var5, var6, var7);
      this.botRight = this.generateSplattingBotRight(var5, var6, var7);
      this.botLeft = this.generateSplattingBotLeft(var5, var6, var7);
   }

   public boolean isNull() {
      return this.topLeft == null && this.topRight == null && this.botRight == null && this.botLeft == null;
   }

   public void addTileDrawables(LevelTileDrawOptions var1, TerrainSplatterTile var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
      Performance.record(var7, "drawSplatSetup", (Runnable)(() -> {
         int var8 = var6.getTileDrawX(var4);
         int var9 = var6.getTileDrawY(var5);
         var1.add(var2.getTerrainTexture(var3, var4, var5)).pos(var8, var9);
         if (this.topLeft != null) {
            this.addSplatDrawOptions(var1, this.topLeft, var3, var4, var5, var8, var9, var7);
         }

         if (this.topRight != null) {
            this.addSplatDrawOptions(var1, this.topRight, var3, var4, var5, var8 + 16, var9, var7);
         }

         if (this.botRight != null) {
            this.addSplatDrawOptions(var1, this.botRight, var3, var4, var5, var8 + 16, var9 + 16, var7);
         }

         if (this.botLeft != null) {
            this.addSplatDrawOptions(var1, this.botLeft, var3, var4, var5, var8, var9 + 16, var7);
         }

      }));
   }

   private void addSplatDrawOptions(LevelTileDrawOptions var1, SplattingOption[] var2, Level var3, int var4, int var5, int var6, int var7, TickManager var8) {
      SplattingOption[] var9 = var2;
      int var10 = var2.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         SplattingOption var12 = var9[var11];
         var12.addDrawOptions(var1, var3, var4, var5, var6, var7, var8);
      }

   }

   private SplattingOption[] generateSplattingTopLeft(GameTile[] var1, GameObject[] var2, boolean[] var3) {
      byte var4 = 3;
      byte var5 = 0;
      byte var6 = 1;
      ArrayList var7 = new ArrayList();
      if (var3[var4]) {
         if (this.sameSplat(var1, var2, var4, var6)) {
            this.addOption(var7, var1[var4], 2, 2);
         } else {
            this.addOption(var7, var1[var4], 0, 2);
         }
      }

      if (var3[var6] && !this.sameSplat(var1, var2, var6, var4)) {
         this.addOption(var7, var1[var6], 2, 0);
      }

      if (var3[var5] && !this.sameSplat(var1, var2, var5, var6) && !this.sameSplat(var1, var2, var5, var4)) {
         this.addOption(var7, var1[var5], 0, 0);
      }

      var7.sort((Comparator)null);
      return var7.size() == 0 ? null : (SplattingOption[])var7.toArray(new SplattingOption[var7.size()]);
   }

   private SplattingOption[] generateSplattingTopRight(GameTile[] var1, GameObject[] var2, boolean[] var3) {
      byte var4 = 1;
      byte var5 = 2;
      byte var6 = 4;
      ArrayList var7 = new ArrayList();
      if (var3[var4]) {
         if (this.sameSplat(var1, var2, var4, var6)) {
            this.addOption(var7, var1[var4], 3, 2);
         } else {
            this.addOption(var7, var1[var4], 3, 0);
         }
      }

      if (var3[var6] && !this.sameSplat(var1, var2, var6, var4)) {
         this.addOption(var7, var1[var6], 1, 2);
      }

      if (var3[var5] && !this.sameSplat(var1, var2, var5, var4) && !this.sameSplat(var1, var2, var5, var6)) {
         this.addOption(var7, var1[var5], 1, 0);
      }

      var7.sort((Comparator)null);
      return var7.size() == 0 ? null : (SplattingOption[])var7.toArray(new SplattingOption[var7.size()]);
   }

   private SplattingOption[] generateSplattingBotRight(GameTile[] var1, GameObject[] var2, boolean[] var3) {
      byte var4 = 4;
      byte var5 = 7;
      byte var6 = 6;
      ArrayList var7 = new ArrayList();
      if (var3[var4]) {
         if (this.sameSplat(var1, var2, var4, var6)) {
            this.addOption(var7, var1[var4], 3, 3);
         } else {
            this.addOption(var7, var1[var4], 1, 3);
         }
      }

      if (var3[var6] && !this.sameSplat(var1, var2, var6, var4)) {
         this.addOption(var7, var1[var6], 3, 1);
      }

      if (var3[var5] && !this.sameSplat(var1, var2, var5, var6) && !this.sameSplat(var1, var2, var5, var4)) {
         this.addOption(var7, var1[var5], 1, 1);
      }

      var7.sort((Comparator)null);
      return var7.size() == 0 ? null : (SplattingOption[])var7.toArray(new SplattingOption[var7.size()]);
   }

   private SplattingOption[] generateSplattingBotLeft(GameTile[] var1, GameObject[] var2, boolean[] var3) {
      byte var4 = 6;
      byte var5 = 5;
      byte var6 = 3;
      ArrayList var7 = new ArrayList();
      if (var3[var4]) {
         if (this.sameSplat(var1, var2, var4, var6)) {
            this.addOption(var7, var1[var4], 2, 3);
         } else {
            this.addOption(var7, var1[var4], 2, 1);
         }
      }

      if (var3[var6] && !this.sameSplat(var1, var2, var6, var4)) {
         this.addOption(var7, var1[var6], 0, 3);
      }

      if (var3[var5] && !this.sameSplat(var1, var2, var5, var6) && !this.sameSplat(var1, var2, var5, var4)) {
         this.addOption(var7, var1[var5], 0, 1);
      }

      var7.sort((Comparator)null);
      return var7.size() == 0 ? null : (SplattingOption[])var7.toArray(new SplattingOption[var7.size()]);
   }

   private boolean sameSplat(GameTile[] var1, GameObject[] var2, int var3, int var4) {
      if (var1[var3] != var1[var4]) {
         return false;
      } else {
         return !var2[var3].stopsTerrainSplatting() && !var2[var4].stopsTerrainSplatting();
      }
   }

   private boolean splatsInto(GameTile var1, GameTile var2, GameObject var3) {
      if (var1 != var2 && var1.terrainSplatting && !var3.stopsTerrainSplatting()) {
         if (var2.isLiquid) {
            return true;
         } else if (var2.terrainSplatting) {
            return TerrainSplatterTile.comparePriority((TerrainSplatterTile)var1, (TerrainSplatterTile)var2) < 0;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void addDebugTooltips(StringTooltips var1) {
      var1.add("Splat TL: " + this.getDebugTooltips(this.topLeft));
      var1.add("Splat TR: " + this.getDebugTooltips(this.topRight));
      var1.add("Splat BL: " + this.getDebugTooltips(this.botLeft));
      var1.add("Splat BR: " + this.getDebugTooltips(this.botRight));
   }

   private String getDebugTooltips(SplattingOption[] var1) {
      if (var1 == null) {
         return "null";
      } else {
         String var2 = "";

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var2 = var2 + TileRegistry.getTile(var1[var3].tile).getStringID() + "(" + var1[var3].spriteX + "," + var1[var3].spriteY + ")";
            if (var3 < var1.length - 1) {
               var2 = var2 + ", ";
            }
         }

         return var2;
      }
   }

   private void addOption(ArrayList<SplattingOption> var1, GameTile var2, int var3, int var4) {
      if (var2.isLiquid) {
         var1.add(new LiquidSplattingOption(var2, var3, var4));
      } else if (var2.terrainSplatting) {
         var1.add(new TerrainSplattingOption(var2, var3, var4));
      }

   }

   private abstract static class SplattingOption implements Comparable<SplattingOption> {
      public final short tile;
      public final byte spriteX;
      public final byte spriteY;

      public SplattingOption(GameTile var1, int var2, int var3) {
         this.tile = (short)var1.getID();
         this.spriteX = (byte)var2;
         this.spriteY = (byte)var3;
      }

      public abstract void addDrawOptions(LevelTileDrawOptions var1, Level var2, int var3, int var4, int var5, int var6, TickManager var7);
   }

   private static class LiquidSplattingOption extends SplattingOption {
      public LiquidSplattingOption(GameTile var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      public void addDrawOptions(LevelTileDrawOptions var1, Level var2, int var3, int var4, int var5, int var6, TickManager var7) {
         Performance.record(var7, "shoreDrawSetup", (Runnable)(() -> {
            LiquidTile var7 = (LiquidTile)TileRegistry.getTile(this.tile);
            var1.add(var7.getShoreTexture().sprite(this.spriteX, this.spriteY, 16)).color(var7.getLiquidColor(var2, var3, var4)).pos(var5, var6);
         }));
      }

      public int compareTo(SplattingOption var1) {
         return 1;
      }

      // $FF: synthetic method
      // $FF: bridge method
      public int compareTo(Object var1) {
         return this.compareTo((SplattingOption)var1);
      }
   }

   private static class TerrainSplattingOption extends SplattingOption {
      public TerrainSplattingOption(GameTile var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      public void addDrawOptions(LevelTileDrawOptions var1, Level var2, int var3, int var4, int var5, int var6, TickManager var7) {
         Performance.record(var7, "terrainDrawSetup", (Runnable)(() -> {
            var1.add(((TerrainSplatterTile)TileRegistry.getTile(this.tile)).getSplattingTexture(var2, var3, var4).sprite(this.spriteX, this.spriteY, 16)).pos(var5, var6);
         }));
      }

      public int compareTo(SplattingOption var1) {
         GameTile var2 = TileRegistry.getTile(this.tile);
         GameTile var3 = TileRegistry.getTile(var1.tile);
         return var3.isLiquid ? -1 : TerrainSplatterTile.comparePriority((TerrainSplatterTile)var2, (TerrainSplatterTile)var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public int compareTo(Object var1) {
         return this.compareTo((SplattingOption)var1);
      }
   }
}
