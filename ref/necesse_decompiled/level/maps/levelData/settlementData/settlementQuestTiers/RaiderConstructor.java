package necesse.level.maps.levelData.settlementData.settlementQuestTiers;

import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.RaiderMob;
import necesse.entity.mobs.hostile.HumanRaiderMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class RaiderConstructor {
   public final String weapon;
   public final String helmet;
   public final String chest;
   public final String boots;
   public final int health;
   public final int armor;
   public final int coinDropsMin;
   public final int coinDropsMax;

   public RaiderConstructor(String var1, String var2, String var3, String var4, int var5, int var6, int var7, int var8) {
      this.weapon = var1;
      this.helmet = var2;
      this.chest = var3;
      this.boots = var4;
      this.health = var5;
      this.armor = var6;
      this.coinDropsMin = var7;
      this.coinDropsMax = var8;
   }

   public RaiderMob getRaider(Level var1) {
      HumanRaiderMob var2 = (HumanRaiderMob)MobRegistry.getMob("humanraider", var1);
      if (this.weapon != null) {
         var2.weaponID = HumanRaiderMob.weaponRegistry.getObjectID(this.weapon);
      }

      if (this.helmet != null) {
         var2.helmet = new InventoryItem(this.helmet);
      }

      if (this.chest != null) {
         var2.chest = new InventoryItem(this.chest);
      }

      if (this.boots != null) {
         var2.boots = new InventoryItem(this.boots);
      }

      var2.setArmor(this.armor);
      var2.setMaxHealth(this.health);
      var2.setHealth(var2.getMaxHealth());
      var2.minCoinDrops = this.coinDropsMin;
      var2.maxCoinDrops = this.coinDropsMax;
      return var2;
   }
}
