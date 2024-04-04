package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.inventory.item.ItemCategory;

public class FoodQuality {
   public final GameMessage displayName;
   public final int happinessIncrease;
   public final String[] masterCategoryTree;
   public final ItemCategory foodCategory;
   public static HappinessModifier noFoodModifier = new HappinessModifier(0, new LocalMessage("settlement", "nofoodmood"));

   public FoodQuality(GameMessage var1, int var2, String var3, String... var4) {
      this.displayName = var1;
      this.happinessIncrease = var2;
      this.masterCategoryTree = (String[])GameUtils.concat(new String[]{"consumable", "food"}, var4);
      ItemCategory.createCategory(var3, var1, this.masterCategoryTree);
      this.foodCategory = ItemCategory.foodQualityManager.createCategory(var3, var1, var4);
   }

   public HappinessModifier getModifier() {
      LocalMessage var1 = (new LocalMessage("settlement", "foodmood")).addReplacement("quality", this.displayName);
      return new HappinessModifier(this.happinessIncrease, var1);
   }
}
