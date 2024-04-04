package necesse.inventory.item;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.LinkedList;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.InputPosition;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.ui.HUD;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public interface ItemInteractAction {
   default int getLevelInteractAttackAnimTime(InventoryItem var1, PlayerMob var2) {
      return var1.item.getAttackAnimTime(var1, var2);
   }

   default int getLevelInteractCooldownTime(InventoryItem var1, PlayerMob var2) {
      return var1.item.getAttackCooldownTime(var1, var2);
   }

   default int getMobInteractAnimTime(InventoryItem var1, PlayerMob var2) {
      return this.getLevelInteractAttackAnimTime(var1, var2);
   }

   default int getMobInteractCooldownTime(InventoryItem var1, PlayerMob var2) {
      return this.getLevelInteractCooldownTime(var1, var2);
   }

   default boolean canMobInteract(Level var1, Mob var2, PlayerMob var3, InventoryItem var4) {
      return false;
   }

   default void setupMobInteractContentPacket(PacketWriter var1, Level var2, Mob var3, PlayerMob var4, InventoryItem var5) {
   }

   default InventoryItem onMobInteract(Level var1, Mob var2, PlayerMob var3, int var4, InventoryItem var5, PlayerInventorySlot var6, int var7, PacketReader var8) {
      return var5;
   }

   default void showMobInteract(Level var1, Mob var2, PlayerMob var3, int var4, InventoryItem var5, int var6, PacketReader var7) {
   }

   default ItemControllerInteract getControllerInteract(Level var1, PlayerMob var2, InventoryItem var3, boolean var4, int var5, LinkedList<Rectangle> var6, LinkedList<Rectangle> var7) {
      return var4 && !this.overridesObjectInteract(var1, var2, var3) ? null : (ItemControllerInteract)var7.stream().flatMap((var1x) -> {
         LinkedList var2 = new LinkedList();

         for(int var3 = 0; var3 < var1x.width; ++var3) {
            for(int var4 = 0; var4 < var1x.height; ++var4) {
               var2.add(new TilePosition(var1, var1x.x + var3, var1x.y + var4));
            }
         }

         return var2.stream();
      }).filter((var4x) -> {
         int var5 = var4x.tileX * 32 + 16;
         int var6 = var4x.tileY * 32 + 16;
         return this.canLevelInteract(var1, var5, var6, var2, var3);
      }).min(Comparator.comparingDouble((var1x) -> {
         return (double)var2.getDistance((float)(var1x.tileX * 32 + 16), (float)(var1x.tileY * 32 + 16));
      })).map((var3x) -> {
         int var4 = var3x.tileX * 32 + 16;
         int var5 = var3x.tileY * 32 + 16;
         return new ItemControllerInteract(var4, var5) {
            public DrawOptions getDrawOptions(GameCamera var1x) {
               Rectangle var2x = new Rectangle(var3.tileX, var3.tileY, 1, 1);
               return HUD.tileBoundOptions(var1x, Settings.UI.controllerFocusBoundsColor, true, var2x);
            }

            public void onCurrentlyFocused(GameCamera var1x) {
               Screen.setTooltipsInteractFocus(InputPosition.fromScenePos(Screen.input(), var1x.getDrawX(var3.tileX * 32), var1x.getDrawY(var3.tileY * 32)));
               var1.item.onMouseHoverTile(var1, var1x, var2, this.levelX, this.levelY, var3, false);
            }
         };
      }).orElse((Object)null);
   }

   default boolean canLevelInteract(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return false;
   }

   default void setupLevelInteractContentPacket(PacketWriter var1, Level var2, int var3, int var4, PlayerMob var5, InventoryItem var6) {
   }

   default InventoryItem onLevelInteract(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, PacketReader var9) {
      return var6;
   }

   default void showLevelInteract(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
   }

   default boolean overridesObjectInteract(Level var1, PlayerMob var2, InventoryItem var3) {
      return false;
   }

   default boolean getConstantInteract(InventoryItem var1) {
      return false;
   }
}
