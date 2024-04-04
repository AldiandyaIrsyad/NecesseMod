package necesse.inventory.item.placeableItem.consumableItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketUpdateTrinketSlots;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class ChangeTrinketSlotsItem extends ConsumableItem {
   public int trinketSlots;

   public ChangeTrinketSlotsItem(int var1) {
      super(1, true);
      this.trinketSlots = var1;
      this.worldDrawSize = 32;
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         var4.getInv().trinkets.changeSize(this.trinketSlots);
         var4.equipmentBuffManager.updateTrinketBuffs();
         ServerClient var7 = var4.getServerClient();
         var7.closeContainer(false);
         var7.updateInventoryContainer();
         if (var7.achievementsLoaded()) {
            var7.achievements().MAGICAL_DROP.markCompleted(var7);
         }

         var1.getServer().network.sendToAllClients(new PacketUpdateTrinketSlots(var7));
      }

      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      return var5;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return var4.getInv().trinkets.getSize() >= this.trinketSlots ? "incorrectslots" : null;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "consumetip"));
      return var4;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotationInv(var5);
   }
}
