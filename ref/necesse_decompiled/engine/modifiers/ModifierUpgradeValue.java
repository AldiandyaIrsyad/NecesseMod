package necesse.engine.modifiers;

import java.util.ArrayList;
import java.util.Collection;
import necesse.inventory.item.upgradeUtils.AbstractUpgradeValue;

public class ModifierUpgradeValue<T> {
   public final Modifier<T> modifier;
   public final AbstractUpgradeValue<T> value;
   public final ModifierContainerUpgradeLimits<T> limits;

   public ModifierUpgradeValue(Modifier<T> var1, AbstractUpgradeValue<T> var2) {
      this.modifier = var1;
      this.value = var2;
      this.limits = new ModifierContainerUpgradeLimits();
   }

   public ModifierUpgradeValue<T> min(AbstractUpgradeValue<T> var1, int var2) {
      this.limits.min = var1;
      this.limits.hasMin = true;
      this.limits.minPriority = var2;
      return this;
   }

   public ModifierUpgradeValue<T> min(AbstractUpgradeValue<T> var1) {
      return this.min((AbstractUpgradeValue)var1, 0);
   }

   public ModifierUpgradeValue<T> min(final T var1, int var2) {
      return this.min(new AbstractUpgradeValue<T>() {
         public T getValue(float var1x) {
            return var1;
         }
      }, var2);
   }

   public ModifierUpgradeValue<T> min(T var1) {
      return this.min((Object)var1, 0);
   }

   public ModifierUpgradeValue<T> max(AbstractUpgradeValue<T> var1, int var2) {
      this.limits.max = var1;
      this.limits.hasMax = true;
      this.limits.maxPriority = var2;
      return this;
   }

   public ModifierUpgradeValue<T> max(AbstractUpgradeValue<T> var1) {
      return this.max((AbstractUpgradeValue)var1, 0);
   }

   public ModifierUpgradeValue<T> max(final T var1, int var2) {
      return this.max(new AbstractUpgradeValue<T>() {
         public T getValue(float var1x) {
            return var1;
         }
      }, var2);
   }

   public ModifierUpgradeValue<T> max(T var1) {
      return this.max((Object)var1, 0);
   }

   public void apply(ModifierContainer var1, float var2) {
      var1.setModifier(this.modifier, this.value.getValue(var2));
      var1.addModifierLimits(this.modifier, this.limits.toLimits(var2));
   }

   public void add(ModifierContainer var1, float var2) {
      var1.addModifier(this.modifier, this.value.getValue(var2));
      var1.addModifierLimits(this.modifier, this.limits.toLimits(var2));
   }

   public ModifierTooltip getManagerTooltip(float var1) {
      return this.modifier.getTooltip(this.value.getValue(var1), this.modifier.defaultBuffManagerValue);
   }

   public ModifierTooltip getTooltip(float var1) {
      return this.modifier.getTooltip(this.value.getValue(var1), this.modifier.defaultBuffValue);
   }

   public ModifierTooltip getMaxTooltip(float var1) {
      return !this.limits.hasMax ? null : this.modifier.getMaxTooltip(this.limits.max.getValue(var1));
   }

   public ModifierTooltip getMinTooltip(float var1) {
      return !this.limits.hasMin ? null : this.modifier.getMinTooltip(this.limits.min.getValue(var1));
   }

   public Collection<ModifierTooltip> getAllTooltips(float var1) {
      ArrayList var2 = new ArrayList(3);
      ModifierTooltip var3 = this.getTooltip(var1);
      if (var3 != null) {
         var2.add(var3);
      }

      ModifierTooltip var4 = this.getMaxTooltip(var1);
      if (var4 != null) {
         var2.add(var4);
      }

      ModifierTooltip var5 = this.getMinTooltip(var1);
      if (var5 != null) {
         var2.add(var5);
      }

      return var2;
   }
}
