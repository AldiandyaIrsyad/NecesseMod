package necesse.inventory.enchants;

import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierList;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class ItemEnchantment extends ModifierContainer implements IDDataContainer {
   public final IDData idData = new IDData();
   private GameMessage displayName;
   private float enchantCostMod;

   public IDData getIDData() {
      return this.idData;
   }

   public ItemEnchantment(ModifierList var1, int var2) {
      super(var1);
      this.enchantCostMod = (float)var2 / 100.0F;
   }

   public void onEnchantmentRegistryClosed() {
   }

   public GameMessage getLocalization() {
      if (this.displayName == null) {
         this.displayName = new LocalMessage("enchantment", this.getStringID());
      }

      return this.displayName;
   }

   public String getDisplayName() {
      return this.getLocalization().translate();
   }

   public float getEnchantCostMod() {
      return this.enchantCostMod + 1.0F;
   }

   public GameTooltips getTooltips() {
      ListGameTooltips var1 = new ListGameTooltips();
      Stream var10000 = this.getModifierTooltips().stream().map((var0) -> {
         return var0.toTooltip(true);
      });
      Objects.requireNonNull(var1);
      var10000.forEach(var1::add);
      return var1;
   }
}
