package necesse.inventory.item.placeableItem.consumableItem;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketUpdateTrinketSlots;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class TestChangeTrinketSlotsItem extends ConsumableItem {
   public int delta;

   public TestChangeTrinketSlotsItem(int var1) {
      super(100, true);
      this.delta = var1;
   }

   public GameMessage getNewLocalization() {
      return new StaticMessage((this.delta > 0 ? "+" : "") + this.delta + " trinket slots");
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         var4.getInv().trinkets.changeSize(var4.getInv().trinkets.getSize() + this.delta);
         var4.equipmentBuffManager.updateTrinketBuffs();
         ServerClient var7 = var4.getServerClient();
         var7.closeContainer(false);
         var7.updateInventoryContainer();
         var1.getServer().network.sendToAllClients(new PacketUpdateTrinketSlots(var7));
      }

      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      return var5;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return var4.getInv().trinkets.getSize() + this.delta < 0 ? "incorrectslots" : null;
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
