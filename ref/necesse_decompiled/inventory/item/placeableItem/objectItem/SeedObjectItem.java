package necesse.inventory.item.placeableItem.objectItem;

import java.awt.Point;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SeedObject;
import necesse.level.maps.Level;

public class SeedObjectItem extends CustomObjectItem {
   private SeedObject seed;

   public SeedObjectItem(SeedObject var1, Supplier<GameTexture> var2) {
      super(var1, (Supplier)var2, 0, 0);
      this.seed = var1;
      this.addGlobalIngredient(new String[]{"anycompostable"});
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "seedtip"));
      if (this.seed.canBePlacedAsFlower) {
         var4.add(Localization.translate("itemtooltip", "flowertip"));
      }

      return var4;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      String var7 = super.canPlace(var1, var2, var3, var4, var5, new PacketReader(var6));
      if (var7 != null) {
         return this.seed.canBePlacedAsFlower ? this.getFlowerItem().canPlace(var1, var2, var3, var4, var5, new PacketReader(var6)) : var7;
      } else {
         return null;
      }
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return this.seed.canBePlacedAsFlower && this.getFlowerItem().canPlace(var1, var2, var3, var4, var5, new PacketReader(var6)) == null ? this.getFlowerItem().onPlace(var1, var2, var3, var4, var5, new PacketReader(var6)) : super.onPlace(var1, var2, var3, var4, var5, new PacketReader(var6));
   }

   public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7) {
      GameObject var8 = this.getObject();
      int var9 = var8.getPlaceRotation(var1, var2, var3, var5, var5.isAttacking ? var5.beforeAttackDir : var5.dir);
      Point var10 = var8.getPlaceOffset(var1, var2, var3, var5, var9);
      int var11 = (var2 + var10.x) / 32;
      int var12 = (var3 + var10.y) / 32;
      float var13 = 0.5F;
      if (this.seed.canBePlacedAsFlower && this.getFlowerObject().canPlace(var1, var11, var12, var9) == null) {
         this.getFlowerObject().drawPreview(var1, var11, var12, var9, var13, var5, var4);
      } else {
         if (var8.canPlace(var1, var11, var12, var9) == null) {
            var8.drawPreview(var1, var11, var12, var9, var13, var5, var4);
         }

      }
   }

   public GameObject getFlowerObject() {
      return ObjectRegistry.getObject(this.seed.flowerID);
   }

   public ObjectItem getFlowerItem() {
      return this.getFlowerObject().getObjectItem();
   }
}
