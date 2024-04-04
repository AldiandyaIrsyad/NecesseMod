package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.gfx.GameResources;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;

public class RandomVaseObject extends RandomBreakObject {
   public RandomVaseObject(String var1) {
      super(new Rectangle(5, 12, 22, 8), var1, new Color(40, 40, 40));
   }

   public LootTable getBreakLootTable(Level var1, int var2, int var3) {
      return var1.getCrateLootTable();
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.shatter2, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)).volume(0.8F).pitch(GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F)));
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -24, 32, 24));
      return var4;
   }
}
