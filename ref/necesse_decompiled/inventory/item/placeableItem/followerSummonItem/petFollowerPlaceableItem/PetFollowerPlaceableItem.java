package necesse.inventory.item.placeableItem.followerSummonItem.petFollowerPlaceableItem;

import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.followerSummonItem.FollowerSummonPlaceableItem;

public class PetFollowerPlaceableItem extends FollowerSummonPlaceableItem {
   public PetFollowerPlaceableItem(String var1, Item.Rarity var2) {
      super(1, false, var1, FollowPosition.WALK_CLOSE, "summonedpet", "summonedpet", 1);
      this.rarity = var2;
      this.setItemCategory(new String[]{"misc", "pets"});
      this.keyWords.add("pet");
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return () -> {
         if (var1.getClient().isServer()) {
            ServerClient var3 = var1.getClient().getServerClient();
            Packet var4 = this.getSummonContent();
            if (var4 == null) {
               var4 = new Packet();
            }

            if (this.canSummon(var3.getLevel(), var3.playerMob, var2, new PacketReader(var4)) == null) {
               this.summonMob(var3.getLevel(), var3.playerMob, var2, new PacketReader(var4));
            }
         }

         return new ContainerActionResult(42965565);
      };
   }

   protected ListGameTooltips getAnimalTooltips(InventoryItem var1, Attacker var2) {
      ListGameTooltips var3 = super.getAnimalTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "summonquicktip"));
      return var3;
   }
}
