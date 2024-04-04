package necesse.inventory.item.placeableItem.consumableItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelDir;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class TravelScrollItem extends ConsumableItem {
   public TravelScrollItem() {
      super(50, false);
      this.rarity = Item.Rarity.RARE;
      this.itemCooldownTime.setBaseValue(1000);
      this.worldDrawSize = 32;
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         ServerClient var7 = var4.getServerClient();
         PacketOpenContainer var8 = new PacketOpenContainer(ContainerRegistry.TRAVEL_SCROLL_CONTAINER, TravelContainer.getContainerContentPacket(var1.getServer(), var7, TravelDir.All));
         ContainerRegistry.openAndSendContainer(var7, var8);
      }

      return var5;
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      String var6 = super.canAttack(var1, var2, var3, var4, var5);
      if (var6 != null) {
         return var6;
      } else if (!var1.getIdentifier().isIslandPosition()) {
         return "notisland";
      } else {
         return !var4.buffManager.hasBuff("teleportsickness") ? null : "";
      }
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return var4.buffManager.hasBuff("teleportsickness") ? "teleportsickness" : null;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "consumetip"));
      var4.add(Localization.translate("itemtooltip", "traveltip"));
      return var4;
   }
}
