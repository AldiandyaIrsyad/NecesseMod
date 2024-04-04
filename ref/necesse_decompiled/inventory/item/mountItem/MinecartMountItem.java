package necesse.inventory.item.mountItem;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.MinecartLinePos;
import necesse.entity.mobs.summon.MinecartLines;
import necesse.entity.mobs.summon.MinecartMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MinecartMountMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.MinecartTrackObject;
import necesse.level.gameObject.TrapTrackObject;
import necesse.level.maps.Level;

public class MinecartMountItem extends MountItem implements PlaceableItemInterface {
   public MinecartMountItem() {
      super("minecartmount");
      this.setMounterPos = false;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = this.getBaseTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "minecarttip"));
      return var4;
   }

   public String canUseMount(InventoryItem var1, PlayerMob var2, Level var3) {
      Mob var4 = var2.getMount();
      if (var4 != null) {
         return null;
      } else {
         String var5 = super.canUseMount(var1, var2, var3);
         if (var5 != null) {
            return var5;
         } else {
            int var6 = var2.getTileX();
            int var7 = var2.getTileY();

            for(int var8 = var6 - 1; var8 <= var6 + 1; ++var8) {
               for(int var9 = var7 - 1; var9 <= var7 + 1; ++var9) {
                  GameObject var10 = var3.getObject(var8, var9);
                  if (var10 instanceof MinecartTrackObject && !(var10 instanceof TrapTrackObject)) {
                     return null;
                  }
               }
            }

            return Localization.translate("misc", "cannotusemounthere", "mount", this.getDisplayName(var1));
         }
      }
   }

   public Point2D.Float getMountSpawnPos(Mob var1, ServerClient var2, float var3, float var4, InventoryItem var5, Level var6) {
      PlayerMob var7 = var2.playerMob;
      float var8 = Float.MAX_VALUE;
      MinecartLinePos var9 = null;
      int var10 = var7.getTileX();
      int var11 = var7.getTileY();

      for(int var12 = var10 - 1; var12 <= var10 + 1; ++var12) {
         for(int var13 = var11 - 1; var13 <= var11 + 1; ++var13) {
            GameObject var14 = var6.getObject(var12, var13);
            if (var14 instanceof MinecartTrackObject && !(var14 instanceof TrapTrackObject)) {
               MinecartLines var15 = ((MinecartTrackObject)var14).getMinecartLines(var6, var12, var13, 0.0F, 0.0F, false);
               MinecartLinePos var16 = var15.getMinecartPos(var7.x, var7.y, var7.dir);
               if (var16 != null) {
                  float var17 = var7.getDistance(var16.x, var16.y);
                  if (var9 == null || var17 < var8) {
                     var9 = var16;
                     var8 = var17;
                  }
               }
            }
         }
      }

      if (var9 != null) {
         var1.dir = var9.dir;
         ((MinecartMountMob)var1).minecartDir = var9.dir;
         return new Point2D.Float(var9.x, var9.y);
      } else {
         return super.getMountSpawnPos(var1, var2, var3, var4, var5, var6);
      }
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotation(var5);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      if (this.canPlace(var1, var2, var3, var4, var6, var10) == null) {
         if (var1.isServer()) {
            Mob var11 = MobRegistry.getMob("minecart", var1);
            if (var11 instanceof MinecartMob) {
               ((MinecartMob)var11).minecartDir = var4.isAttacking ? var4.beforeAttackDir : var4.dir;
               var11.resetUniqueID();
               var1.entityManager.addMob(var11, (float)var2, (float)var3);
            }
         }

         if (var1.isClient()) {
            Screen.playSound(GameResources.cling, SoundEffect.effect((float)var2, (float)var3).volume(0.8F));
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
         Mob var7 = MobRegistry.getMob("minecart", var1);
         if (var7 != null) {
            var7.setPos((float)var2, (float)var3, true);
            if (var7.collidesWith(var1)) {
               return "collision";
            }

            GameObject var8 = var1.getObject(var7.getTileX(), var7.getTileY());
            if (!(var8 instanceof MinecartTrackObject) || var8 instanceof TrapTrackObject) {
               return "nottracks";
            }
         }

         return null;
      }
   }

   public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7) {
      String var8 = this.canPlace(var1, var2, var3, var5, var6, (PacketReader)null);
      if (var8 == null) {
         int var9 = var5.isAttacking ? var5.beforeAttackDir : var5.dir;
         MinecartMob.drawPlacePreview(var1, var2, var3, var9, var4);
      }

   }
}
