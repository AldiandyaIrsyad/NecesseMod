package necesse.entity.mobs.buffs;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.StatsBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class EquipmentActiveBuff extends ActiveBuff {
   private ArrayList<ArmorBuffHandler> armorBuffs = new ArrayList(3);

   public EquipmentActiveBuff(StatsBuff var1, Mob var2) {
      super((Buff)var1, var2, 0, (Attacker)null);
   }

   public void addEquipmentEnchant(EquipmentItemEnchant var1) {
      Iterator var2 = BuffModifiers.LIST.iterator();

      while(var2.hasNext()) {
         Modifier var3 = (Modifier)var2.next();
         this.addModifier(var3, var1.getModifier(var3));
         this.addModifierLimits(var3, var1.getLimits(var3));
      }

   }

   public void addArmorBuff(Mob var1, InventoryItem var2, InventoryItem var3) {
      this.armorBuffs.add(new ArmorBuffHandler(var1, var2, var3));
   }

   public void onBeforeHit(MobBeforeHitEvent var1) {
      super.onBeforeHit(var1);
      Iterator var2 = this.armorBuffs.iterator();

      while(var2.hasNext()) {
         ArmorBuffHandler var3 = (ArmorBuffHandler)var2.next();
         if (var3.modifiers != null) {
            var3.modifiers.onBeforeHit(this.owner, var1);
         }
      }

   }

   public void onBeforeAttacked(MobBeforeHitEvent var1) {
      super.onBeforeAttacked(var1);
      Iterator var2 = this.armorBuffs.iterator();

      while(var2.hasNext()) {
         ArmorBuffHandler var3 = (ArmorBuffHandler)var2.next();
         if (var3.modifiers != null) {
            var3.modifiers.onBeforeAttacked(this.owner, var1);
         }
      }

   }

   public void onBeforeHitCalculated(MobBeforeHitCalculatedEvent var1) {
      super.onBeforeHitCalculated(var1);
      Iterator var2 = this.armorBuffs.iterator();

      while(var2.hasNext()) {
         ArmorBuffHandler var3 = (ArmorBuffHandler)var2.next();
         if (var3.modifiers != null) {
            var3.modifiers.onBeforeHitCalculated(this.owner, var1);
         }
      }

   }

   public void onBeforeAttackedCalculated(MobBeforeHitCalculatedEvent var1) {
      super.onBeforeAttackedCalculated(var1);
      Iterator var2 = this.armorBuffs.iterator();

      while(var2.hasNext()) {
         ArmorBuffHandler var3 = (ArmorBuffHandler)var2.next();
         if (var3.modifiers != null) {
            var3.modifiers.onBeforeAttackedCalculated(this.owner, var1);
         }
      }

   }

   public void onWasHit(MobWasHitEvent var1) {
      super.onWasHit(var1);
      Iterator var2 = this.armorBuffs.iterator();

      while(var2.hasNext()) {
         ArmorBuffHandler var3 = (ArmorBuffHandler)var2.next();
         if (var3.modifiers != null) {
            var3.modifiers.onWasHitLogic(this.owner, var1);
         }
      }

   }

   public void onHasAttacked(MobWasHitEvent var1) {
      super.onHasAttacked(var1);
      Iterator var2 = this.armorBuffs.iterator();

      while(var2.hasNext()) {
         ArmorBuffHandler var3 = (ArmorBuffHandler)var2.next();
         if (var3.modifiers != null) {
            var3.modifiers.onHasAttackedLogic(this.owner, var1);
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      Iterator var1 = this.armorBuffs.iterator();

      while(var1.hasNext()) {
         ArmorBuffHandler var2 = (ArmorBuffHandler)var1.next();
         if (var2.modifiers != null) {
            var2.modifiers.serverTick(this.owner);
         }
      }

   }

   public void clientTick() {
      super.clientTick();
      Iterator var1 = this.armorBuffs.iterator();

      while(var1.hasNext()) {
         ArmorBuffHandler var2 = (ArmorBuffHandler)var1.next();
         if (var2.modifiers != null) {
            var2.modifiers.clientTick(this.owner);
         }
      }

   }

   public void tickEffects(Mob var1) {
      Iterator var2 = this.armorBuffs.iterator();

      while(var2.hasNext()) {
         ArmorBuffHandler var3 = (ArmorBuffHandler)var2.next();
         if (var3.effectModifier != null) {
            var3.effectModifier.tickEffect(var1, var3.effectModifierCosmetic);
         }
      }

   }

   private class ArmorBuffHandler {
      public ArmorModifiers modifiers;
      public ArmorModifiers effectModifier;
      public boolean effectModifierCosmetic;

      public ArmorBuffHandler(Mob var2, InventoryItem var3, InventoryItem var4) {
         ArmorItem var5;
         ArmorModifiers var6;
         if (var3 != null && var3.item.type == Item.Type.ARMOR) {
            var5 = (ArmorItem)var3.item;
            EquipmentActiveBuff.this.addModifier(BuffModifiers.ARMOR_FLAT, var5.getTotalArmorValue(var3, var2));
            var6 = var5.getArmorModifiers(var3, var2);
            if (var6 != null) {
               Iterator var7 = BuffModifiers.LIST.iterator();

               while(var7.hasNext()) {
                  Modifier var8 = (Modifier)var7.next();
                  EquipmentActiveBuff.this.addModifier(var8, var6.getModifier(var8));
                  EquipmentActiveBuff.this.addModifierLimits(var8, var6.getLimits(var8));
               }

               this.modifiers = var6;
               this.effectModifier = var6;
               this.effectModifierCosmetic = false;
            }
         }

         if (var4 != null && var4.item.type == Item.Type.ARMOR) {
            var5 = (ArmorItem)var4.item;
            var6 = var5.getArmorModifiers(var4, var2);
            if (var6 != null) {
               this.effectModifier = var6;
               this.effectModifierCosmetic = true;
            }
         }

      }
   }
}
