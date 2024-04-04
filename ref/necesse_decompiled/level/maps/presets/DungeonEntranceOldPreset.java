package necesse.level.maps.presets;

import necesse.engine.registries.ObjectRegistry;

public class DungeonEntranceOldPreset extends Preset {
   public DungeonEntranceOldPreset() {
      super(17, 17);
      this.applyScript("PRESET{\n\ttileData = [watertile, grasstile, sandtile, dirttile, rocktile, dungeonfloor, farmland, woodfloor, rockfloor, sandstonetile, lavatile, snowtile, icetile, stonepathtile],\n\tobjectData = [air, oaktree, torch, forge, oaksapling, storagebox, workstation, ladderdown, ladderup, cactus, cactussapling, pinetree, pinesapling, null, null, null, null, null, null, null, rock, ironorerock, copperorerock, goldorerock, null, null, null, null, null, null, stonewall, stonedoor, stonedooropen, dungeonwall, dungeondoor, dungeondooropen, woodwall, wooddoor, wooddooropen, null, woodfence, grass, sign, firechalice, firechalicer, dungeonentrance, dungeonexit, wheatseed, wheatseed1, wheatseed2, wheatseed3, wheatseed4, bathtub, bathtubr, dungeondinnertable, dungeondinnertabler, dungeonchair, wooddinnertable, wooddinnertabler, woodchair, woodbookshelf, dungeonbookshelf, dungeonbathtub, dungeonbathtubr, dungeontoilet, null, woodtoilet, woodbathtub, woodbathtubr, sandstonerock, ironoresandstone, copperoresandstone, goldoresandstone, null, null, null, null, null, null, null, surfacerock, surfacerockr, woolcarpet, woolcarpetr, woolcarpetd, woolcarpetdr, leathercarpet, leathercarpetr, leathercarpetd, leathercarpetdr, carpentersbench, carpentersbenchr, ironanvil, sunflowerseed, sunflowerseed1, sunflowerseed2, sunflowerseed3, sunflowerseed4, wildsunflower, firemoneseed, firemoneseed1, firemoneseed2, firemoneseed3, firemoneseed4, wildfiremone, woodbed, woodbedd, wooddesk, woodmodulartable, clayrock, flowerpot, sunflower, firemone, demonicworkstation, brickwall, brickdoor, brickdooropen, barrel, demonchest, ironlamp, goldlamp, woodpressureplate, rockpressureplate, rocklever, rockleveractive, ledpanel, andgate, nandgate, orgate, norgate, xorgate, tflipflopgate, rslatchgate, timergate, stoneflametrap, dungeonflametrap, tnt, buffergate, stonearrowtrap, dungeonarrowtrap, iceblossomseed, iceblossomseed1, iceblossomseed2, iceblossomseed3, iceblossomseed4, wildiceblossom, iceblossom, dungeonpressureplate, telepad, sensorgate, mushroom, mushroom1, mushroom2, mushroom3, mushroom4, wildmushroom, mushroomflower, soundgate, alchemytable, coinstack, itemstand, armorstand, crate, settlementflag, goldchair, golddinnertable, golddinnertabler],\n\twidth = 17,\n\theight = 17,\n\ttiles = [1, 1, 1, 5, 5, 5, 5, 1, 1, 1, 5, 5, 5, 5, 1, 1, 1, 1, 1, 5, 5, 5, 5, 5, 5, 1, 5, 5, 5, 5, 5, 5, 1, 1, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 1, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 1, 5, 5, 5, 1, 1, 1, 1, 1, 5, 5, 5, 1, 1, 1],\n\tobjects = [0, 0, 0, 33, 33, 33, 33, 0, 0, 0, 33, 33, 33, 33, 0, 0, 0, 0, 0, 33, 33, 33, 33, 33, 33, 0, 33, 33, 33, 33, 33, 33, 0, 0, 0, 33, 33, 33, 0, 0, 33, 33, 33, 33, 33, 0, 0, 33, 33, 33, 0, 33, 33, 33, 0, 0, 0, 0, 33, 33, 33, 0, 0, 0, 0, 33, 33, 33, 33, 33, 0, 43, 44, 0, 0, 0, 33, 0, 0, 0, 43, 44, 0, 33, 33, 33, 33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33, 33, 33, 33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33, 33, 33, 33, 33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33, 33, 33, 0, 33, 33, 33, 0, 0, 0, 0, 45, 0, 0, 0, 0, 33, 33, 33, 0, 33, 33, 33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33, 33, 33, 33, 33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33, 33, 33, 33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33, 33, 33, 33, 0, 43, 44, 0, 0, 0, 0, 0, 0, 0, 43, 44, 0, 33, 33, 33, 33, 33, 0, 0, 33, 0, 0, 0, 0, 0, 33, 0, 0, 33, 33, 33, 0, 33, 33, 33, 0, 33, 33, 0, 0, 0, 33, 33, 0, 33, 33, 33, 0, 0, 0, 33, 33, 33, 33, 33, 0, 0, 0, 33, 33, 33, 33, 33, 0, 0, 0, 0, 0, 33, 33, 33, 0, 0, 0, 0, 0, 33, 33, 33, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      this.fillTile(0, 0, 3, 1, -1);
      this.fillTile(0, 1, 2, 1, -1);
      this.fillTile(0, 2, 1, 1, -1);
      this.fillObject(0, 0, 3, 1, -1);
      this.fillObject(0, 1, 2, 1, -1);
      this.fillObject(0, 2, 1, 1, -1);
      this.fillTile(14, 0, 3, 1, -1);
      this.fillTile(15, 1, 2, 1, -1);
      this.fillTile(16, 2, 1, 1, -1);
      this.fillObject(14, 0, 3, 1, -1);
      this.fillObject(15, 1, 2, 1, -1);
      this.fillObject(16, 2, 1, 1, -1);
      this.setTile(0, 8, -1);
      this.setTile(16, 8, -1);
      this.setObject(0, 8, -1);
      this.setObject(16, 8, -1);
      this.fillTile(0, 14, 1, 1, -1);
      this.fillTile(0, 15, 2, 1, -1);
      this.fillTile(0, 16, 3, 1, -1);
      this.fillObject(0, 14, 1, 1, -1);
      this.fillObject(0, 15, 2, 1, -1);
      this.fillObject(0, 16, 3, 1, -1);
      this.fillTile(16, 14, 1, 1, -1);
      this.fillTile(15, 15, 2, 1, -1);
      this.fillTile(14, 16, 3, 1, -1);
      this.fillObject(16, 14, 1, 1, -1);
      this.fillObject(15, 15, 2, 1, -1);
      this.fillObject(14, 16, 3, 1, -1);
      this.fillTile(6, 16, 5, 1, -1);
      this.fillObject(6, 16, 5, 1, -1);
      int var1 = ObjectRegistry.getObjectID("dungeonentrance");
      this.setObject(this.width / 2, this.height / 2, var1);
      this.addMob("vampire", 5, 5, false);
      this.addMob("vampire", 11, 5, false);
      this.addMob("vampire", 5, 11, false);
      this.addMob("vampire", 11, 11, false);
   }
}
