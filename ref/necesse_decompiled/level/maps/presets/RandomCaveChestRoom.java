package necesse.level.maps.presets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TrialEntranceObjectEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.presets.set.ChestRoomSet;

public class RandomCaveChestRoom extends Preset {
   public RandomCaveChestRoom(GameRandom var1, LootTable var2, AtomicInteger var3, ChestRoomSet... var4) {
      super(7, 7);
      this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileData = [watertile, grasstile, sandtile, dirttile, rocktile, dungeonfloor, farmland, woodfloor, rockfloor, sandstonetile, swamprocktile, snowrocktile, lavatile, snowtile, icetile, stonepathtile, graveltile, sandbrick],\n\ttiles = [-1, 8, 8, 8, 8, 8, -1, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, -1, 8, 8, 8, 8, 8, -1],\n\tobjectData = [air, oaktree, oaksapling, pinetree, pinesapling, cactus, cactussapling, workstation, forge, carpentersbench, carpentersbench2, ironanvil, demonicworkstation, alchemytable, woodwall, wooddoor, wooddooropen, stonewall, stonedoor, stonedooropen, sandstonewall, sandstonedoor, sandstonedooropen, swampstonewall, swampstonedoor, swampstonedooropen, icewall, icedoor, icedooropen, brickwall, brickdoor, brickdooropen, dungeonwall, dungeondoor, dungeondooropen, woodfence, woodfencegate, woodfencegateopen, storagebox, barrel, demonchest, torch, walltorch, ironlamp, goldlamp, firechalice, firechalicer, sign, flowerpot, itemstand, armorstand, trainingdummy, wooddinnertable, wooddinnertable2, wooddesk, woodmodulartable, woodchair, dungeondinnertable, dungeondinnertable2, dungeonchair, golddinnertable, golddinnertable2, goldchair, woodbookshelf, dungeonbookshelf, woodbathtub, woodbathtubr, dungeonbathtub, dungeonbathtubr, bathtub, bathtubr, woodtoilet, dungeontoilet, woolcarpet, woolcarpetr, woolcarpetd, woolcarpetdr, leathercarpet, leathercarpetr, leathercarpetd, leathercarpetdr, woodbed, woodbed2, woodpressureplate, rockpressureplate, dungeonpressureplate, rocklever, rockleveractive, telepad, ledpanel, andgate, nandgate, orgate, norgate, xorgate, tflipflopgate, rslatchgate, timergate, buffergate, sensorgate, soundgate, stoneflametrap, dungeonflametrap, stonearrowtrap, dungeonarrowtrap, tnt, sunflowerseed4, sunflowerseed3, sunflowerseed2, sunflowerseed1, sunflowerseed, wildsunflower, sunflower, firemoneseed4, firemoneseed3, firemoneseed2, firemoneseed1, firemoneseed, wildfiremone, firemone, iceblossomseed4, iceblossomseed3, iceblossomseed2, iceblossomseed1, iceblossomseed, wildiceblossom, iceblossom, mushroomflower, mushroom4, mushroom3, mushroom2, mushroom1, mushroom, wildmushroom, compostbin, feedingtrough, feedingtrough2, surfacerock, surfacerockr, rock, ironorerock, copperorerock, goldorerock, sandstonerock, ironoresandstone, copperoresandstone, goldoresandstone, quartzsandstone, swamprock, ironoreswamp, copperoreswamp, goldoreswamp, snowrock, ironoresnow, copperoresnow, goldoresnow, clayrock, grass, swampgrass, snowpile3, snowpile2, snowpile1, snowpile0, ladderdown, ladderup, dungeonentrance, dungeonexit, coinstack, crate, snowcrate, swampcrate, vase, ancienttotem, settlementflag],\n\tobjects = [-1, 17, 17, 17, 17, 17, -1, 17, 17, 0, 0, 0, 17, 17, 17, 0, 0, 0, 0, 0, 17, 17, 0, 0, 0, 0, 0, 17, 17, 0, 0, 0, 0, 0, 17, 17, 17, 0, 0, 0, 17, 17, -1, 17, 17, 17, 17, 17, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      ChestRoomSet var5 = ChestRoomSet.stone;
      ChestRoomSet var6 = var4.length == 0 ? ChestRoomSet.stone : (ChestRoomSet)var1.getOneOf((Object[])var4);
      var6.replacePreset((ChestRoomSet)var5, this);
      this.addCustomApply(this.openingApply(var1, var2, var3, var6));
   }

   private Preset.CustomApply openingApply(final GameRandom var1, final LootTable var2, final AtomicInteger var3, final ChestRoomSet var4) {
      return new Preset.CustomApply() {
         public Preset.UndoLogic applyToLevel(Level var1x, int var2x, int var3x) {
            int var4x = var4.traps.isEmpty() ? -1 : (Integer)var1.getOneOf((List)var4.traps);
            boolean var5 = var4x != -1 && var1.getChance(0.9F);
            boolean var6 = var5 && var1.nextBoolean();
            ArrayList var7 = new ArrayList();
            Runnable var8 = () -> {
               var1x.setObject(var2x + 3, var3x + 3, var4.inventoryObject, 0);
               var1x.setObject(var2x + 3, var3x, var4.wallSet.doorClosed, 0);
               if (var5) {
                  RandomCaveChestRoom.this.placeTrap(var1x, var4.pressureplate, var4x, var2x + 3, var3x + 1, var6 ? 1 : 3, 2);
               }

            };
            Runnable var9 = () -> {
               var1x.setObject(var2x + 3, var3x + 3, var4.inventoryObject, 1);
               var1x.setObject(var2x + 6, var3x + 3, var4.wallSet.doorClosed, 1);
               if (var5) {
                  RandomCaveChestRoom.this.placeTrap(var1x, var4.pressureplate, var4x, var2x + 5, var3x + 3, var6 ? 0 : 2, 2);
               }

            };
            Runnable var10 = () -> {
               var1x.setObject(var2x + 3, var3x + 3, var4.inventoryObject, 2);
               var1x.setObject(var2x + 3, var3x + 6, var4.wallSet.doorClosed, 2);
               if (var5) {
                  RandomCaveChestRoom.this.placeTrap(var1x, var4.pressureplate, var4x, var2x + 3, var3x + 5, var6 ? 1 : 3, 2);
               }

            };
            Runnable var11 = () -> {
               var1x.setObject(var2x + 3, var3x + 3, var4.inventoryObject, 3);
               var1x.setObject(var2x, var3x + 3, var4.wallSet.doorClosed, 3);
               if (var5) {
                  RandomCaveChestRoom.this.placeTrap(var1x, var4.pressureplate, var4x, var2x + 1, var3x + 3, var6 ? 0 : 2, 2);
               }

            };
            if (!var1x.getObject(var2x + 3, var3x - 1).isSolid) {
               var7.add(var8);
            }

            if (!var1x.getObject(var2x + 7, var3x + 3).isSolid) {
               var7.add(var9);
            }

            if (!var1x.getObject(var2x + 3, var3x + 7).isSolid) {
               var7.add(var10);
            }

            if (!var1x.getObject(var2x - 1, var3x + 3).isSolid) {
               var7.add(var11);
            }

            if (var7.isEmpty()) {
               var1.runOneOf(var8, var9, var10, var11);
            } else {
               ((Runnable)var1.getOneOf((List)var7)).run();
            }

            if (var1.getChance(0.1F)) {
               int var12 = ObjectRegistry.getObjectID("trialentrance");
               var1x.setObject(var2x + 3, var3x + 3, var12);
               ObjectEntity var13 = var1x.entityManager.getObjectEntity(var2x + 3, var3x + 3);
               if (var13 instanceof TrialEntranceObjectEntity) {
                  for(int var14 = 0; var14 < 2; ++var14) {
                     ArrayList var15 = var2.getNewList(var1, (Float)var1x.buffManager.getModifier(LevelModifiers.LOOT), var3);
                     ((TrialEntranceObjectEntity)var13).addLootList(var15);
                  }
               }
            } else {
               var2.applyToLevel(var1, (Float)var1x.buffManager.getModifier(LevelModifiers.LOOT), var1x, var2x + 3, var3x + 3, var1x, var3);
            }

            return (var0, var1xx, var2xx) -> {
            };
         }

         public Preset.CustomApply mirrorX(int var1x) throws PresetMirrorException {
            return RandomCaveChestRoom.this.openingApply(var1, var2, var3, var4);
         }

         public Preset.CustomApply mirrorY(int var1x) throws PresetMirrorException {
            return RandomCaveChestRoom.this.openingApply(var1, var2, var3, var4);
         }

         public Preset.CustomApply rotate(PresetRotation var1x, int var2x, int var3x) throws PresetRotateException {
            return RandomCaveChestRoom.this.openingApply(var1, var2, var3, var4);
         }
      };
   }

   private void placeTrap(Level var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      var1.setObject(var4, var5, var2);
      var6 %= 4;
      int var8;
      if (var6 == 0) {
         var1.setObject(var4, var5 - var7, var3, 2);

         for(var8 = 0; var8 <= var7; ++var8) {
            var1.wireManager.setWire(var4, var5 - var8, 0, true);
         }
      } else if (var6 == 1) {
         var1.setObject(var4 + var7, var5, var3, 3);

         for(var8 = 0; var8 <= var7; ++var8) {
            var1.wireManager.setWire(var4 + var8, var5, 0, true);
         }
      } else if (var6 == 2) {
         var1.setObject(var4, var5 + var7, var3, 0);

         for(var8 = 0; var8 <= var7; ++var8) {
            var1.wireManager.setWire(var4, var5 + var8, 0, true);
         }
      } else {
         var1.setObject(var4 - var7, var5, var3, 1);

         for(var8 = 0; var8 <= var7; ++var8) {
            var1.wireManager.setWire(var4 - var8, var5, 0, true);
         }
      }

   }
}
