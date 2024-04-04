package necesse.engine.modifiers;

import java.util.ArrayList;
import java.util.Collection;

public class ModifierValue<T> {
   public final Modifier<T> modifier;
   public final T value;
   public final ModifierContainerLimits<T> limits;

   public ModifierValue(Modifier<T> var1, T var2) {
      this.modifier = var1;
      this.value = var2;
      this.limits = new ModifierContainerLimits();
   }

   public ModifierValue(Modifier<T> var1) {
      this(var1, var1.defaultBuffValue);
   }

   public ModifierValue<T> min(T var1, int var2) {
      this.limits.min = var1;
      this.limits.hasMin = true;
      this.limits.minPriority = var2;
      return this;
   }

   public ModifierValue<T> min(T var1) {
      return this.min(var1, 0);
   }

   public ModifierValue<T> max(T var1, int var2) {
      this.limits.max = var1;
      this.limits.hasMax = true;
      this.limits.maxPriority = var2;
      return this;
   }

   public ModifierValue<T> max(T var1) {
      return this.max(var1, 0);
   }

   public void apply(ModifierContainer var1) {
      var1.setModifier(this.modifier, this.value);
      var1.addModifierLimits(this.modifier, this.limits);
   }

   public void add(ModifierContainer var1) {
      var1.addModifier(this.modifier, this.value);
      var1.addModifierLimits(this.modifier, this.limits);
   }

   public ModifierTooltip getManagerTooltip() {
      return this.modifier.getTooltip(this.value, this.modifier.defaultBuffManagerValue);
   }

   public ModifierTooltip getTooltip() {
      return this.modifier.getTooltip(this.value, this.modifier.defaultBuffValue);
   }

   public ModifierTooltip getMaxTooltip() {
      return !this.limits.hasMax ? null : this.modifier.getMaxTooltip(this.limits.max);
   }

   public ModifierTooltip getMinTooltip() {
      return !this.limits.hasMin ? null : this.modifier.getMinTooltip(this.limits.min);
   }

   public Collection<ModifierTooltip> getAllTooltips() {
      ArrayList var1 = new ArrayList(3);
      ModifierTooltip var2 = this.getTooltip();
      if (var2 != null) {
         var1.add(var2);
      }

      ModifierTooltip var3 = this.getMaxTooltip();
      if (var3 != null) {
         var1.add(var3);
      }

      ModifierTooltip var4 = this.getMinTooltip();
      if (var4 != null) {
         var1.add(var4);
      }

      return var1;
   }
}
