package necesse.level.maps.generationModules;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageFarm1Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageFarm2Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageFarm3Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse1Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse2Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse3Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse4Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse5Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse6Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse7Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse8Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse9Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePathPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;
import necesse.level.maps.presets.set.VillageSet;

public class VillageGeneration {
   private Level level;
   private float islandSize;
   public GameRandom random;
   private VillageSet defaultSet;
   private VillageSet villageSet;
   private ArrayList<AddedPreset> basicPresets;
   private ArrayList<AddedPreset> edgePresets;

   public VillageGeneration(Level var1, float var2, VillageSet var3) {
      this(var1, var2, var3, new GameRandom(var1.getSeed()));
   }

   public VillageGeneration(Level var1, float var2, VillageSet var3, GameRandom var4) {
      this.defaultSet = VillageSet.defaultSet;
      this.basicPresets = new ArrayList();
      this.edgePresets = new ArrayList();
      this.level = var1;
      this.islandSize = var2;
      this.villageSet = var3;
      this.random = var4;
   }

   public VillageGeneration addStandardPresets() {
      this.addBasicPreset(new VillageHouse1Preset(this.random), 3, 2);
      this.addBasicPreset(new VillageHouse2Preset(this.random), 3, 2);
      this.addBasicPreset(new VillageHouse5Preset(this.random), 3, 2);
      this.addBasicPreset(new VillageHouse6Preset(this.random), 3, 2);
      this.addBasicPreset(new VillageHouse7Preset(this.random), 3, 2);
      this.addBasicPreset(new VillageHouse8Preset(this.random), 3, 2);
      this.addBasicPreset(new VillageHouse9Preset(this.random), 3, 2);
      this.addEdgePreset(new VillageFarm1Preset(this.random), 8, 2);
      this.addEdgePreset(new VillageFarm2Preset(this.random), 20, 2);
      this.addEdgePreset(new VillageFarm3Preset(this.random), 20, 2);
      this.addEdgePreset(new VillageHouse3Preset(this.random), 50, 1);
      this.addEdgePreset(new VillageHouse4Preset(this.random), 50, 1);
      return this;
   }

   public VillageGeneration addBasicPreset(VillagePreset var1, int var2, int var3) {
      this.basicPresets.add(new AddedPreset(var1, var2, var3));
      return this;
   }

   public VillageGeneration addEdgePreset(VillagePreset var1, int var2, int var3) {
      this.edgePresets.add(new AddedPreset(var1, var2, var3));
      return this;
   }

   public void generate() {
      int var1 = (int)(this.islandSize * 60.0F) + 60;
      VillageModularGeneration var2 = new VillageModularGeneration(this.level, var1 / 3, var1 / 3, 3, 3, 1) {
         public Point getStartCell() {
            return new Point(this.cellsWidth / 2, this.cellsHeight / 2);
         }
      };
      var2.random = this.random;
      int var3 = this.level.width / 2 - var2.cellRes * var2.cellsWidth / 2;
      int var4 = this.level.height / 2 - var2.cellRes * var2.cellsHeight / 2;
      Rectangle var5 = new Rectangle((var2.getCellRealX(0) + var3) * 32, (var2.getCellRealY(0) + var4) * 32, var2.cellsWidth * var2.cellRes * 32, var2.cellsHeight * var2.cellRes * 32);
      this.level.entityManager.mobs.streamInRegionsShape(var5, 0).filter((var1x) -> {
         return var5.contains(var1x.getX(), var1x.getY());
      }).forEach(Mob::remove);
      VillagePreset var6 = (VillagePreset)this.villageSet.replacePreset((VillageSet)this.defaultSet, new VillagePathPreset());
      var2.setStartPreset(var6);
      var2.initGeneration(var3, var4);
      Point var7 = var2.getStartCell();
      int var8 = 4 + (int)(5.0F * this.islandSize);

      for(int var9 = 1; var9 <= var8; ++var9) {
         var2.applyPreset(var6, new Point(var7.x - var9, var7.y), false, false, var3, var4, new Point(var7.x - (var9 - 1), var7.y));
         var2.applyPreset(var6, new Point(var7.x + var9, var7.y), false, false, var3, var4, new Point(var7.x + (var9 - 1), var7.y));
         var2.applyPreset(var6, new Point(var7.x, var7.y - var9), false, false, var3, var4, new Point(var7.x, var7.y - (var9 - 1)));
         var2.applyPreset(var6, new Point(var7.x, var7.y + var9), false, false, var3, var4, new Point(var7.x, var7.y + (var9 - 1)));
      }

      VillagePathPreset var20;
      var2.addPreset(var20 = (VillagePathPreset)this.villageSet.replacePreset((VillageSet)this.defaultSet, new VillagePathPreset(true, true, true, true)), 6);
      VillagePathPreset var10;
      var2.addPreset(var10 = (VillagePathPreset)this.villageSet.replacePreset((VillageSet)this.defaultSet, new VillagePathPreset(true, false, true, false)), 3);
      VillagePathPreset var11;
      var2.addPreset(var11 = (VillagePathPreset)this.villageSet.replacePreset((VillageSet)this.defaultSet, new VillagePathPreset(false, true, false, true)), 3);
      var2.tickGeneration(var3, var4, 4);
      Iterator var12 = this.basicPresets.iterator();

      AddedPreset var13;
      while(var12.hasNext()) {
         var13 = (AddedPreset)var12.next();
         var2.addPreset(var13.preset, var13.tickets, var13.maxUsage);
      }

      var2.tickGeneration(var3, var4, var1 / 2);
      var12 = this.edgePresets.iterator();

      while(var12.hasNext()) {
         var13 = (AddedPreset)var12.next();
         var2.addPreset(var13.preset, var13.tickets, var13.maxUsage);
      }

      var2.tickGeneration(var3, var4, var1 * 5);
      ArrayList var21 = new ArrayList();
      ArrayList var22 = new ArrayList();
      VillagePathLocation var14 = new VillagePathLocation((VillagePathLocation)null, var2.getPlacedPreset(var7));
      var14.markGoesSomewhere();
      var22.add(var14);

      VillagePathLocation var15;
      for(; !var22.isEmpty(); var21.add(var15)) {
         var15 = (VillagePathLocation)var22.remove(0);
         if (var15.preset.isPresetOpenTop()) {
            this.tickPathRemove(var2, var15, var15.preset.cell.x, var15.preset.cell.y - 1, var21, var22);
         }

         if (var15.preset.isPresetOpenBot()) {
            this.tickPathRemove(var2, var15, var15.preset.cell.x, var15.preset.cell.y + 1, var21, var22);
         }

         if (var15.preset.isPresetOpenRight()) {
            this.tickPathRemove(var2, var15, var15.preset.cell.x + 1, var15.preset.cell.y, var21, var22);
         }

         if (var15.preset.isPresetOpenLeft()) {
            this.tickPathRemove(var2, var15, var15.preset.cell.x - 1, var15.preset.cell.y, var21, var22);
         }
      }

      Iterator var23 = var21.iterator();

      while(true) {
         VillagePathLocation var16;
         int var17;
         int var18;
         do {
            if (!var23.hasNext()) {
               var2.removeAllPresets(var20);
               var2.removeAllPresets(var10);
               var2.removeAllPresets(var11);
               var2.tickGeneration(var3, var4, var1 * 5);
               var2.endGeneration();
               return;
            }

            var16 = (VillagePathLocation)var23.next();
            var17 = var2.getCellRealX(var16.preset.cell.x) + var3;
            var18 = var2.getCellRealY(var16.preset.cell.y) + var4;
         } while(var16.goesSomewhere());

         var16.preset.replacedLevel.applyToLevel(this.level, var17, var18);

         for(VillagePathLocation var19 = var16.parent; var19 != null && var19.goesSomewhere(); var19 = var19.parent) {
            var2.openCell(var16.parent.preset.cell);
         }

         var2.removePlacedPreset(var16.preset);
      }
   }

   private void tickPathRemove(ModularGeneration var1, VillagePathLocation var2, int var3, int var4, List<VillagePathLocation> var5, List<VillagePathLocation> var6) {
      PlacedPreset var7 = var1.getPlacedPreset(new Point(var3, var4));
      if (var7 != null) {
         if (var7.preset instanceof VillagePathPreset) {
            if (var5.stream().noneMatch((var2x) -> {
               return var2x.preset.cell.equals(new Point(var3, var4));
            })) {
               var6.add(new VillagePathLocation(var2, var7));
            }
         } else {
            var2.markGoesSomewhere();
         }

      }
   }

   private class AddedPreset {
      public final VillagePreset preset;
      public final int tickets;
      public final int maxUsage;

      public AddedPreset(VillagePreset var2, int var3, int var4) {
         var2.replaceObject("storagebox", "sprucechest");
         this.preset = (VillagePreset)VillageGeneration.this.villageSet.replacePreset((VillageSet)VillageGeneration.this.defaultSet, var2);
         this.tickets = var3;
         this.maxUsage = var4;
      }
   }

   private static class VillagePathLocation {
      public final VillagePathLocation parent;
      public final PlacedPreset preset;
      private boolean goesSomewhere = false;

      public VillagePathLocation(VillagePathLocation var1, PlacedPreset var2) {
         this.parent = var1;
         this.preset = var2;
      }

      public void markGoesSomewhere() {
         this.goesSomewhere = true;
         if (this.parent != null) {
            this.parent.markGoesSomewhere();
         }

      }

      public boolean goesSomewhere() {
         return this.goesSomewhere;
      }
   }
}
