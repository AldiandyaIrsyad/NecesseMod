package necesse.inventory.item.trinketItem;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class WoodShieldTrinketItem extends ShieldTrinketItem {
   public WoodShieldTrinketItem(Item.Rarity var1, int var2, float var3, int var4, float var5, int var6, float var7, int var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void playHitSound(InventoryItem var1, Mob var2, MobWasHitEvent var3) {
      Screen.playSound(GameResources.tap2, SoundEffect.effect(var2).volume(0.8F));
   }
}
