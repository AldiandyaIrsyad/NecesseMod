package necesse.inventory.item.placeableItem.bucketItem;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.GameEvents;
import necesse.engine.Screen;
import necesse.engine.control.Control;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class BucketItem extends PlaceableItem implements ItemInteractAction {
   public BucketItem() {
      super(50, true);
      this.controllerIsTileBasedPlacing = true;
      this.setItemCategory(new String[]{"equipment", "tools"});
      this.keyWords.add("liquid");
      this.incinerationTimeMillis = 30000;
   }

   public void loadItemTextures() {
      this.itemTexture = GameTexture.fromFile("tiles/bucket");
   }

   public GameSprite getItemSprite(InventoryItem var1, PlayerMob var2) {
      return new GameSprite(this.itemTexture, 0, 0, 32);
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var2 >= 0 && var3 >= 0 && var2 < var1.width * 32 && var3 < var1.height * 32) {
         int var7 = var2 / 32;
         int var8 = var3 / 32;
         if (var1.isProtected(var7, var8)) {
            return "protected";
         } else if (var4.getPositionPoint().distance((double)(var7 * 32 + 16), (double)(var8 * 32 + 16)) > (double)this.getPlaceRange(var5, var4)) {
            return "outofrange";
         } else if (!var1.isLiquidTile(var7, var8)) {
            return "notliquid";
         } else {
            return var1.liquidManager.getHeight(var7, var8) < -3 ? "deepsea" : null;
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
         GameTile var10 = var1.getTile(var7, var8);
         var1.setTile(var7, var8, TileRegistry.dirtID);
         if (var1.isClient()) {
            Screen.playSound(GameResources.waterblob, SoundEffect.effect((float)(var7 * 32 + 16), (float)(var8 * 32 + 16)));
         } else {
            var1.sendTileUpdatePacket(var7, var8);
            var1.getLevelTile(var7, var8).checkAround();
            var1.getLevelObject(var7, var8).checkAround();
         }

         InventoryItem var11 = new InventoryItem(ItemRegistry.getItem(var10.getStringID()));
         if (var5.getAmount() <= 1 && this.singleUse) {
            return var11;
         }

         var4.getInv().addItemsDropRemaining(var11, "addback", var4, false, false);
         if (this.singleUse) {
            var5.setAmount(var5.getAmount() - 1);
         }
      }

      return var5;
   }

   public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7) {
      if (this.canPlace(var1, var2, var3, var5, var6, (PacketReader)null) == null) {
         int var8 = var2 / 32;
         int var9 = var3 / 32;
         TileRegistry.getTile(TileRegistry.dirtID).drawPreview(var1, var8, var9, 0.5F, var5, var4);
      }

   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotationInv(var5);
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.BUILDING_SPEED);
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add((Object)(new StringTooltips(Localization.translate("itemtooltip", "buckettip"), 300)));
      return var4;
   }

   public boolean canMobInteract(Level var1, Mob var2, PlayerMob var3, InventoryItem var4) {
      return var2 instanceof HusbandryMob && ((HusbandryMob)var2).canMilk(var4) && var2.inInteractRange(var3);
   }

   public InventoryItem onMobInteract(Level var1, Mob var2, PlayerMob var3, int var4, InventoryItem var5, PlayerInventorySlot var6, int var7, PacketReader var8) {
      if (var2 instanceof HusbandryMob) {
         HusbandryMob var9 = (HusbandryMob)var2;
         if (var9.canMilk(var5)) {
            ArrayList var10 = new ArrayList();
            InventoryItem var11 = var9.onMilk(var5, var10);
            if (!var1.isClient()) {
               Iterator var12 = var10.iterator();

               while(var12.hasNext()) {
                  InventoryItem var13 = (InventoryItem)var12.next();
                  var1.entityManager.pickups.add(var13.getPickupEntity(var1, var9.x, var9.y));
               }
            }

            return var11;
         }
      }

      return var5;
   }

   public boolean getConstantInteract(InventoryItem var1) {
      return true;
   }

   public boolean onMouseHoverMob(InventoryItem var1, GameCamera var2, PlayerMob var3, Mob var4, boolean var5) {
      boolean var6 = super.onMouseHoverMob(var1, var2, var3, var4, var5);
      if (var4 instanceof HusbandryMob && ((HusbandryMob)var4).canMilk(var1)) {
         if (var4.inInteractRange(var3)) {
            Screen.setCursor(Screen.CURSOR.INTERACT);
            Screen.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "milktip")), TooltipLocation.INTERACT_FOCUS);
         } else {
            Screen.setCursor(Screen.CURSOR.INTERACT);
            Screen.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "milktip"), 0.5F), TooltipLocation.INTERACT_FOCUS);
         }

         return true;
      } else {
         return var6;
      }
   }
}
