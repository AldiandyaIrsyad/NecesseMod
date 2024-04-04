package necesse.entity.mobs;

import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;

public interface HungerMob {
   float getHungerLevel();

   void useHunger(float var1, boolean var2);

   void addHunger(float var1);

   default boolean useFoodItem(FoodConsumableItem var1, boolean var2) {
      if (var2) {
         var1.giveFoodBuff((Mob)this);
      }

      this.addHunger((float)var1.nutrition / 100.0F);
      return true;
   }
}
