package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import java.awt.Point;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.entity.mobs.hostile.pirates.PirateCaptainMob;

public class PirateVillageBossPreset extends PirateVillagePreset {
   public PirateVillageBossPreset(GameRandom var1) {
      super(3, 3, false, var1);
      this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 9,\n\ttileData = [watertile, grasstile, sandtile, dirttile, rocktile, dungeonfloor, farmland, woodfloor, rockfloor, sandstonetile, swamprocktile, snowrocktile, lavatile, snowtile, icetile, stonepathtile, graveltile, sandbrick],\n\ttiles = [7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7],\n\tobjectData = [air, oaktree, oaksapling, pinetree, pinesapling, cactus, cactussapling, workstation, forge, carpentersbench, carpentersbench2, ironanvil, demonicworkstation, alchemytable, woodwall, wooddoor, wooddooropen, stonewall, stonedoor, stonedooropen, sandstonewall, sandstonedoor, sandstonedooropen, swampstonewall, swampstonedoor, swampstonedooropen, icewall, icedoor, icedooropen, brickwall, brickdoor, brickdooropen, dungeonwall, dungeondoor, dungeondooropen, woodfence, woodfencegate, woodfencegateopen, storagebox, barrel, demonchest, torch, walltorch, ironlamp, goldlamp, firechalice, firechalicer, sign, flowerpot, itemstand, armorstand, trainingdummy, wooddinnertable, wooddinnertable2, wooddesk, woodmodulartable, woodchair, dungeondinnertable, dungeondinnertable2, dungeonchair, golddinnertable, golddinnertable2, goldchair, woodbookshelf, dungeonbookshelf, woodbathtub, woodbathtubr, dungeonbathtub, dungeonbathtubr, bathtub, bathtubr, woodtoilet, dungeontoilet, woolcarpet, woolcarpetr, woolcarpetd, woolcarpetdr, leathercarpet, leathercarpetr, leathercarpetd, leathercarpetdr, woodbed, woodbed2, woodpressureplate, rockpressureplate, dungeonpressureplate, rocklever, rockleveractive, telepad, ledpanel, andgate, nandgate, orgate, norgate, xorgate, tflipflopgate, rslatchgate, timergate, buffergate, sensorgate, soundgate, stoneflametrap, dungeonflametrap, stonearrowtrap, dungeonarrowtrap, tnt, sunflowerseed4, sunflowerseed3, sunflowerseed2, sunflowerseed1, sunflowerseed, wildsunflower, sunflower, firemoneseed4, firemoneseed3, firemoneseed2, firemoneseed1, firemoneseed, wildfiremone, firemone, iceblossomseed4, iceblossomseed3, iceblossomseed2, iceblossomseed1, iceblossomseed, wildiceblossom, iceblossom, mushroomflower, mushroom4, mushroom3, mushroom2, mushroom1, mushroom, wildmushroom, compostbin, feedingtrough, feedingtrough2, surfacerock, surfacerockr, rock, ironorerock, copperorerock, goldorerock, sandstonerock, ironoresandstone, copperoresandstone, goldoresandstone, quartzsandstone, swamprock, ironoreswamp, copperoreswamp, goldoreswamp, snowrock, ironoresnow, copperoresnow, goldoresnow, clayrock, grass, swampgrass, snowpile3, snowpile2, snowpile1, snowpile0, ladderdown, ladderup, dungeonentrance, dungeonexit, coinstack, crate, snowcrate, swampcrate, vase, ancienttotem, settlementflag],\n\tobjects = [14, 14, 14, 0, 0, 0, 14, 14, 14, 14, 0, 0, 0, 0, 0, 0, 0, 14, 14, 0, 44, 0, 0, 0, 44, 0, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0, 44, 0, 0, 0, 44, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 14, 14, 14, 14, 0, 0, 0, 14, 14, 14],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      this.applyRandomCoinStack(3, 2, var1);
      this.applyRandomCoinStack(6, 4, var1);
      this.applyRandomCoinStack(4, 7, var1);
      this.applyRandomCoinStack(8, 6, var1);
      this.addCustomApply(4, 4, 0, (var0, var1x, var2x, var3) -> {
         PirateCaptainMob var4 = new PirateCaptainMob();
         var4.setLevel(var0);
         var0.entityManager.addMob(var4, (float)(var1x * 32 + 16), (float)(var2x * 32 + 16));
         var4.dropLadder = true;
         var4.canDespawn = false;
         return (var1, var2, var3x) -> {
            var4.remove();
         };
      });
      Point var2 = (Point)var1.getOneOf((Object[])(new Point(3, 3), new Point(5, 5), new Point(3, 5), new Point(5, 3)));
      this.addCustomApply(var2.x, var2.y, 0, (var0, var1x, var2x, var3) -> {
         StylistHumanMob var4 = (StylistHumanMob)MobRegistry.getMob("stylisthuman", var0);
         var4.setTrapped(true);
         var0.entityManager.addMob(var4, (float)(var1x * 32 + 16), (float)(var2x * 32 + 16));
         return (var1, var2, var3x) -> {
            var4.remove();
         };
      });
      this.open(1, 0, 0);
      this.open(2, 1, 1);
      this.open(1, 2, 2);
      this.open(0, 1, 3);
   }
}
