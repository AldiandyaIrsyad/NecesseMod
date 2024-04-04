package necesse.inventory.enchants;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.inventory.InventoryItem;

public interface Enchantable<T extends ItemEnchantment> {
   void setEnchantment(InventoryItem var1, int var2);

   int getEnchantmentID(InventoryItem var1);

   void clearEnchantment(InventoryItem var1);

   T getEnchantment(InventoryItem var1);

   boolean isValidEnchantment(InventoryItem var1, ItemEnchantment var2);

   Set<Integer> getValidEnchantmentIDs(InventoryItem var1);

   T getRandomEnchantment(GameRandom var1, InventoryItem var2);

   int getEnchantCost(InventoryItem var1);

   default int getRandomEnchantmentID(GameRandom var1, InventoryItem var2) {
      ItemEnchantment var3 = this.getRandomEnchantment(var1, var2);
      return var3 == null ? 0 : var3.getID();
   }

   default int getFinalEnchantCost(InventoryItem var1) {
      return (int)((float)this.getEnchantCost(var1) * this.getEnchantment(var1).getEnchantCostMod());
   }

   default int getRandomEnchantCost(InventoryItem var1, GameRandom var2, int var3) {
      return HumanShop.getRandomHappinessMiddlePrice(var2, var3, this.getFinalEnchantCost(var1), 6, 3);
   }

   default String getEnchantName(InventoryItem var1) {
      return this.getEnchantment(var1).getDisplayName();
   }

   default void addRandomEnchantment(InventoryItem var1, GameRandom var2) {
      this.setEnchantment(var1, this.getRandomEnchantmentID(var2, var1));
   }

   default void addRandomEnchantment(InventoryItem var1) {
      this.setEnchantment(var1, this.getRandomEnchantment(var1));
   }

   default int getRandomEnchantment(InventoryItem var1) {
      return this.getRandomEnchantmentID(GameRandom.globalRandom, var1);
   }

   default GameTooltips getEnchantmentTooltips(InventoryItem var1) {
      return this.getEnchantment(var1).getTooltips();
   }

   static <T extends ItemEnchantment> T getRandomEnchantment(GameRandom var0, Set<Integer> var1, Predicate<Integer> var2, Class<T> var3, T var4) {
      List var5 = (List)var1.stream().filter(var2).collect(Collectors.toList());
      if (var5.isEmpty()) {
         return null;
      } else {
         int var6 = (Integer)var5.get(var0.nextInt(var5.size()));
         return EnchantmentRegistry.getEnchantment(var6, var3, var4);
      }
   }

   static <T extends ItemEnchantment> T getRandomEnchantment(GameRandom var0, Set<Integer> var1, int var2, Class<T> var3) {
      return getRandomEnchantment(var0, var1, (var1x) -> {
         return var1x != var2;
      }, var3, (ItemEnchantment)null);
   }
}
