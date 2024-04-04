package necesse.level.gameObject;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SimulatePriorityList;
import necesse.level.maps.light.GameLight;

public class DeepSwampTallGrassObject extends GrassObject {
   public static double spreadChance = GameMath.getAverageSuccessRuns(300.0);

   public DeepSwampTallGrassObject() {
      super("deepswamptallgrass", -1);
      this.canPlaceOnShore = true;
      this.mapColor = new Color(37, 63, 37);
      this.displayMapTooltip = true;
      this.weaveAmount = 0.05F;
      this.extraWeaveSpace = 32;
      this.randomYOffset = 3.0F;
      this.randomXOffset = 10.0F;
      this.attackThrough = false;
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("object", "swampgrass");
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      float var4 = 30.0F;
      if (var1.rainingLayer.isRaining()) {
         var4 = 10.0F;
      }

      if (var1.isCave) {
         var4 = 25.0F;
      }

      return new LootTable(new LootItemInterface[]{new ChanceLootItem(1.0F / var4, "swamplarva")});
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      return var1.getTileID(var2, var3) != TileRegistry.deepSwampRockID ? "notswamprock" : super.canPlace(var1, var2, var3, var4);
   }

   public boolean isValid(Level var1, int var2, int var3) {
      return super.isValid(var1, var2, var3) && var1.getTileID(var2, var3) == TileRegistry.deepSwampRockID;
   }

   public int getLightLevelMod(Level var1, int var2, int var3) {
      return 30;
   }

   public void addSimulateLogic(Level var1, int var2, int var3, long var4, SimulatePriorityList var6, boolean var7) {
      super.addSimulateLogic(var1, var2, var3, var4, var6, var7);
      this.addSimulateSpread(var1, var2, var3, 2, 8, 1, spreadChance, var4, var6, var7);
   }

   public void tick(Level var1, int var2, int var3) {
      super.tick(var1, var2, var3);
      if (var1.isServer() && GameRandom.globalRandom.getChance(spreadChance)) {
         Performance.record(var1.tickManager(), "growTallGrass", (Runnable)(() -> {
            this.tickSpread(var1, var2, var3, 2, 8, 1);
         }));
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      Performance.record(var6, "thornsSetup", (Runnable)(() -> {
         Integer[] var7x = var3.getAdjacentObjectsInt(var4, var5);
         int var8 = 0;
         Integer[] var9 = var7x;
         int var10 = var7x.length;

         int var11;
         for(var11 = 0; var11 < var10; ++var11) {
            Integer var12 = var9[var11];
            if (var12 == this.getID()) {
               ++var8;
            }
         }

         byte var14;
         byte var15;
         if (var8 < 4) {
            var14 = 4;
            var15 = 7;
         } else {
            var14 = 0;
            var15 = 3;
         }

         var11 = var7.getTileDrawX(var4);
         int var16 = var7.getTileDrawY(var5);
         GameLight var13 = var3.getLightLevel(var4, var5);
         this.addGrassDrawable(var1, var2, var3, var4, var5, var11, var16, var13, 6, -5, 0, var14, var15);
         this.addGrassDrawable(var1, var2, var3, var4, var5, var11, var16, var13, 12, -5, 1);
         this.addGrassDrawable(var1, var2, var3, var4, var5, var11, var16, var13, 26, -5, 2, var14, var15);
      }));
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      Integer[] var8 = var1.getAdjacentObjectsInt(var2, var3);
      int var9 = 0;
      Integer[] var10 = var8;
      int var11 = var8.length;

      int var12;
      for(var12 = 0; var12 < var11; ++var12) {
         Integer var13 = var10[var12];
         if (var13 == this.getID()) {
            ++var9;
         }
      }

      byte var16;
      byte var17;
      if (var9 < 4) {
         var16 = 4;
         var17 = 7;
      } else {
         var16 = 0;
         var17 = 3;
      }

      var12 = var7.getTileDrawX(var2);
      int var18 = var7.getTileDrawY(var3);
      LinkedList var14 = new LinkedList();
      OrderableDrawables var15 = new OrderableDrawables(new TreeMap());
      this.addGrassDrawable(var14, var15, var1, var2, var3, var12, var18, (GameLight)null, 6, -5, 0, var16, var17, 0.5F);
      this.addGrassDrawable(var14, var15, var1, var2, var3, var12, var18, (GameLight)null, 12, -5, 1, 0.5F);
      this.addGrassDrawable(var14, var15, var1, var2, var3, var12, var18, (GameLight)null, 26, -5, 2, var16, var17, 0.5F);
      var15.forEach((var1x) -> {
         var1x.draw(var1.tickManager());
      });
      var14.forEach((var1x) -> {
         var1x.draw(var1.tickManager());
      });
   }

   public boolean shouldSnapSmartMining(Level var1, int var2, int var3) {
      return true;
   }
}
