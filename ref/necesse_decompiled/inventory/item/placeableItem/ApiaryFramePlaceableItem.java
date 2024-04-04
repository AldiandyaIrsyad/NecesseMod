package necesse.inventory.item.placeableItem;

import necesse.engine.GameEvents;
import necesse.engine.Screen;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class ApiaryFramePlaceableItem extends PlaceableItem {
   public ApiaryFramePlaceableItem() {
      super(100, true);
      this.controllerIsTileBasedPlacing = true;
      this.rarity = Item.Rarity.COMMON;
      this.dropsAsMatDeathPenalty = true;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add((String)Localization.translate("itemtooltip", "apiaryframetip"), 300);
      return var4;
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
            ObjectEntity var9 = var1.entityManager.getObjectEntity(var7, var8);
            if (var9 instanceof AbstractBeeHiveObjectEntity) {
               AbstractBeeHiveObjectEntity var10 = (AbstractBeeHiveObjectEntity)var9;
               return !var10.canAddFrame() ? "maxframes" : null;
            } else {
               return "notapiary";
            }
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
            ObjectEntity var10 = var1.entityManager.getObjectEntity(var7, var8);
            if (var10 instanceof AbstractBeeHiveObjectEntity) {
               AbstractBeeHiveObjectEntity var11 = (AbstractBeeHiveObjectEntity)var10;
               var11.addFrame();
            }
         } else {
            Screen.playSound(GameResources.tap, SoundEffect.effect((float)(var7 * 32 + 16), (float)(var8 * 32 + 16)));
         }

         if (this.singleUse) {
            var5.setAmount(var5.getAmount() - 1);
         }
      }

      return var5;
   }
}
