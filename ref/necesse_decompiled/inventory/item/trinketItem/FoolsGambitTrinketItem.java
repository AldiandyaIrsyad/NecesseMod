package necesse.inventory.item.trinketItem;

import java.util.Arrays;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;

public class FoolsGambitTrinketItem extends TrinketItem {
   private String[] buffStringIDs;

   public FoolsGambitTrinketItem(Item.Rarity var1, String[] var2, int var3) {
      super(var1, var3);
      this.buffStringIDs = var2;
   }

   public FoolsGambitTrinketItem(Item.Rarity var1, String var2, int var3) {
      this(var1, new String[]{var2}, var3);
   }

   public TrinketBuff[] getBuffs(InventoryItem var1) {
      return (TrinketBuff[])Arrays.stream(this.buffStringIDs).map((var0) -> {
         return (TrinketBuff)BuffRegistry.getBuff(var0);
      }).toArray((var0) -> {
         return new TrinketBuff[var0];
      });
   }

   public void addTrinketAbilityHotkeyTooltip(ListGameTooltips var1, InventoryItem var2) {
   }

   public boolean isAbilityTrinket(InventoryItem var1) {
      return true;
   }

   public String getInvalidInSlotError(Container var1, ContainerSlot var2, InventoryItem var3) {
      String var4 = super.getInvalidInSlotError(var1, var2, var3);
      if (var4 != null) {
         return var4;
      } else {
         return var2.getContainerIndex() == var1.CLIENT_TRINKET_ABILITY_SLOT ? null : Localization.translate("itemtooltip", "foolsgambiterrortip");
      }
   }
}
