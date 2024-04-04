package necesse.entity.objectEntity;

import necesse.engine.Screen;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.gfx.GameResources;
import necesse.gfx.gameSound.GameSound;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

public abstract class AnyLogFueledProcessingTechInventoryObjectEntity extends FueledProcessingTechInventoryObjectEntity {
   public GameSound workingSound;
   private SoundPlayer playingSound;

   public AnyLogFueledProcessingTechInventoryObjectEntity(Level var1, String var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8, boolean var9, Tech... var10) {
      super(var1, var2, var3, var4, 2, var5, var6, var7, var8, var9, var10);
      this.workingSound = GameResources.campfireAmbient;
   }

   public void clientTick() {
      super.clientTick();
      if (this.workingSound != null && this.isFuelRunning()) {
         if (this.playingSound == null || this.playingSound.isDone()) {
            this.playingSound = Screen.playSound(this.workingSound, SoundEffect.effect(this).falloffDistance(400).volume(0.25F));
         }

         if (this.playingSound != null) {
            this.playingSound.refreshLooping(1.0F);
         }
      }

   }

   public boolean isValidFuelItem(InventoryItem var1) {
      return var1.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog"));
   }

   public int getNextFuelBurnTime(boolean var1) {
      return this.itemToBurnTime(var1, (var1x) -> {
         return var1x.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog")) ? this.getFuelTime(var1x) : 0;
      });
   }

   public abstract int getFuelTime(InventoryItem var1);
}
