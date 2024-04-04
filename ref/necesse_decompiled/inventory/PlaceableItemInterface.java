package necesse.inventory;

import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.level.maps.Level;

public interface PlaceableItemInterface {
   static int getPlaceRange(PlayerMob var0) {
      return (int)((3.5F + (var0 == null ? 0.0F : (Float)var0.buffManager.getModifier(BuffModifiers.BUILD_RANGE))) * 32.0F);
   }

   default int getPlaceRange(InventoryItem var1, PlayerMob var2) {
      return getPlaceRange(var2);
   }

   void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7);
}
