package necesse.inventory.item.toolItem.miscToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketPlayObjectDamageSound;
import necesse.engine.network.server.ServerClient;
import necesse.entity.TileDamageResult;
import necesse.entity.TileDamageType;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.FruitBushObject;
import necesse.level.gameObject.SeedObject;
import necesse.level.maps.LevelObject;

public class FarmingScytheToolItem extends ToolDamageItem {
   public FarmingScytheToolItem() {
      super(0);
      this.setItemCategory(new String[]{"equipment", "tools"});
      this.setItemCategory(ItemCategory.equipmentManager, (String[])null);
      this.toolType = ToolType.NONE;
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(0.0F);
      this.attackRange.setBaseValue(80);
      this.width = 90.0F;
      this.attackXOffset = 10;
      this.attackYOffset = 10;
   }

   public boolean isEnchantable(InventoryItem var1) {
      return false;
   }

   public boolean canHitMob(Mob var1, ToolItemEvent var2) {
      return false;
   }

   public boolean canHitObject(LevelObject var1) {
      if (super.canHitObject(var1)) {
         return true;
      } else {
         return var1.object instanceof SeedObject ? ((SeedObject)var1.object).isLastStage() : var1.object instanceof FruitBushObject;
      }
   }

   public void hitObject(InventoryItem var1, LevelObject var2, Mob var3) {
      super.hitObject(var1, var2, var3);
      if (var2.object instanceof SeedObject) {
         if (((SeedObject)var2.object).isLastStage()) {
            ServerClient var4 = var3.isPlayer ? ((PlayerMob)var3).getServerClient() : null;
            TileDamageResult var5 = var2.level.entityManager.doDamage(var2.tileX, var2.tileY, var2.object.objectHealth, TileDamageType.Object, this.getToolTier(var1), var4);
            if (var5 != null && var5.addedDamage > 0) {
               var2.level.getServer().network.sendToClientsAt(new PacketPlayObjectDamageSound(var2.tileX, var2.tileY, var2.object.getID()), (ServerClient)var4);
            }
         }
      } else if (var2.object instanceof FruitBushObject) {
         ((FruitBushObject)var2.object).harvest(var2.level, var2.tileX, var2.tileY, var3);
      }

   }

   protected void addToolTooltips(ListGameTooltips var1) {
      var1.add(Localization.translate("itemtooltip", "farmingscythetip"));
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
   }

   public String getCanBeUpgradedError(InventoryItem var1) {
      return Localization.translate("ui", "itemnotupgradable");
   }
}
