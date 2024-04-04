package necesse.engine.modifiers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.inventory.item.ItemStatTip;

public class ModifierTipsBuilder {
   private ModifierContainer values;
   private ModifierContainer lastValues;
   private boolean addModifiers;
   private boolean addLimits;
   private HashSet<Modifier> excludeModifiers = new HashSet();
   private HashSet<Modifier> excludeLimits = new HashSet();

   public ModifierTipsBuilder(ModifierContainer var1, boolean var2, boolean var3) {
      this.values = var1;
      this.addModifiers = var2;
      this.addLimits = var3;
   }

   public ModifierTipsBuilder addLastValues(ModifierContainer var1) {
      this.lastValues = var1;
      return this;
   }

   public ModifierTipsBuilder excludeModifiers(Modifier... var1) {
      this.excludeModifiers.addAll(Arrays.asList(var1));
      return this;
   }

   public ModifierTipsBuilder excludeLimits(Modifier... var1) {
      this.excludeLimits.addAll(Arrays.asList(var1));
      return this;
   }

   public ModifierTipsBuilder exclude(Modifier... var1) {
      this.excludeModifiers(var1);
      this.excludeLimits(var1);
      return this;
   }

   public LinkedList<ModifierTooltip> build() {
      return this.values.getModifierTooltips(this.lastValues, this.addModifiers, this.addLimits, this.excludeModifiers, this.excludeLimits);
   }

   public void buildToList(LinkedList<ModifierTooltip> var1) {
      this.values.addModifierTooltips(var1, this.lastValues, this.addModifiers, this.addLimits, this.excludeModifiers, this.excludeLimits);
   }

   public LinkedList<ItemStatTip> buildStatTips() {
      LinkedList var1 = new LinkedList();
      LinkedList var2 = this.values.getModifierTooltips(this.lastValues, this.addModifiers, this.addLimits, this.excludeModifiers, this.excludeLimits);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ModifierTooltip var4 = (ModifierTooltip)var3.next();
         var1.add(var4.tip);
      }

      return var1;
   }

   public void buildToStatList(LinkedList<ItemStatTip> var1) {
      LinkedList var2 = this.values.getModifierTooltips(this.lastValues, this.addModifiers, this.addLimits, this.excludeModifiers, this.excludeLimits);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ModifierTooltip var4 = (ModifierTooltip)var3.next();
         var1.add(var4.tip);
      }

   }
}
