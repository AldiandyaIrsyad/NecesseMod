package necesse.inventory.item.trinketItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class CalmingMinersBouquetTrinketItem extends TrinketItem {
   public CalmingMinersBouquetTrinketItem() {
      super(Item.Rarity.RARE, 600);
   }

   public TrinketBuff[] getBuffs(InventoryItem var1) {
      return new TrinketBuff[]{(TrinketBuff)BuffRegistry.getBuff("calmingminersbouquettrinket")};
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotation(var5);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "calmingminersbouquetusetip"));
      return var4;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      InventoryItem var11 = new InventoryItem("minersbouquet", var6.getAmount());
      var11.setGndData(var6.getGndData().copy());
      var11.setLocked(var6.isLocked());
      return var11;
   }
}
