package necesse.inventory.item.armorItem;

import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.BuffModifiers;

public class ArmorModifiers extends ModifierContainer {
   public ArmorModifiers(ModifierValue... var1) {
      super(BuffModifiers.LIST);
      ModifierValue[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ModifierValue var5 = var2[var4];
         var5.apply(this);
      }

   }

   public void onBeforeHit(Mob var1, MobBeforeHitEvent var2) {
   }

   public void onBeforeAttacked(Mob var1, MobBeforeHitEvent var2) {
   }

   public void onBeforeHitCalculated(Mob var1, MobBeforeHitCalculatedEvent var2) {
   }

   public void onBeforeAttackedCalculated(Mob var1, MobBeforeHitCalculatedEvent var2) {
   }

   public void onWasHitLogic(Mob var1, MobWasHitEvent var2) {
   }

   public void onHasAttackedLogic(Mob var1, MobWasHitEvent var2) {
   }

   public void serverTick(Mob var1) {
   }

   public void clientTick(Mob var1) {
   }

   public void tickEffect(Mob var1, boolean var2) {
   }
}
