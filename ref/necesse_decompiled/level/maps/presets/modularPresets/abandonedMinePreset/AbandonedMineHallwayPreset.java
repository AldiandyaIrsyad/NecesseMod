package necesse.level.maps.presets.modularPresets.abandonedMinePreset;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;

public class AbandonedMineHallwayPreset extends AbandonedMinePreset {
   public AbandonedMineHallwayPreset(GameRandom var1, boolean var2, boolean var3, boolean var4, boolean var5) {
      super(1, 1, 7, 1, var1);
      this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileData = [watertile, grasstile, sandtile, dirttile, rocktile, dungeonfloor, farmland, woodfloor, rockfloor, sandstonetile, swamprocktile, snowrocktile, lavatile, snowtile, icetile, stonepathtile, graveltile, sandbrick],\n\ttiles = [8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8],\n\tobjectData = [air, oaktree, oaksapling, pinetree, pinesapling, cactus, cactussapling, workstation, forge, carpentersbench, carpentersbench2, ironanvil, demonicworkstation, alchemytable, advancedworkstation, advancedworkstation2, woodwall, wooddoor, wooddooropen, stonewall, stonedoor, stonedooropen, sandstonewall, sandstonedoor, sandstonedooropen, swampstonewall, swampstonedoor, swampstonedooropen, icewall, icedoor, icedooropen, brickwall, brickdoor, brickdooropen, dungeonwall, dungeondoor, dungeondooropen, woodfence, woodfencegate, woodfencegateopen, storagebox, barrel, demonchest, torch, walltorch, ironlamp, goldlamp, firechalice, firechalicer, sign, flowerpot, itemstand, armorstand, trainingdummy, wooddinnertable, wooddinnertable2, wooddesk, woodmodulartable, woodchair, dungeondinnertable, dungeondinnertable2, dungeonchair, golddinnertable, golddinnertable2, goldchair, woodbookshelf, dungeonbookshelf, woodbathtub, woodbathtubr, dungeonbathtub, dungeonbathtubr, bathtub, bathtubr, woodtoilet, dungeontoilet, woolcarpet, woolcarpetr, woolcarpetd, woolcarpetdr, leathercarpet, leathercarpetr, leathercarpetd, leathercarpetdr, woodbed, woodbed2, woodpressureplate, rockpressureplate, dungeonpressureplate, rocklever, rockleveractive, telepad, ledpanel, andgate, nandgate, orgate, norgate, xorgate, tflipflopgate, rslatchgate, timergate, buffergate, sensorgate, soundgate, stoneflametrap, dungeonflametrap, stonearrowtrap, dungeonarrowtrap, tnt, sunflowerseed4, sunflowerseed3, sunflowerseed2, sunflowerseed1, sunflowerseed, wildsunflower, sunflower, firemoneseed4, firemoneseed3, firemoneseed2, firemoneseed1, firemoneseed, wildfiremone, firemone, iceblossomseed4, iceblossomseed3, iceblossomseed2, iceblossomseed1, iceblossomseed, wildiceblossom, iceblossom, mushroomflower, mushroom4, mushroom3, mushroom2, mushroom1, mushroom, wildmushroom, compostbin, feedingtrough, feedingtrough2, surfacerock, surfacerockr, rock, ironorerock, copperorerock, goldorerock, tungstenorerock, lifequartzrock, sandstonerock, ironoresandstone, copperoresandstone, goldoresandstone, quartzsandstone, swamprock, ironoreswamp, copperoreswamp, goldoreswamp, snowrock, ironoresnow, copperoresnow, goldoresnow, clayrock, obsidianrock, grass, swampgrass, snowpile3, snowpile2, snowpile1, snowpile0, ladderdown, ladderup, dungeonentrance, dungeonexit, deepladderdown, deepladderup, coinstack, crate, snowcrate, swampcrate, vase, ancienttotem, settlementflag],\n\tobjects = [19, 19, 19, 19, 19, 19, 19, 19, 0, 0, 0, 0, 0, 19, 19, 0, 0, 0, 0, 0, 19, 19, 0, 0, 0, 0, 0, 19, 19, 0, 0, 0, 0, 0, 19, 19, 0, 0, 0, 0, 0, 19, 19, 19, 19, 19, 19, 19, 19],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      this.replaceObject(this.wall, 0);
      this.replaceObject("stonewall", "air");
      this.replaceTile(TileRegistry.stoneFloorID, TileRegistry.deepStoneFloorID);
      this.closeObject = this.wall;
      if (var2) {
         this.open(0, 0, 0);
      }

      if (var3) {
         this.open(0, 0, 1);
      }

      if (var4) {
         this.open(0, 0, 2);
      }

      if (var5) {
         this.open(0, 0, 3);
      }

      this.addSkeletonMiner(this.width / 2, this.height / 2, var1, 0.05F);
   }

   public void fillOpeningReal(Level var1, int var2, int var3, int var4, int var5, int var6) {
      super.fillOpeningRealSuper(var1, var2, var3, var4, var5, var6);
   }
}
