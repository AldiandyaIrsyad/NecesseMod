package necesse.inventory.item.placeableItem;

import necesse.engine.GameEvents;
import necesse.engine.Screen;
import necesse.engine.control.Control;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.gfx.forms.presets.sidebar.WireEditSidebarForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class WrenchPlaceableItem extends PlaceableItem implements ItemInteractAction {
   public WrenchPlaceableItem() {
      super(1, false);
      this.controllerIsTileBasedPlacing = true;
      this.rarity = Item.Rarity.COMMON;
      this.setItemCategory(new String[]{"wiring"});
      this.keyWords.add("wire");
      this.keyWords.add("logic");
      this.keyWords.add("gate");
      this.keyWords.add("logicgate");
      this.attackXOffset = 8;
      this.attackYOffset = 8;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public GameSprite getAttackSprite(InventoryItem var1, PlayerMob var2) {
      return this.getItemSprite(var1, var2);
   }

   public boolean canLevelInteract(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return this.canInteractError(var1, var2, var3, var4, var5) == null;
   }

   public String canInteractError(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      int var6 = var2 / 32;
      int var7 = var3 / 32;
      if (var1.isProtected(var6, var7)) {
         return "protected";
      } else if (!var1.logicLayer.hasGate(var6, var7)) {
         return "nogate";
      } else {
         return var4.getPositionPoint().distance((double)(var6 * 32 + 16), (double)(var7 * 32 + 16)) > (double)this.getPlaceRange(var5, var4) ? "outofrange" : null;
      }
   }

   public boolean overridesObjectInteract(Level var1, PlayerMob var2, InventoryItem var3) {
      return true;
   }

   public InventoryItem onLevelInteract(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, PacketReader var9) {
      int var10 = var2 / 32;
      int var11 = var3 / 32;
      if (var1.isServer()) {
         LogicGateEntity var12 = var1.logicLayer.getEntity(var10, var11);
         if (var12 != null) {
            var12.openContainer(var4.getServerClient());
         }
      }

      return var6;
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
            int var9 = 0;
            int var10;
            if (var6 != null) {
               for(var10 = 0; var10 < 4; ++var10) {
                  if (var6.getNextBoolean() && !var1.wireManager.hasWire(var7, var8, var10)) {
                     ++var9;
                  }
               }
            } else if (var1.isClient()) {
               for(var10 = 0; var10 < 4; ++var10) {
                  if (WireEditSidebarForm.isEditing(var10) && !var1.wireManager.hasWire(var7, var8, var10)) {
                     ++var9;
                  }
               }
            }

            if (var9 == 0) {
               return "nowire";
            } else {
               return var9 > this.getAvailableAmmo(var4) ? "noammo" : null;
            }
         }
      } else {
         return "outsidelevel";
      }
   }

   public void setupAttackContentPacket(PacketWriter var1, Level var2, int var3, int var4, PlayerMob var5, InventoryItem var6) {
      super.setupAttackContentPacket(var1, var2, var3, var4, var5, var6);

      for(int var7 = 0; var7 < 4; ++var7) {
         var1.putNextBoolean(WireEditSidebarForm.isEditing(var7));
      }

   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      int var7 = var2 / 32;
      int var8 = var3 / 32;
      ItemPlaceEvent var9 = new ItemPlaceEvent(var1, var7, var8, var4, var5);
      GameEvents.triggerEvent(var9);
      if (!var9.isPrevented()) {
         int var10 = 0;

         for(int var11 = 0; var11 < 4; ++var11) {
            if (var6.getNextBoolean() && !var1.wireManager.hasWire(var7, var8, var11)) {
               var1.wireManager.setWire(var7, var8, var11, true);
               ++var10;
            }
         }

         if (var10 > 0) {
            if (var1.isServer()) {
               var1.sendWireUpdatePacket(var7, var8);
            }

            var4.getInv().main.removeItems(var1, var4, ItemRegistry.getItem("wire"), var10, "wireammo");
         }
      }

      return var5;
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.BUILDING_SPEED);
   }

   public int getAvailableAmmo(PlayerMob var1) {
      return var1 != null ? var1.getInv().main.getAmount(var1.getLevel(), var1, ItemRegistry.getItem("wire"), "wireammo") : 0;
   }

   public void draw(InventoryItem var1, PlayerMob var2, int var3, int var4, boolean var5) {
      super.draw(var1, var2, var3, var4, var5);
      if (var5) {
         int var6 = this.getAvailableAmmo(var2);
         if (var6 > 999) {
            var6 = 999;
         }

         String var7 = String.valueOf(var6);
         int var8 = FontManager.bit.getWidthCeil(var7, tipFontOptions);
         FontManager.bit.drawString((float)(var3 + 28 - var8), (float)(var4 + 16), var7, tipFontOptions);
      }

   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "wrenchtip"));
      var4.add(Localization.translate("itemtooltip", "wrenchammo", "value", (Object)this.getAvailableAmmo(var2)));
      return var4;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotation(var5);
   }

   public boolean showWires() {
      return true;
   }

   public SidebarForm getSidebar(InventoryItem var1) {
      return new WireEditSidebarForm(var1);
   }

   public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7) {
      int var8 = var2 / 32;
      int var9 = var3 / 32;
      if (this.canPlace(var1, var2, var3, var5, var6, (PacketReader)null) == null) {
         for(int var10 = 0; var10 < 4; ++var10) {
            if (WireEditSidebarForm.isEditing(var10) && !var1.wireManager.hasWire(var8, var9, var10)) {
               var1.wireManager.drawWirePreset(var8, var9, var4, var10);
            }
         }
      }

   }

   public void onMouseHoverTile(InventoryItem var1, GameCamera var2, PlayerMob var3, int var4, int var5, TilePosition var6, boolean var7) {
      super.onMouseHoverTile(var1, var2, var3, var4, var5, var6, var7);
      String var8 = this.canInteractError(var6.level, var4, var5, var3, var1);
      if (var8 == null) {
         Screen.setCursor(Screen.CURSOR.INTERACT);
         Screen.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "edittip")), TooltipLocation.INTERACT_FOCUS);
      } else if (var8.equals("outofrange")) {
         Screen.setCursor(Screen.CURSOR.INTERACT);
         Screen.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "edittip"), 0.5F), TooltipLocation.INTERACT_FOCUS);
      }

   }
}
