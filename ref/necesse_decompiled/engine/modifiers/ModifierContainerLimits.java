package necesse.engine.modifiers;

public class ModifierContainerLimits<T> {
   protected boolean hasMax;
   protected boolean hasMin;
   protected T min;
   protected T max;
   protected int minPriority = Integer.MIN_VALUE;
   protected int maxPriority = Integer.MIN_VALUE;

   public ModifierContainerLimits() {
   }

   public boolean hasMax() {
      return this.hasMax;
   }

   public boolean hasMin() {
      return this.hasMin;
   }

   public T min() {
      return this.min;
   }

   public T max() {
      return this.max;
   }

   public void combine(Modifier<T> var1, ModifierContainerLimits<T> var2) {
      if (var2.hasMin()) {
         if (this.hasMin()) {
            this.min = var1.max(this.min(), var2.min());
         } else {
            this.min = var2.min();
         }

         this.minPriority = Math.max(this.minPriority, var2.minPriority);
         this.hasMin = true;
      }

      if (var2.hasMax()) {
         if (this.hasMax()) {
            this.max = var1.min(this.max(), var2.max());
         } else {
            this.max = var2.max();
         }

         this.maxPriority = Math.max(this.maxPriority, var2.maxPriority);
         this.hasMax = true;
      }

      if (this.hasMin() && this.hasMax()) {
         if (this.minPriority > this.maxPriority) {
            this.max = var1.max(this.max(), this.min());
         } else {
            this.min = var1.min(this.max(), this.min());
         }
      }

   }

   public T applyModifierLimits(Modifier<T> var1, T var2) {
      Object var3 = var2;
      if (this.hasMin()) {
         var3 = var1.max(var2, this.min());
      }

      if (this.hasMax()) {
         var3 = var1.min(var3, this.max());
      }

      return var1.finalLimit(var3);
   }
}
