package necesse.inventory.item.placeableItem.consumableItem.spawnItems;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;

public class FlyingSpiritsSpawnItem extends ConsumableItem {
   public FlyingSpiritsSpawnItem() {
      super(1, true);
      this.itemCooldownTime.setBaseValue(2000);
      this.setItemCategory(new String[]{"consumable", "bossitems"});
      this.dropsAsMatDeathPenalty = true;
      this.keyWords.add("boss");
      this.rarity = Item.Rarity.LEGENDARY;
      this.worldDrawSize = 32;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1 instanceof IncursionLevel) {
         return null;
      } else if (var1.isCave) {
         return "notsurface";
      } else {
         return !var1.getWorldEntity().isNight() ? "notnight" : null;
      }
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         if (var1 instanceof IncursionLevel) {
            GameMessage var7 = ((IncursionLevel)var1).canSummonBoss("reaper");
            if (var7 != null) {
               if (var4 != null && var4.isServerClient()) {
                  var4.getServerClient().sendChatMessage(var7);
               }

               return var5;
            }
         }

         System.out.println("Flying Spirits has been summoned at " + var1.getIdentifier() + ".");
         Mob var8 = MobRegistry.getMob("sageandgrit", var1);
         var1.entityManager.addMob(var8, (float)var4.getX(), (float)var4.getY());
         var1.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", MobRegistry.getLocalization("grit"))), (Level)var1);
         var1.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", MobRegistry.getLocalization("sage"))), (Level)var1);
         if (var1 instanceof IncursionLevel) {
            ((IncursionLevel)var1).onBossSummoned(var8);
         }
      }

      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      return var5;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "dragonsoulstip"));
      return var4;
   }
}
