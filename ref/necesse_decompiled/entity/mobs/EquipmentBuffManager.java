package necesse.entity.mobs;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.EquipmentActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.ArmorBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public abstract class EquipmentBuffManager {
   private Mob owner;
   private SetBonusBuff cosmeticEffectBuff;
   private ObjectValue<ActiveBuff, SetBonusBuff> setBonusBuff;
   private ArrayList<ActiveArmorBuff> armorBuffs = new ArrayList();
   private ArrayList<ActiveTrinketBuff> trinketBuffs = new ArrayList();
   private EquipmentActiveBuff equipmentBuff;

   public EquipmentBuffManager(Mob var1) {
      this.owner = var1;
   }

   public abstract InventoryItem getArmorItem(int var1);

   public abstract InventoryItem getCosmeticItem(int var1);

   public abstract ArrayList<InventoryItem> getTrinketItems();

   public void clientTickEffects() {
      if (this.cosmeticEffectBuff != null) {
         this.cosmeticEffectBuff.tickEffect((ActiveBuff)null, this.owner);
      } else if (this.setBonusBuff != null) {
         InventoryItem var1 = this.getCosmeticItem(0);
         InventoryItem var2 = this.getCosmeticItem(1);
         InventoryItem var3 = this.getCosmeticItem(2);
         if (var1 == null && var2 == null && var3 == null) {
            ((SetBonusBuff)this.setBonusBuff.value).tickEffect((ActiveBuff)this.setBonusBuff.object, this.owner);
         }
      }

      if (this.equipmentBuff != null) {
         this.equipmentBuff.tickEffects(this.owner);
      }

      Iterator var4 = this.trinketBuffs.iterator();

      while(true) {
         ActiveTrinketBuff var5;
         int var7;
         do {
            if (!var4.hasNext()) {
               var4 = this.armorBuffs.iterator();

               while(true) {
                  ActiveArmorBuff var6;
                  do {
                     if (!var4.hasNext()) {
                        return;
                     }

                     var6 = (ActiveArmorBuff)var4.next();
                  } while(var6 == null);

                  for(var7 = 0; var7 < var6.buffs.length; ++var7) {
                     var6.buffs[var7].tickEffect(var6.activeBuffs[var7], this.owner);
                  }
               }
            }

            var5 = (ActiveTrinketBuff)var4.next();
         } while(var5 == null);

         for(var7 = 0; var7 < var5.buffs.length; ++var7) {
            var5.buffs[var7].tickEffect(var5.activeBuffs[var7], this.owner);
         }
      }
   }

   public void updateEquipmentBuff() {
      this.owner.buffManager.removeBuff(BuffRegistry.EQUIPMENT_BUFF.getID(), false);
      this.equipmentBuff = new EquipmentActiveBuff(BuffRegistry.EQUIPMENT_BUFF, this.owner);
      Iterator var1 = this.armorBuffs.iterator();

      while(var1.hasNext()) {
         ActiveArmorBuff var2 = (ActiveArmorBuff)var1.next();
         if (var2.enchant != null) {
            this.equipmentBuff.addEquipmentEnchant(var2.enchant);
         }
      }

      var1 = this.trinketBuffs.iterator();

      while(var1.hasNext()) {
         ActiveTrinketBuff var5 = (ActiveTrinketBuff)var1.next();
         if (var5.enchant != null) {
            this.equipmentBuff.addEquipmentEnchant(var5.enchant);
         }
      }

      for(int var4 = 0; var4 < 3; ++var4) {
         InventoryItem var6 = this.getArmorItem(var4);
         InventoryItem var3 = this.getCosmeticItem(var4);
         this.equipmentBuff.addArmorBuff(this.owner, var6, var3);
      }

      this.owner.buffManager.addBuff(this.equipmentBuff, false);
   }

   public void updateAll() {
      this.updateArmorBuffs(false);
      this.updateCosmeticSetBonus(false);
      this.updateTrinketBuffs(false);
      this.updateEquipmentBuff();
   }

   public void updateArmorBuffs() {
      this.updateArmorBuffs(true);
   }

   private void updateArmorBuffs(boolean var1) {
      InventoryItem var2 = this.getArmorItem(0);
      InventoryItem var3 = this.getArmorItem(1);
      InventoryItem var4 = this.getArmorItem(2);
      SetBonusBuff var5 = null;
      if (var2 != null && var3 != null && var4 != null && var2.item.isArmorItem() && ((ArmorItem)var2.item).hasSet(var2, var3, var4)) {
         var5 = ((ArmorItem)var2.item).getSetBuff(var2, this.owner, false);
      }

      if (this.setBonusBuff != null) {
         this.owner.buffManager.removeBuff((Buff)this.setBonusBuff.value, false);
         this.setBonusBuff = null;
      }

      if (var5 != null) {
         ActiveBuff var6 = new ActiveBuff(var5, this.owner, 0, (Attacker)null);
         var6.getGndData().setItem("headItem", new GNDItemInventoryItem(var2));
         var6.getGndData().setItem("chestItem", new GNDItemInventoryItem(var3));
         var6.getGndData().setItem("feetItem", new GNDItemInventoryItem(var4));
         ((ArmorItem)var2.item).setupSetBuff(var2, var3, var4, this.owner, var6, false);
         this.setBonusBuff = new ObjectValue(this.owner.buffManager.addBuff(var6, false), var5);
         if (this.owner.isPlayer && this.owner.getLevel() != null && this.owner.isServer()) {
            ServerClient var7 = ((PlayerMob)this.owner).getServerClient();
            if (var7 != null) {
               var7.newStats.set_bonuses_worn.addSetBonusWorn(var5);
            }
         }
      }

      ActiveArmorBuff[] var13 = new ActiveArmorBuff[3];

      for(int var14 = 0; var14 < 3; ++var14) {
         ArmorBuff[] var8 = null;
         EquipmentItemEnchant var9 = null;
         InventoryItem var10 = this.getArmorItem(var14);
         if (var10 != null && var10.item.isArmorItem()) {
            ArmorItem var11 = (ArmorItem)var10.item;
            var8 = var11.getBuffs(var10);
            var9 = var11.getEnchantment(var10);
         }

         var13[var14] = var8 == null ? null : new ActiveArmorBuff(var10, var8, var9);
      }

      Iterator var15 = this.armorBuffs.iterator();

      int var23;
      while(var15.hasNext()) {
         ActiveArmorBuff var17 = (ActiveArmorBuff)var15.next();
         ArmorBuff[] var19 = var17.buffs;
         int var21 = var19.length;

         for(var23 = 0; var23 < var21; ++var23) {
            ArmorBuff var12 = var19[var23];
            this.owner.buffManager.removeBuff((Buff)var12, false);
         }
      }

      this.armorBuffs.clear();
      ActiveArmorBuff[] var16 = var13;
      int var18 = var13.length;

      for(int var20 = 0; var20 < var18; ++var20) {
         ActiveArmorBuff var22 = var16[var20];
         if (var22 != null) {
            this.armorBuffs.add(var22);

            for(var23 = 0; var23 < var22.buffs.length; ++var23) {
               ActiveBuff var24 = new ActiveBuff(var22.buffs[var23], this.owner, 0, (Attacker)null);
               var24.getGndData().setItem("armorItem", new GNDItemInventoryItem(var22.item));
               var22.activeBuffs[var23] = this.owner.buffManager.addBuff(var24, false);
            }
         }
      }

      if (var1) {
         this.updateEquipmentBuff();
      }

   }

   public void updateCosmeticSetBonus() {
      this.updateCosmeticSetBonus(true);
   }

   private void updateCosmeticSetBonus(boolean var1) {
      this.cosmeticEffectBuff = null;
      InventoryItem var2 = this.getCosmeticItem(0);
      InventoryItem var3 = this.getCosmeticItem(1);
      InventoryItem var4 = this.getCosmeticItem(2);
      if (var2 != null && var3 != null && var4 != null && var2.item.isArmorItem() && ((ArmorItem)var2.item).hasSet(var2, var3, var4)) {
         this.cosmeticEffectBuff = ((ArmorItem)var2.item).getSetBuff(var2, this.owner, true);
      }

      if (var1) {
         this.updateEquipmentBuff();
      }

   }

   public void updateTrinketBuffs() {
      this.updateTrinketBuffs(true);
   }

   private void updateTrinketBuffs(boolean var1) {
      ArrayList var2 = this.getTrinketItems();
      ActiveTrinketBuff[] var3 = new ActiveTrinketBuff[var2.size()];

      int var4;
      TrinketBuff[] var6;
      InventoryItem var8;
      for(var4 = var2.size() - 1; var4 >= 0; --var4) {
         ArrayList var5 = new ArrayList();
         var6 = null;
         EquipmentItemEnchant var7 = null;
         var8 = (InventoryItem)var2.get(var4);
         if (var8 != null && var8.item.isTrinketItem()) {
            TrinketItem var9 = (TrinketItem)var8.item;
            var6 = var9.getBuffs(var8);
            var7 = var9.getEnchantment(var8);

            for(int var10 = 0; var10 < var2.size(); ++var10) {
               if (var10 != var4) {
                  InventoryItem var11 = (InventoryItem)var2.get(var10);
                  if (var11 != null && (var3[var10] == null || var3[var10].disables.isEmpty()) && (var9.disabledBy(var11) || var9 == var11.item)) {
                     var5.add(var11.item.getID());
                  }
               }
            }
         }

         var3[var4] = var6 == null ? null : new ActiveTrinketBuff(var8, var6, var7, var5);
      }

      int var19;
      for(var4 = 0; var4 < var2.size(); ++var4) {
         InventoryItem var13 = (InventoryItem)var2.get(var4);
         if (var3[var4] != null && var13 != null && var3[var4].disables.isEmpty() && var13.item.isTrinketItem()) {
            TrinketItem var17 = (TrinketItem)var13.item;

            for(var19 = var2.size() - 1; var19 >= 0; --var19) {
               if (var19 != var4 && var3[var19] != null && var3[var19].disables.isEmpty()) {
                  var8 = (InventoryItem)var2.get(var19);
                  if (var8 != null && var17.disables(var8)) {
                     var3[var19].disables.add(var17.getID());
                  }
               }
            }
         }
      }

      Iterator var12 = this.trinketBuffs.iterator();

      int var20;
      while(var12.hasNext()) {
         ActiveTrinketBuff var15 = (ActiveTrinketBuff)var12.next();
         var6 = var15.buffs;
         var19 = var6.length;

         for(var20 = 0; var20 < var19; ++var20) {
            TrinketBuff var22 = var6[var20];
            this.owner.buffManager.removeBuff((Buff)var22, false);
         }
      }

      this.trinketBuffs.clear();
      ActiveTrinketBuff[] var14 = var3;
      int var16 = var3.length;

      for(int var18 = 0; var18 < var16; ++var18) {
         ActiveTrinketBuff var21 = var14[var18];
         if (var21 != null) {
            this.trinketBuffs.add(var21);
            if (var21.disables.isEmpty()) {
               for(var20 = 0; var20 < var21.buffs.length; ++var20) {
                  ActiveBuff var23 = new ActiveBuff(var21.buffs[var20], this.owner, 0, (Attacker)null);
                  var23.getGndData().setItem("trinketItem", new GNDItemInventoryItem(var21.item));
                  var21.activeBuffs[var20] = this.owner.buffManager.addBuff(var23, false);
               }
            }
         }
      }

      if (var1) {
         this.updateEquipmentBuff();
      }

   }

   public boolean hasSetBuff() {
      return this.setBonusBuff != null;
   }

   public ObjectValue<ActiveBuff, SetBonusBuff> getSetBonusBuff() {
      return this.setBonusBuff;
   }

   public ListGameTooltips getSetBonusBuffTooltip(GameBlackboard var1) {
      return this.setBonusBuff != null ? ((ActiveBuff)this.setBonusBuff.object).getTooltips(var1) : null;
   }

   public ArrayList<ActiveArmorBuff> getArmorBuffs() {
      return this.armorBuffs;
   }

   public ArrayList<ActiveTrinketBuff> getTrinketBuffs() {
      return this.trinketBuffs;
   }

   public ArrayList<Integer> getTrinketBuffDisables(InventoryItem var1) {
      return (ArrayList)this.trinketBuffs.stream().filter((var1x) -> {
         return var1x.item == var1;
      }).map((var0) -> {
         return var0.disables;
      }).findFirst().orElseGet(ArrayList::new);
   }
}
