package necesse.engine.modifiers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class ModifierContainer {
   private final ModifierList list;
   private final Object[] modifiers;
   private final ModifierContainerLimits[] limits;
   private final HashSet<Integer> nonDefaultModifiers;
   private final HashSet<Integer> nonDefaultLimits;

   public ModifierContainer(ModifierList var1) {
      this.list = var1;
      this.modifiers = new Object[var1.getModifierCount()];
      this.limits = new ModifierContainerLimits[var1.getModifierCount()];

      for(int var2 = 0; var2 < this.limits.length; ++var2) {
         this.limits[var2] = new ModifierContainerLimits();
      }

      this.nonDefaultModifiers = new HashSet();
      this.nonDefaultLimits = new HashSet();
      this.resetDefaultModifiers();
   }

   public void resetDefaultModifiers() {
      for(int var1 = 0; var1 < this.modifiers.length; ++var1) {
         this.modifiers[var1] = this.list.getModifier(var1).defaultBuffValue;
      }

      this.nonDefaultModifiers.clear();
   }

   public <T> T applyModifierUnlimited(Modifier<T> var1, T var2) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         return var1.appendManager(var2, this.modifiers[var1.index], this.getStacks());
      }
   }

   public <T> T applyModifierLimited(Modifier<T> var1, T var2) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         return var1.finalLimit(var1.appendManager(var2, this.modifiers[var1.index], this.getStacks()));
      }
   }

   /** @deprecated */
   @Deprecated
   public <T> T applyModifier(Modifier<T> var1, T var2) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         return var1.finalLimit(var1.appendManager(var2, this.modifiers[var1.index], this.getStacks()));
      }
   }

   public int getStacks() {
      return 1;
   }

   public <T> void setModifier(Modifier<T> var1, T var2) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         this.modifiers[var1.index] = var2;
         if (var2 == var1.defaultBuffValue) {
            this.nonDefaultModifiers.remove(var1.index);
         } else {
            this.nonDefaultModifiers.add(var1.index);
         }

      }
   }

   public <T> T applyModifierLimits(Modifier<T> var1, T var2) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         return this.limits[var1.index].applyModifierLimits(var1, var2);
      }
   }

   public <T> void addModifier(Modifier<T> var1, T var2, int var3) {
      this.setModifier(var1, var1.appendManager(this.modifiers[var1.index], var2, var3));
   }

   public final <T> void addModifier(Modifier<T> var1, T var2) {
      this.addModifier(var1, var2, 1);
   }

   public <T> void addModifierLimits(Modifier<T> var1, ModifierContainerLimits<T> var2) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         this.limits[var1.index].combine(var1, var2);
         this.nonDefaultLimits.add(var1.index);
      }
   }

   public <T> void setMaxModifier(Modifier<T> var1, T var2, int var3) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         if (!this.limits[var1.index].hasMax) {
            this.nonDefaultLimits.add(var1.index);
         }

         this.limits[var1.index].max = var1.finalLimit(var2);
         this.limits[var1.index].hasMax = true;
         this.limits[var1.index].maxPriority = var3;
      }
   }

   public <T> void setMaxModifier(Modifier<T> var1, T var2) {
      this.setMaxModifier(var1, var2, 0);
   }

   public <T> void clearMaxModifier(Modifier<T> var1) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         this.nonDefaultLimits.remove(var1.index);
         this.limits[var1.index].hasMax = false;
      }
   }

   public <T> void setMinModifier(Modifier<T> var1, T var2, int var3) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         if (!this.limits[var1.index].hasMin) {
            this.nonDefaultLimits.add(var1.index);
         }

         this.limits[var1.index].min = var1.finalLimit(var2);
         this.limits[var1.index].hasMin = true;
         this.limits[var1.index].minPriority = var3;
      }
   }

   public <T> void setMinModifier(Modifier<T> var1, T var2) {
      this.setMinModifier(var1, var2, 0);
   }

   public <T> void clearMinModifier(Modifier<T> var1) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         this.nonDefaultLimits.remove(var1.index);
         this.limits[var1.index].hasMin = false;
      }
   }

   public <T> T getModifier(Modifier<T> var1) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         return this.modifiers[var1.index];
      }
   }

   public <T> ModifierContainerLimits<T> getLimits(Modifier<T> var1) {
      if (this.list != var1.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         return this.limits[var1.index];
      }
   }

   public Iterable<Integer> getNonDefaultModifierIndexes() {
      return this.nonDefaultModifiers;
   }

   public Iterable<Integer> getNonDefaultLimitIndexes() {
      return this.nonDefaultLimits;
   }

   public void onUpdate() {
   }

   public Modifier<?> getModifierByIndex(int var1) {
      return this.list.getModifier(var1);
   }

   public LinkedList<ModifierTooltip> getModifierTooltips() {
      return this.getModifierTooltips((ModifierContainer)null, true, true);
   }

   public LinkedList<ModifierTooltip> getModifierTooltips(ModifierContainer var1) {
      return this.getModifierTooltips(var1, true, true);
   }

   public LinkedList<ModifierTooltip> getModifierTooltips(ModifierContainer var1, boolean var2, boolean var3) {
      return this.getModifierTooltips(var1, var2, var3, (HashSet)null, (HashSet)null);
   }

   public ModifierTipsBuilder getModifierTooltipsBuilder(boolean var1, boolean var2) {
      return new ModifierTipsBuilder(this, var1, var2);
   }

   public LinkedList<ModifierTooltip> getModifierTooltips(ModifierContainer var1, boolean var2, boolean var3, HashSet<Modifier> var4, HashSet<Modifier> var5) {
      LinkedList var6 = new LinkedList();
      this.addModifierTooltips(var6, var1, var2, var3, var4, var5);
      return var6;
   }

   public void addModifierTooltips(LinkedList<ModifierTooltip> var1, ModifierContainer var2, boolean var3, boolean var4, HashSet<Modifier> var5, HashSet<Modifier> var6) {
      if (var3 || var4) {
         Iterator var7 = this.list.iterator();

         while(true) {
            Modifier var8;
            do {
               do {
                  if (!var7.hasNext()) {
                     return;
                  }

                  var8 = (Modifier)var7.next();
                  if (var3 && (var5 == null || !var5.contains(var8))) {
                     ModifierTooltip var9 = this.getModifierTooltip(var8, var2);
                     if (var9 != null) {
                        var1.add(var9);
                     }
                  }
               } while(!var4);
            } while(var6 != null && var6.contains(var8));

            ModifierContainerLimits var11 = this.limits[var8.index];
            ModifierTooltip var10;
            if (var11.hasMin()) {
               var10 = this.getMinLimitTooltip(var8, var2, false);
               if (var10 != null) {
                  var1.add(var10);
               }
            }

            if (var11.hasMax()) {
               var10 = this.getMaxLimitTooltip(var8, var2, false);
               if (var10 != null) {
                  var1.add(var10);
               }
            }
         }
      }
   }

   public ModifierTooltip getModifierTooltip(Modifier var1, ModifierContainer var2) {
      return var1.getTooltip(this.modifiers[var1.index], var2 != null ? var2.modifiers[var1.index] : null, var1.defaultBuffValue);
   }

   public ModifierTooltip getMinLimitTooltip(Modifier var1, ModifierContainer var2, boolean var3) {
      ModifierContainerLimits var4 = this.limits[var1.index];
      return !var4.hasMin() && !var3 ? null : var1.getMinTooltip(var4.min(), var2 != null ? var2.limits[var1.index].min() : null);
   }

   public ModifierTooltip getMaxLimitTooltip(Modifier var1, ModifierContainer var2, boolean var3) {
      ModifierContainerLimits var4 = this.limits[var1.index];
      return !var4.hasMax() && !var3 ? null : var1.getMaxTooltip(var4.max(), var2 != null ? var2.limits[var1.index].max() : null);
   }
}
