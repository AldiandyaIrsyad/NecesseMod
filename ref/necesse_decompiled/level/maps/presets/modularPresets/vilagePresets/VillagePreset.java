package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.presets.modularPresets.ModularPreset;

public class VillagePreset extends ModularPreset {
   public int shoreTileID;
   public GameRandom random;
   public final int path;
   public final int grass;
   private boolean isPath;

   private VillagePreset(int var1, int var2, boolean var3, int var4, int var5, GameRandom var6, boolean var7) {
      super(var1, var2, 3, 3, 1);
      this.shoreTileID = TileRegistry.sandID;
      this.isPath = var3;
      this.path = var4;
      this.grass = var5;
      if (var3) {
         this.openObject = -1;
         this.openTile = -1;
         this.closeObject = -1;
         this.closeTile = -1;
      } else {
         this.openObject = -1;
         this.openTile = -1;
         this.closeObject = -1;
         this.closeTile = -1;
      }

      if (var7) {
         if (var6 != null) {
            this.random = new GameRandom(var6.nextLong());
         }

         this.addCustomPreApplyRectEach(0, 0, this.width, this.height, 0, (var1x, var2x, var3x, var4x) -> {
            if (this.shoreTileID > 0 && var1x.getTile(var2x, var3x).isLiquid) {
               var1x.setTile(var2x, var3x, this.shoreTileID);
            }

            return null;
         });
      } else {
         this.random = var6;
      }

   }

   public VillagePreset(int var1, int var2, boolean var3, int var4, int var5, GameRandom var6) {
      this(var1, var2, var3, var4, var5, var6, true);
   }

   public VillagePreset(int var1, int var2, boolean var3, GameRandom var4) {
      this(var1, var2, var3, TileRegistry.getTileID("stonepathtile"), TileRegistry.grassID, var4);
   }

   public VillagePreset(int var1, int var2, boolean var3) {
      this(var1, var2, var3, (GameRandom)null);
   }

   protected VillagePreset newModularObject(int var1, int var2, int var3, int var4, int var5) {
      return new VillagePreset(var1, var2, this.isPath, this.path, this.grass, this.random, false);
   }

   public boolean isPath() {
      return this.isPath;
   }

   public void addHumanMob(int var1, int var2, float var3, String... var4) {
      this.addMob((var3x) -> {
         return this.random.getChance(var3) && var4.length > 0 ? (HumanMob)MobRegistry.getMob((String)this.random.getOneOf((Object[])var4), var3x) : null;
      }, var1, var2, (var1x) -> {
         var1x.setSettlerSeed(this.random.nextInt());
         var1x.setHome(var1x.getX() / 32, var1x.getY() / 32);
      });
   }

   public void addHumanMob(int var1, int var2, String... var3) {
      this.addHumanMob(var1, var2, 1.0F, var3);
   }

   public int getRandomSeed(GameRandom var1) {
      return (Integer)var1.getOneOf(() -> {
         return ObjectRegistry.getObjectID("sunflowerseed");
      }, () -> {
         return ObjectRegistry.getObjectID("firemoneseed");
      }, () -> {
         return ObjectRegistry.getObjectID("iceblossomseed");
      }, () -> {
         return ObjectRegistry.getObjectID("mushroom");
      }, () -> {
         return ObjectRegistry.getObjectID("wheatseed");
      }, () -> {
         return ObjectRegistry.getObjectID("cornseed");
      }, () -> {
         return ObjectRegistry.getObjectID("tomatoseed");
      }, () -> {
         return ObjectRegistry.getObjectID("chilipepperseed");
      }, () -> {
         return ObjectRegistry.getObjectID("sugarbeetseed");
      });
   }

   public void applyRandomSeedArea(int var1, int var2, int var3, int var4, GameRandom var5) {
      this.addCustomApplyRect(var1, var2, var3, var4, 0, (var2x, var3x, var4x, var5x, var6, var7) -> {
         int var8 = this.getRandomSeed(var5);

         for(int var9 = var3x; var9 <= var5x; ++var9) {
            for(int var10 = var4x; var10 <= var6; ++var10) {
               var2x.setObject(var9, var10, var8);
            }
         }

         return null;
      });
   }

   public int getRandomFlower(GameRandom var1) {
      return (Integer)var1.getOneOf(() -> {
         return ObjectRegistry.getObjectID("sunflower");
      }, () -> {
         return ObjectRegistry.getObjectID("firemone");
      }, () -> {
         return ObjectRegistry.getObjectID("iceblossom");
      }, () -> {
         return ObjectRegistry.getObjectID("mushroomflower");
      });
   }

   public void applyRandomFlower(int var1, int var2, GameRandom var3) {
      this.addCustomApply(var1, var2, 0, (var2x, var3x, var4, var5) -> {
         int var6 = this.getRandomFlower(var3);
         var2x.setObject(var3x, var4, var6, var5);
         return null;
      });
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected ModularPreset newModularObject(int var1, int var2, int var3, int var4, int var5) {
      return this.newModularObject(var1, var2, var3, var4, var5);
   }
}
