package necesse.engine.modifiers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import necesse.engine.util.HashMapArrayList;

public abstract class ModifierManager<M extends ModifierContainer> {
   private final ModifierList list;
   private Object[] nonLimitedModifiers;
   private Object[] limitedModifiers;
   private ModifierContainerLimits[] limits;
   private HashSet<Integer> queryableModifiers;
   private HashMapArrayList<Modifier<?>, M> queryModifiers;

   public ModifierManager(ModifierList var1) {
      this.list = var1;
      this.nonLimitedModifiers = new Object[var1.getModifierCount()];
      this.limitedModifiers = new Object[var1.getModifierCount()];
      this.limits = new ModifierContainerLimits[var1.getModifierCount()];

      for(int var2 = 0; var2 < this.limits.length; ++var2) {
         this.limits[var2] = new ModifierContainerLimits();
      }

      this.queryableModifiers = new HashSet();
      this.queryModifiers = new HashMapArrayList();
   }

   protected void makeQueryable(Modifier var1) {
      this.queryableModifiers.add(var1.index);
   }

   protected Collection<M> queryContainers(Modifier var1) {
      return (Collection)this.queryModifiers.get(var1);
   }

   protected void updateModifiers() {
      for(int var1 = 0; var1 < this.nonLimitedModifiers.length; ++var1) {
         this.nonLimitedModifiers[var1] = this.list.getModifier(var1).defaultBuffManagerValue;
         this.limits[var1] = new ModifierContainerLimits();
      }

      HashMapArrayList var7 = new HashMapArrayList();
      Iterator var2 = this.getModifierContainers().iterator();

      while(var2.hasNext()) {
         ModifierContainer var3 = (ModifierContainer)var2.next();
         var3.onUpdate();
         Iterator var4 = var3.getNonDefaultModifierIndexes().iterator();

         Integer var5;
         Modifier var6;
         while(var4.hasNext()) {
            var5 = (Integer)var4.next();
            var6 = this.list.getModifier(var5);
            this.nonLimitedModifiers[var5] = var3.applyModifierUnlimited(var6, this.nonLimitedModifiers[var5]);
            if (this.queryableModifiers.contains(var5)) {
               var7.add(var6, var3);
            }
         }

         var4 = var3.getNonDefaultLimitIndexes().iterator();

         while(var4.hasNext()) {
            var5 = (Integer)var4.next();
            var6 = this.list.getModifier(var5);
            this.limits[var5].combine(var6, var3.getLimits(var6));
            if (this.queryableModifiers.contains(var5)) {
               var7.add(var6, var3);
            }
         }
      }

      ((Stream)this.getDefaultModifiers().sequential()).map((var0) -> {
         return var0;
      }).forEach((var1x) -> {
         this.nonLimitedModifiers[var1x.modifier.index] = var1x.modifier.appendManager(this.nonLimitedModifiers[var1x.modifier.index], var1x.value);
         this.limits[var1x.modifier.index].combine(var1x.modifier, var1x.limits);
      });

      for(int var8 = 0; var8 < this.limitedModifiers.length; ++var8) {
         this.limitedModifiers[var8] = this.limits[var8].applyModifierLimits(this.list.getModifier(var8), this.nonLimitedModifiers[var8]);
      }

      this.queryModifiers = var7;
   }

   protected abstract Iterable<? extends M> getModifierContainers();

   public <T> T getNonLimitedModifier(Modifier<T> var1) {
      if (var1.list != this.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         return this.nonLimitedModifiers[var1.index];
      }
   }

   public <T> T getModifier(Modifier<T> var1) {
      if (var1.list != this.list) {
         throw new IllegalArgumentException("Modifier is not part of list.");
      } else {
         return this.limitedModifiers[var1.index];
      }
   }

   public <T> ModifierContainerLimits<T> getLimits(Modifier<T> var1) {
      return this.limits[var1.index];
   }

   @SafeVarargs
   public final <T> T applyModifiers(Modifier<T> var1, T var2, ModifierValue<T>... var3) {
      Object var4 = var2;
      ModifierValue[] var5 = var3;
      int var6 = var3.length;

      int var7;
      ModifierValue var8;
      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var5[var7];
         var4 = var1.appendManager(var4, var8.value);
      }

      var4 = this.limits[var1.index].applyModifierLimits(var1, var4);
      var5 = var3;
      var6 = var3.length;

      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var5[var7];
         var4 = var8.limits.applyModifierLimits(var1, var4);
      }

      return var4;
   }

   @SafeVarargs
   public final <T> T getAndApplyModifiers(Modifier<T> var1, ModifierValue<T>... var2) {
      return this.applyModifiers(var1, this.getModifier(var1), var2);
   }

   @SafeVarargs
   public final <T> T getAndApplyModifiers(Modifier<T> var1, T... var2) {
      ModifierValue[] var3 = new ModifierValue[var2.length];

      for(int var4 = 0; var4 < var2.length; ++var4) {
         var3[var4] = new ModifierValue(var1, var2[var4]);
      }

      return this.applyModifiers(var1, this.getModifier(var1), var3);
   }

   public LinkedList<ModifierTooltip> getModifierTooltips() {
      return this.getModifierTooltips((ModifierManager)null);
   }

   public LinkedList<ModifierTooltip> getModifierTooltips(ModifierManager<M> var1) {
      LinkedList var2 = new LinkedList();
      Iterator var3 = this.list.iterator();

      while(var3.hasNext()) {
         Modifier var4 = (Modifier)var3.next();
         ModifierContainerLimits var5 = this.limits[var4.index];
         ModifierTooltip var6;
         if (var5.hasMin()) {
            var6 = var4.getMinTooltip(var5.min(), var1 != null ? var1.limits[var4.index].min() : null, this.getNonLimitedModifier(var4));
            if (var6 != null) {
               var2.add(var6);
            }
         }

         if (var5.hasMax()) {
            var6 = var4.getMaxTooltip(var5.max(), var1 != null ? var1.limits[var4.index].max() : null, this.getNonLimitedModifier(var4));
            if (var6 != null) {
               var2.add(var6);
            }
         }

         var6 = var4.getTooltip(this.getModifier(var4), var1 != null ? var1.getModifier(var4) : null, var4.defaultBuffManagerValue);
         if (var6 != null) {
            var2.add(var6);
         }
      }

      return var2;
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.empty();
   }
}
