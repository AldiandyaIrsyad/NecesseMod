package necesse.inventory.item.toolItem.miscToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.mobs.friendly.QueenBeeMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class NetToolItem extends SwordToolItem {
   public NetToolItem() {
      super(0);
      this.setItemCategory(new String[]{"equipment", "tools"});
      this.setItemCategory(ItemCategory.equipmentManager, (String[])null);
      this.keyWords.remove("sword");
      this.keyWords.add("tool");
      this.damageType = DamageTypeRegistry.TRUE;
      this.attackDamage.setBaseValue(100.0F);
      this.attackAnimTime.setBaseValue(300);
      this.attackRange.setBaseValue(50);
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return new LocalMessage("ui", "settlercantuseitem");
   }

   public boolean canHitMob(Mob var1, ToolItemEvent var2) {
      return var1 instanceof HoneyBeeMob || var1 instanceof QueenBeeMob;
   }

   public void hitMob(InventoryItem var1, ToolItemEvent var2, Level var3, Mob var4, Mob var5) {
      var4.remove(0.0F, 0.0F, var5, true);
   }

   public boolean canHitObject(LevelObject var1) {
      return false;
   }

   public boolean isEnchantable(InventoryItem var1) {
      return false;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "nettip"));
      return var4;
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
   }

   public String getCanBeUpgradedError(InventoryItem var1) {
      return Localization.translate("ui", "itemnotupgradable");
   }
}
