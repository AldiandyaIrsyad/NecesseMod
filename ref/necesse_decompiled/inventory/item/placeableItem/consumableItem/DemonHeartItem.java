package necesse.inventory.item.placeableItem.consumableItem;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class DemonHeartItem extends ConsumableItem {
   public DemonHeartItem() {
      super(1, true);
      this.rarity = Item.Rarity.UNIQUE;
      this.allowRightClickToConsume = true;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 60000;
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      var4.setMaxHealth(200);
      if (var1.isServer()) {
         var1.getServer().network.sendToAllClientsExcept(new PacketPlayerGeneral(var4.getServerClient()), var4.getServerClient());
      } else if (var1.isClient()) {
         Screen.playSound(GameResources.eat, SoundEffect.effect(var4));
      }

      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      return var5;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return var4.getMaxHealthFlat() >= 200 ? "incorrecthealth" : null;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "consumetip"));
      var4.add(Localization.translate("itemtooltip", "demonhearttip"));
      return var4;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotationInv(var5);
   }
}
