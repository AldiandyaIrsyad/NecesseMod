package necesse.level.maps.presets.modularPresets.dungeonPresets;

import necesse.engine.GameLog;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.presets.modularPresets.ModularPreset;

public class DungeonPreset extends ModularPreset {
   public final int wall;
   public final int floor;
   public final int dungeonChest;
   public final int door;
   public final int bookshelf;

   private DungeonPreset(int var1, int var2, GameRandom var3, boolean var4) {
      super(var1, var2, 15, 3, 2);
      this.wall = ObjectRegistry.getObjectID("dungeonwall");
      this.floor = TileRegistry.getTileID("dungeonfloor");
      this.dungeonChest = ObjectRegistry.getObjectID("dungeonchest");
      this.door = ObjectRegistry.getObjectID("dungeondoor");
      this.bookshelf = ObjectRegistry.getObjectID("dungeonbookshelf");
      this.openObject = 0;
      this.openTile = this.floor;
      this.closeObject = this.wall;
      this.closeTile = this.floor;
      if (var4) {
         this.fillTile(0, 0, this.width, this.height, this.floor);
         this.fillObject(0, 0, this.width, this.height, 0);
         this.boxObject(0, 0, this.width, this.height, this.wall);
         this.boxObject(1, 1, this.width - 2, this.height - 2, this.wall);
         this.onObjectApply((var2x, var3x, var4x, var5, var6) -> {
            if (var5 == this.bookshelf) {
               byte var7 = 8;
               byte var8 = 100;
               ObjectEntity var9 = var2x.entityManager.getObjectEntity(var3x, var4x);
               if (var9 != null && var9.implementsOEInventory()) {
                  Inventory var10 = ((OEInventory)var9).getInventory();

                  for(int var11 = 0; var11 < var10.getSize(); ++var11) {
                     int var12 = this.getBookCount(var3, 0, var7, var8);
                     if (var12 > 0) {
                        var10.setItem(var11, new InventoryItem("book", var12));
                     }
                  }
               } else {
                  GameLog.warn.println("Could not add books to dungeon bookshelf at " + var3x + ", " + var4x);
               }
            }

         });
      }

   }

   public DungeonPreset(int var1, int var2, GameRandom var3) {
      this(var1, var2, var3, true);
   }

   public DungeonPreset(int var1, int var2) {
      this(var1, var2, (GameRandom)null);
   }

   protected DungeonPreset newModularObject(int var1, int var2, int var3, int var4, int var5) {
      return new DungeonPreset(var1, var2, (GameRandom)null, false);
   }

   public void openLevel(Level var1, int var2, int var3, int var4, int var5, int var6, GameRandom var7, int var8) {
      super.openLevel(var1, var2, var3, var4, var5, var6, var7, var8);
      if (var7.getChance(10)) {
         int var9 = var2 * var8 + var4;
         int var10 = var3 * var8 + var5;
         int var11 = var8 - this.openingDepth;
         boolean var12 = var7.nextBoolean();
         int var13 = var8 / 2 + (var12 ? this.openingSize + 2 : -this.openingSize - 2) / 2;
         int var14 = ObjectRegistry.getObjectID((String)var7.getOneOf((Object[])("dungeonflametrap", "dungeonarrowtrap", "dungeonvoidtrap")));
         int var15 = ObjectRegistry.getObjectID("dungeonpressureplate");
         boolean[] var16 = new boolean[this.openingSize];
         int var17 = 0;
         byte var18 = 2;
         int var19 = var7.nextInt(this.openingSize);

         int var20;
         int var21;
         for(var20 = 0; var20 < this.openingSize; ++var20) {
            var21 = (var20 + var19) % this.openingSize;
            if (var17 <= 0 || var7.nextInt(var17) == 0) {
               var16[var21] = true;
               var17 += var18;
            }
         }

         var20 = var7.nextInt(4);
         int var22;
         int var23;
         if (var6 == 0) {
            var21 = var9 + var13;
            var22 = var10 + 1;
            var1.setObject(var21, var22, var14, var12 ? 3 : 1);
            var1.wireManager.setWire(var21, var22, var20, true);

            for(var23 = 0; var23 < this.openingSize; ++var23) {
               if (var16[var23]) {
                  var1.setObject(var21 + (var12 ? -var23 - 1 : var23 + 1), var22, var15);
               }

               var1.wireManager.setWire(var21 + (var12 ? -var23 - 1 : var23 + 1), var22, var20, true);
            }
         } else if (var6 == 1) {
            var21 = var9 + var11;
            var22 = var10 + var13;
            var1.setObject(var21, var22, var14, var12 ? 0 : 2);
            var1.wireManager.setWire(var21, var22, var20, true);

            for(var23 = 0; var23 < this.openingSize; ++var23) {
               if (var16[var23]) {
                  var1.setObject(var21, var22 + (var12 ? -var23 - 1 : var23 + 1), var15);
               }

               var1.wireManager.setWire(var21, var22 + (var12 ? -var23 - 1 : var23 + 1), var20, true);
            }
         } else if (var6 == 2) {
            var21 = var9 + var13;
            var22 = var10 + var11;
            var1.setObject(var21, var22, var14, var12 ? 3 : 1);
            var1.wireManager.setWire(var21, var22, var20, true);

            for(var23 = 0; var23 < this.openingSize; ++var23) {
               if (var16[var23]) {
                  var1.setObject(var21 + (var12 ? -var23 - 1 : var23 + 1), var22, var15);
               }

               var1.wireManager.setWire(var21 + (var12 ? -var23 - 1 : var23 + 1), var22, var20, true);
            }
         } else if (var6 == 3) {
            var21 = var9 + 1;
            var22 = var10 + var13;
            var1.setObject(var21, var22, var14, var12 ? 0 : 2);
            var1.wireManager.setWire(var21, var22, var20, true);

            for(var23 = 0; var23 < this.openingSize; ++var23) {
               if (var16[var23]) {
                  var1.setObject(var21, var22 + (var12 ? -var23 - 1 : var23 + 1), var15);
               }

               var1.wireManager.setWire(var21, var22 + (var12 ? -var23 - 1 : var23 + 1), var20, true);
            }
         }
      }

   }

   private int getBookCount(GameRandom var1, int var2, int var3, int var4) {
      boolean var5 = var1.getChance(var3);
      if (var2 >= var4) {
         return var4;
      } else {
         return var5 ? this.getBookCount(var1, var2 + 1, var3, var4) : var2;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected ModularPreset newModularObject(int var1, int var2, int var3, int var4, int var5) {
      return this.newModularObject(var1, var2, var3, var4, var5);
   }
}
