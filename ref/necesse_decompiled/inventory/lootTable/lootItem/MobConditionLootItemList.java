package necesse.inventory.lootTable.lootItem;

import java.util.function.BiFunction;
import java.util.function.Function;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;

public class MobConditionLootItemList extends ConditionLootItemList {
   public MobConditionLootItemList(BiFunction<GameRandom, Mob, Boolean> var1, LootItemInterface... var2) {
      super((var1x, var2x) -> {
         Mob var3 = (Mob)LootTable.expectExtra(Mob.class, var2x, 0);
         return var3 != null ? (Boolean)var1.apply(var1x, var3) : false;
      }, var2);
   }

   public MobConditionLootItemList(Function<Mob, Boolean> var1, LootItemInterface... var2) {
      super((var1x, var2x) -> {
         Mob var3 = (Mob)LootTable.expectExtra(Mob.class, var2x, 0);
         return var3 != null ? (Boolean)var1.apply(var3) : false;
      }, var2);
   }
}
