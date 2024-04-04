package necesse.entity.objectEntity;

import necesse.engine.Screen;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.gfx.GameResources;
import necesse.gfx.gameSound.GameSound;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class AnyLogFueledInventoryObjectEntity extends FueledInventoryObjectEntity {
   public GameSound workingSound;
   private SoundPlayer sound;

   public AnyLogFueledInventoryObjectEntity(Level var1, String var2, int var3, int var4, boolean var5) {
      super(var1, var2, var3, var4, 2, var5);
      this.workingSound = GameResources.campfireAmbient;
   }

   public void clientTick() {
      super.clientTick();
      if (this.workingSound != null && this.isFueled()) {
         if (this.sound == null || this.sound.isDone()) {
            this.sound = Screen.playSound(this.workingSound, SoundEffect.effect(this).falloffDistance(400).volume(0.25F));
         }

         if (this.sound != null) {
            this.sound.refreshLooping(1.0F);
         }
      }

   }

   public boolean isValidFuelItem(int var1, InventoryItem var2) {
      return var2.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog"));
   }

   public int getNextFuelBurnTime(boolean var1) {
      return this.itemToBurnTime(var1, (var0) -> {
         return var0.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog")) ? 120000 : 0;
      });
   }
}
