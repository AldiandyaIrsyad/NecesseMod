package necesse.inventory.item.upgradeUtils;

import java.util.Map;
import java.util.TreeMap;
import necesse.engine.util.GameMath;

public class IntUpgradeValue extends AbstractUpgradeValue<Integer> {
   public boolean inverse;
   public int defaultValue;
   public float defaultLevelIncreaseMultiplier;
   private TreeMap<Float, Integer> map;

   public IntUpgradeValue(boolean var1, int var2, float var3) {
      this.map = new TreeMap();
      this.inverse = var1;
      this.defaultValue = var2;
      this.defaultLevelIncreaseMultiplier = var3;
   }

   public IntUpgradeValue(int var1, float var2) {
      this(false, var1, var2);
   }

   public IntUpgradeValue() {
      this(false, 0, 0.0F);
   }

   public IntUpgradeValue setBaseValue(int var1) {
      return this.setUpgradedValue(0.0F, var1);
   }

   public IntUpgradeValue setUpgradedValue(float var1, int var2) {
      this.map.put(var1, var2);
      return this;
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean hasMoreThanOneValue() {
      return this.map.size() > 1;
   }

   public Integer getValue(float var1) {
      Map.Entry var2 = null;
      Map.Entry var3 = null;
      Map.Entry var4 = this.map.floorEntry(var1);
      Map.Entry var5;
      if (var4 != null) {
         if (var1 == (Float)var4.getKey()) {
            return (Integer)var4.getValue();
         }

         var2 = var4;
         var5 = this.map.higherEntry(var1);
         if (var5 != null) {
            var3 = var5;
         } else {
            var2 = this.map.lowerEntry((Float)var4.getKey());
            var3 = var4;
         }
      } else {
         var5 = this.map.higherEntry(var1);
         if (var5 != null) {
            var2 = var5;
            var3 = this.map.higherEntry((Float)var5.getKey());
         }
      }

      if (var2 != null && var3 != null) {
         if ((Float)var2.getKey() < 1.0F) {
            float var6;
            int var8;
            if ((Float)var3.getKey() > 1.0F) {
               var8 = Math.round((float)(Integer)var3.getValue() / (1.0F + this.defaultLevelIncreaseMultiplier * (Float)var3.getKey()));
            } else if ((Float)var3.getKey() < 1.0F) {
               var6 = GameMath.getPercentageBetweenTwoNumbers(1.0F, (Float)var2.getKey(), (Float)var3.getKey());
               var8 = GameMath.lerp(var6, (Integer)var2.getValue(), (Integer)var3.getValue());
            } else {
               var8 = (Integer)var3.getValue();
            }

            if (var1 > 1.0F) {
               var6 = 1.0F + (var1 - 1.0F) * this.defaultLevelIncreaseMultiplier;
               return this.inverse ? Math.round((float)var8 * (1.0F / var6)) : Math.round((float)var8 * var6);
            } else {
               var6 = GameMath.getPercentageBetweenTwoNumbers(var1, (Float)var2.getKey(), 1.0F);
               return GameMath.lerp(var6, (Integer)var2.getValue(), var8);
            }
         } else {
            float var7 = GameMath.getPercentageBetweenTwoNumbers(var1, (Float)var2.getKey(), (Float)var3.getKey());
            return GameMath.lerp(var7, (Integer)var2.getValue(), (Integer)var3.getValue());
         }
      } else if (var2 != null) {
         return (Integer)var2.getValue();
      } else {
         return var3 != null ? (Integer)var3.getValue() : this.defaultValue;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object getValue(float var1) {
      return this.getValue(var1);
   }
}
