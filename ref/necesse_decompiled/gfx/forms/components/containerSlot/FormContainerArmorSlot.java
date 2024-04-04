package necesse.gfx.forms.components.containerSlot;

import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.item.armorItem.ArmorItem;

public abstract class FormContainerArmorSlot extends FormContainerSlot {
   private ArmorItem.ArmorType armorType;
   private boolean isCosmetic;

   public FormContainerArmorSlot(Client var1, Container var2, int var3, int var4, int var5, ArmorItem.ArmorType var6, boolean var7) {
      super(var1, var2, var3, var4, var5);
      this.armorType = var6;
      this.isCosmetic = var7;
      if (var6 == ArmorItem.ArmorType.HEAD) {
         if (var7) {
            this.setDecal(Settings.UI.inventoryslot_icon_hat);
         } else {
            this.setDecal(Settings.UI.inventoryslot_icon_helmet);
         }
      }

      if (var6 == ArmorItem.ArmorType.CHEST) {
         if (var7) {
            this.setDecal(Settings.UI.inventoryslot_icon_shirt);
         } else {
            this.setDecal(Settings.UI.inventoryslot_icon_chestplate);
         }
      }

      if (var6 == ArmorItem.ArmorType.FEET) {
         if (var7) {
            this.setDecal(Settings.UI.inventoryslot_icon_shoes);
         } else {
            this.setDecal(Settings.UI.inventoryslot_icon_boots);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public FormContainerArmorSlot(Client var1, int var2, int var3, int var4, ArmorItem.ArmorType var5, boolean var6) {
      this(var1, (Container)null, var2, var3, var4, var5, var6);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      super.draw(var1, var2, var3);
      InventoryItem var4 = this.getContainerSlot().getItem();
      if (this.isCosmetic && var4 != null && var4.item instanceof ArmorItem && ((ArmorItem)var4.item).getTotalArmorValue(var4, var2) != 0) {
         Settings.UI.note_disabled.initDraw().draw(this.getX() + 5, this.getY() + 5);
      }

   }

   public GameTooltips getClearTooltips() {
      if (this.armorType == ArmorItem.ArmorType.HEAD) {
         return new StringTooltips(Localization.translate("itemtooltip", (this.isCosmetic ? "cosmetic" : "") + "headslot"));
      } else if (this.armorType == ArmorItem.ArmorType.CHEST) {
         return new StringTooltips(Localization.translate("itemtooltip", (this.isCosmetic ? "cosmetic" : "") + "chestslot"));
      } else {
         return this.armorType == ArmorItem.ArmorType.FEET ? new StringTooltips(Localization.translate("itemtooltip", (this.isCosmetic ? "cosmetic" : "") + "feetslot")) : null;
      }
   }

   public GameTooltips getItemTooltip(InventoryItem var1, PlayerMob var2) {
      GameBlackboard var3 = (new GameBlackboard()).set("isCosmeticSlot", this.isCosmetic).set("equippedMob", this.getEquippedMob(var2)).set("setBonus", this.getSetBonusTooltips(new GameBlackboard()));
      return var1.item.getTooltips(var1, var2, var3);
   }

   public abstract Mob getEquippedMob(PlayerMob var1);

   public abstract ListGameTooltips getSetBonusTooltips(GameBlackboard var1);
}
