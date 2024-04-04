package necesse.inventory.item.mountItem;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.WoodBoatMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.maps.Level;

public class WoodBoatMountItem extends MountItem implements PlaceableItemInterface {
   public WoodBoatMountItem() {
      super("woodboatmount");
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = this.getBaseTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "boattip1"));
      var4.add(Localization.translate("itemtooltip", "boattip2"));
      return var4;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotation(var5);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      if (this.canPlace(var1, var2, var3, var4, var6, var10) == null) {
         if (var1.isServer()) {
            Mob var11 = MobRegistry.getMob("woodboat", var1);
            if (var11 != null) {
               var11.resetUniqueID();
               var11.dir = var4.dir;
               var1.entityManager.addMob(var11, (float)var2, (float)var3);
            }
         } else if (var1.isClient() && Settings.showControlTips) {
            var1.getClient().setMessage((GameMessage)(new LocalMessage("misc", "boatplacetip")), Color.WHITE, 5.0F);
         }

         if (var1.isClient()) {
            Screen.playSound(GameResources.waterblob, SoundEffect.effect((float)var2, (float)var3).volume(0.8F));
         }

         var6.setAmount(var6.getAmount() - 1);
         return var6;
      } else {
         return var6;
      }
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return null;
   }

   protected String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var4.getPositionPoint().distance((double)var2, (double)var3) > 100.0) {
         return "outofrange";
      } else {
         Mob var7 = MobRegistry.getMob("woodboat", var1);
         if (var7 != null) {
            var7.setPos((float)var2, (float)var3, true);
            if (var7.collidesWith(var1)) {
               return "collision";
            }
         }

         return null;
      }
   }

   public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7) {
      String var8 = this.canPlace(var1, var2, var3, var5, var6, (PacketReader)null);
      if (var8 == null) {
         WoodBoatMob.drawPlacePreview(var1, var2, var3, var5.dir, var4);
      }

   }
}
