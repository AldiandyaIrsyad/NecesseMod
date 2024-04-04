package necesse.engine.modifiers;

import necesse.inventory.item.upgradeUtils.AbstractUpgradeValue;

public class ModifierContainerUpgradeLimits<T> {
   protected boolean hasMax;
   protected boolean hasMin;
   protected AbstractUpgradeValue<T> min;
   protected AbstractUpgradeValue<T> max;
   protected int minPriority = Integer.MIN_VALUE;
   protected int maxPriority = Integer.MIN_VALUE;

   public ModifierContainerUpgradeLimits() {
   }

   public boolean hasMax() {
      return this.hasMax;
   }

   public boolean hasMin() {
      return this.hasMin;
   }

   public AbstractUpgradeValue<T> min() {
      return this.min;
   }

   public AbstractUpgradeValue<T> max() {
      return this.max;
   }

   public ModifierContainerLimits<T> toLimits(float var1) {
      ModifierContainerLimits var2 = new ModifierContainerLimits();
      var2.hasMin = this.hasMin;
      var2.min = this.min.getValue(var1);
      var2.minPriority = this.minPriority;
      var2.hasMax = this.hasMax;
      var2.max = this.max.getValue(var1);
      var2.maxPriority = this.maxPriority;
      return var2;
   }
}
