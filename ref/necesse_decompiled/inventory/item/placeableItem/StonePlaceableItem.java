package necesse.inventory.item.placeableItem;

import necesse.engine.GameEvents;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketPlaceTile;
import necesse.engine.registries.TileRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class StonePlaceableItem extends PlaceableItem {
   public static String[][] gravelTiles = new String[][]{{"graveltile", "dirttile"}, {"sandgraveltile", "sandtile"}};

   public StonePlaceableItem(int var1) {
      super(var1, true);
      this.controllerIsTileBasedPlacing = true;
      this.addGlobalIngredient(new String[]{"anystone"});
      this.dropsAsMatDeathPenalty = true;
      this.setItemCategory(new String[]{"materials", "stone"});
      this.keyWords.add("stone");
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var2 >= 0 && var3 >= 0 && var2 < var1.width * 32 && var3 < var1.height * 32) {
         int var7 = var2 / 32;
         int var8 = var3 / 32;
         if (var1.isProtected(var7, var8)) {
            return "protected";
         } else if (var4.getPositionPoint().distance((double)(var7 * 32 + 16), (double)(var8 * 32 + 16)) > (double)this.getPlaceRange(var5, var4)) {
            return "outofrange";
         } else {
            GameTile var9 = var1.getTile(var7, var8);
            String[][] var10 = gravelTiles;
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               String[] var13 = var10[var12];

               for(int var14 = 1; var14 < var13.length; ++var14) {
                  if (var9.getStringID().equals(var13[var14])) {
                     return null;
                  }
               }
            }

            return "invalidtile";
         }
      } else {
         return "outsidelevel";
      }
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      int var7 = var2 / 32;
      int var8 = var3 / 32;
      ItemPlaceEvent var9 = new ItemPlaceEvent(var1, var7, var8, var4, var5);
      GameEvents.triggerEvent(var9);
      if (!var9.isPrevented()) {
         if (var1.isServer()) {
            GameTile var10 = var1.getTile(var7, var8);
            GameTile var11 = null;
            String[][] var12 = gravelTiles;
            int var13 = var12.length;

            for(int var14 = 0; var14 < var13; ++var14) {
               String[] var15 = var12[var14];

               for(int var16 = 1; var16 < var15.length; ++var16) {
                  if (var10.getStringID().equals(var15[var16])) {
                     var11 = TileRegistry.getTile(var15[0]);
                     break;
                  }
               }

               if (var11 != null) {
                  break;
               }
            }

            if (var11 == null) {
               var11 = TileRegistry.getTile(TileRegistry.gravelID);
            }

            var11.placeTile(var1, var7, var8);
            var1.getServer().network.sendToClientsWithTile(new PacketPlaceTile(var1, var4.getServerClient(), var11.getID(), var7, var8), var1, var7, var8);
            var4.getServerClient().newStats.tiles_placed.increment(1);
            var1.getLevelTile(var7, var8).checkAround();
            var1.getLevelObject(var7, var8).checkAround();
         }

         if (this.singleUse) {
            var5.setAmount(var5.getAmount() - 1);
         }
      }

      return var5;
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.BUILDING_SPEED);
   }
}
