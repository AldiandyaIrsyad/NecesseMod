package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.gfx.GameResources;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;

public class RandomCoinStackObject extends RandomBreakObject {
   public RandomCoinStackObject() {
      super(new Rectangle(), "coinstacks", new Color(205, 180, 70), false);
      this.countAsCratesBroken = false;
   }

   public LootTable getBreakLootTable(Level var1, int var2, int var3) {
      int var4 = this.getSprite(new GameRandom(), var2, var3, 4);
      int var5 = 40 + var4 * 30;
      return new LootTable(new LootItemInterface[]{LootItem.between("coin", var5, var5 + 29)});
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.coins, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
   }
}
