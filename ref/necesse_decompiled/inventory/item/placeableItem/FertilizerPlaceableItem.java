package necesse.inventory.item.placeableItem;

import necesse.engine.GameEvents;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SeedObjectEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class FertilizerPlaceableItem extends PlaceableItem {
   public FertilizerPlaceableItem() {
      super(100, true);
      this.controllerIsTileBasedPlacing = true;
      this.rarity = Item.Rarity.COMMON;
      this.dropsAsMatDeathPenalty = true;
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.BUILDING_SPEED);
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var2 >= 0 && var3 >= 0 && var2 < var1.width * 32 && var3 < var1.height * 32) {
         int var7 = var2 / 32;
         int var8 = var3 / 32;
         if (var1.isProtected(var7, var8)) {
            return "protected";
         } else if (!var1.getObject(var7, var8).isSeed) {
            return "notseed";
         } else if (var4.getPositionPoint().distance((double)(var7 * 32 + 16), (double)(var8 * 32 + 16)) > 100.0) {
            return "outofrange";
         } else {
            ObjectEntity var9 = var1.entityManager.getObjectEntity(var7, var8);
            if (var9 instanceof SeedObjectEntity) {
               return ((SeedObjectEntity)var9).isFertilized() ? "alreadyfertilized" : null;
            } else {
               return "notseed";
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
            if (var10 instanceof SeedObjectEntity) {
               ((SeedObjectEntity)var10).fertilize();
            }
         }

         if (this.singleUse) {
            var5.setAmount(var5.getAmount() - 1);
         }
      }

      return var5;
   }

   public InventoryItem onAttemptPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, String var7) {
      return var5;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "fertilizertip"));
      return var4;
   }
}
