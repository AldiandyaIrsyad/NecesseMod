package necesse.inventory.item.placeableItem.bucketItem;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.LinkedList;
import necesse.engine.GameEvents;
import necesse.engine.Screen;
import necesse.engine.control.Control;
import necesse.engine.control.Input;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class InfiniteWaterBucketItem extends PlaceableItem implements ItemInteractAction {
   public InfiniteWaterBucketItem() {
      super(1, false);
      this.controllerIsTileBasedPlacing = true;
      this.setItemCategory(new String[]{"equipment", "tools"});
      this.keyWords.add("liquid");
      this.rarity = Item.Rarity.EPIC;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var2 >= 0 && var3 >= 0 && var2 < var1.width * 32 && var3 < var1.height * 32) {
         int var7 = var2 / 32;
         int var8 = var3 / 32;
         if (var1.isProtected(var7, var8)) {
            return "protected";
         } else if (var4.getPositionPoint().distance((double)(var7 * 32 + 16), (double)(var8 * 32 + 16)) > (double)this.getPlaceRange(var5, var4)) {
            return "outofrange";
         } else {
            GameTile var9 = TileRegistry.getTile(TileRegistry.waterID);
            return var9.canPlace(var1, var7, var8);
         }
      } else {
         return "outsidelevel";
      }
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      int var7 = var2 / 32;
      int var8 = var3 / 32;
      ItemPlaceEvent var9 = new ItemPlaceEvent(var1, var7, var8, var4, var5);
      GameEvents.triggerEvent(var9);
      if (!var9.isPrevented()) {
         TileRegistry.getTile(TileRegistry.waterID).placeTile(var1, var7, var8);
         if (var1.isClient()) {
            Screen.playSound(GameResources.watersplash, SoundEffect.effect((float)(var7 * 32 + 16), (float)(var8 * 32 + 16)));
         } else {
            var1.sendTileUpdatePacket(var7, var8);
            var1.getLevelTile(var7, var8).checkAround();
            var1.getLevelObject(var7, var8).checkAround();
         }
      }

      return var5;
   }

   public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7) {
      int var8;
      int var9;
      if (this.canPlace(var1, var2, var3, var5, var6, (PacketReader)null) == null) {
         var8 = var2 / 32;
         var9 = var3 / 32;
         TileRegistry.getTile(TileRegistry.waterID).drawPreview(var1, var8, var9, 0.5F, var5, var4);
      } else if (!Input.lastInputIsController) {
         var8 = var2 / 32;
         var9 = var3 / 32;
         if (var1.isProtected(var8, var9)) {
            return;
         }

         if (var5.getPositionPoint().distance((double)(var8 * 32 + 16), (double)(var9 * 32 + 16)) > (double)this.getPlaceRange(var6, var5)) {
            return;
         }

         if (var1.getTileID(var8, var9) != TileRegistry.waterID) {
            return;
         }

         if (var1.liquidManager.getHeight(var8, var9) < -3) {
            return;
         }

         TileRegistry.getTile(TileRegistry.dirtID).drawPreview(var1, var8, var9, 0.5F, var5, var4);
      }

   }

   public ItemControllerInteract getControllerInteract(Level var1, PlayerMob var2, InventoryItem var3, boolean var4, int var5, LinkedList<Rectangle> var6, LinkedList<Rectangle> var7) {
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
      })).map((var4x) -> {
         int var5 = var4x.tileX * 32 + 16;
         int var6 = var4x.tileY * 32 + 16;
         return new ItemControllerInteract(var5, var6) {
            public DrawOptions getDrawOptions(GameCamera var1x) {
               if (var1.isProtected(var4.tileX, var4.tileY)) {
                  return null;
               } else if (var2.getPositionPoint().distance((double)(var4.tileX * 32 + 16), (double)(var4.tileY * 32 + 16)) > (double)InfiniteWaterBucketItem.this.getPlaceRange(var3, var2)) {
                  return null;
               } else if (var1.getTileID(var4.tileX, var4.tileY) != TileRegistry.waterID) {
                  return null;
               } else {
                  return var1.liquidManager.getHeight(var4.tileX, var4.tileY) < -3 ? null : () -> {
                     TileRegistry.getTile(TileRegistry.dirtID).drawPreview(var1, var4.tileX, var4.tileY, 0.5F, var2, var1x);
                  };
               }
            }

            public void onCurrentlyFocused(GameCamera var1x) {
            }
         };
      }).orElse((Object)null);
   }

   public boolean canLevelInteract(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return true;
   }

   public InventoryItem onLevelInteract(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, PacketReader var9) {
      int var10 = var2 / 32;
      int var11 = var3 / 32;
      if (var1.isProtected(var10, var11)) {
         return var6;
      } else if (var4.getPositionPoint().distance((double)(var10 * 32 + 16), (double)(var11 * 32 + 16)) > (double)this.getPlaceRange(var6, var4)) {
         return var6;
      } else if (var1.getTileID(var10, var11) != TileRegistry.waterID) {
         return var6;
      } else if (var1.liquidManager.getHeight(var10, var11) < -3) {
         return var6;
      } else {
         ItemPlaceEvent var12 = new ItemPlaceEvent(var1, var10, var11, var4, var6);
         GameEvents.triggerEvent(var12);
         if (!var12.isPrevented()) {
            TileRegistry.getTile(TileRegistry.dirtID).placeTile(var1, var10, var11);
            if (var1.isClient()) {
               Screen.playSound(GameResources.waterblob, SoundEffect.effect((float)(var10 * 32 + 16), (float)(var11 * 32 + 16)));
            } else {
               var1.sendTileUpdatePacket(var10, var11);
               var1.getLevelTile(var10, var11).checkAround();
               var1.getLevelObject(var10, var11).checkAround();
            }
         }

         return var6;
      }
   }

   public boolean getConstantInteract(InventoryItem var1) {
      return true;
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.BUILDING_SPEED);
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "infwaterbuckettip"));
      var4.add((Object)(new InputTooltip(Control.MOUSE1, Localization.translate("itemtooltip", "infwaterbucketplace"))));
      var4.add((Object)(new InputTooltip(Control.MOUSE2, Localization.translate("itemtooltip", "infwaterbucketpickup"))));
      return var4;
   }
}
