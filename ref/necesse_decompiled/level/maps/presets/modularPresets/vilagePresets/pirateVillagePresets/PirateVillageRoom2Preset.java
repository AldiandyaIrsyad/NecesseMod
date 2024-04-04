package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;

public class PirateVillageRoom2Preset extends PirateVillagePreset {
   public PirateVillageRoom2Preset(GameRandom var1, AtomicInteger var2) {
      super(4, 4, false, var1);
      this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttileData = [watertile, grasstile, sandtile, swampgrasstile, mudtile, dirttile, rocktile, dungeonfloor, farmland, woodfloor, rockfloor, sandstonetile, swamprocktile, snowrocktile, lavatile, snowtile, icetile, stonepathtile, graveltile, sandbrick, deeprocktile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 9, 9, 9, 9, 9, 9, 9, -1, -1, -1, -1, -1, 9, 9, 9, 9, 9, 9, 9, -1, -1, -1, -1, -1, 9, 9, 9, 9, 9, 9, 9, -1, -1, -1, -1, 18, 9, 9, 9, 9, 9, 9, 9, -1, -1, -1, -1, -1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, -1, -1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, -1, -1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, -1, -1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, -1, -1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, -1, -1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, -1, -1, -1, -1, -1, -1, -1, -1, 18, -1, -1, -1, -1],\n\tobjectData = [air, oaktree, oaksapling, sprucetree, sprucesapling, pinetree, pinesapling, palmtree, palmsapling, willowtree, willowsapling, cactus, cactussapling, workstation, forge, carpentersbench, carpentersbench2, ironanvil, demonicworkstation, alchemytable, advancedworkstation, advancedworkstation2, woodwall, wooddoor, wooddooropen, stonewall, stonedoor, stonedooropen, sandstonewall, sandstonedoor, sandstonedooropen, swampstonewall, swampstonedoor, swampstonedooropen, icewall, icedoor, icedooropen, brickwall, brickdoor, brickdooropen, dungeonwall, dungeondoor, dungeondooropen, deepstonewall, deepstonedoor, deepstonedooropen, obsidianwall, obsidiandoor, obsidiandooropen, woodfence, woodfencegate, woodfencegateopen, storagebox, barrel, demonchest, torch, walltorch, ironlamp, goldlamp, firechalice, firechalicer, sign, flowerpot, itemstand, armorstand, trainingdummy, wooddinnertable, wooddinnertable2, wooddesk, woodmodulartable, woodchair, dungeondinnertable, dungeondinnertable2, dungeonchair, golddinnertable, golddinnertable2, goldchair, woodbookshelf, dungeonbookshelf, woodbathtub, woodbathtubr, dungeonbathtub, dungeonbathtubr, woodtoilet, dungeontoilet, woolcarpet, woolcarpetr, woolcarpetd, woolcarpetdr, leathercarpet, leathercarpetr, leathercarpetd, leathercarpetdr, woodbed, woodbed2, woodpressureplate, rockpressureplate, dungeonpressureplate, rocklever, rockleveractive, telepad, ledpanel, andgate, nandgate, orgate, norgate, xorgate, tflipflopgate, rslatchgate, timergate, buffergate, sensorgate, soundgate, stoneflametrap, dungeonflametrap, stonearrowtrap, dungeonarrowtrap, tnt, sunflowerseed4, sunflowerseed3, sunflowerseed2, sunflowerseed1, sunflowerseed, wildsunflower, sunflower, firemoneseed4, firemoneseed3, firemoneseed2, firemoneseed1, firemoneseed, wildfiremone, firemone, iceblossomseed4, iceblossomseed3, iceblossomseed2, iceblossomseed1, iceblossomseed, wildiceblossom, iceblossom, mushroomflower, mushroom4, mushroom3, mushroom2, mushroom1, mushroom, wildmushroom, compostbin, feedingtrough, feedingtrough2, surfacerock, surfacerockr, sandsurfacerock, sandsurfacerockr, surfacerocksmall, sandsurfacerocksmall, rock, ironorerock, copperorerock, goldorerock, tungstenorerock, lifequartzrock, sandstonerock, ironoresandstone, copperoresandstone, goldoresandstone, quartzsandstone, swamprock, ironoreswamp, copperoreswamp, goldoreswamp, snowrock, ironoresnow, copperoresnow, goldoresnow, clayrock, deeprock, ironoredeeprock, copperoredeeprock, goldoredeeprock, tungstenoredeeprock, lifequartzdeeprock, obsidianrock, grass, swampgrass, snowpile3, snowpile2, snowpile1, snowpile0, ladderdown, ladderup, dungeonentrance, dungeonexit, deepladderdown, deepladderup, coinstack, crate, snowcrate, swampcrate, vase, ancienttotem, settlementflag],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22, 22, 22, 22, 22, 22, 22, 0, 0, 0, 0, 0, 22, 58, 64, 52, 17, 68, 22, 0, 0, 0, 0, 0, 22, 0, 89, 90, 0, 70, 22, 0, 0, 0, 0, 0, 23, 0, 91, 92, 0, 0, 22, 0, 0, 0, 0, 0, 22, 0, 194, 0, 0, 0, 22, 22, 22, 22, 0, 0, 22, 0, 0, 0, 0, 194, 76, 76, 124, 22, 0, 0, 22, 0, 0, 0, 0, 0, 74, 75, 0, 22, 0, 0, 22, 0, 0, 0, 0, 0, 76, 76, 0, 22, 0, 0, 22, 77, 77, 58, 19, 0, 0, 0, 0, 22, 0, 0, 22, 22, 22, 22, 22, 22, 23, 22, 22, 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      this.applyRandomCoinStack(3, 5, var1);
      this.applyRandomCoinStack(6, 6, var1);
      this.applyRandomCoinStack(9, 9, var1);
      this.applyRandomFlower(9, 6, var1);
      this.addInventory(LootTablePresets.pirateChest, var1, 4, 2, new Object[]{var2});
      this.open(0, 1, 3);
      this.open(2, 3, 2);
   }
}