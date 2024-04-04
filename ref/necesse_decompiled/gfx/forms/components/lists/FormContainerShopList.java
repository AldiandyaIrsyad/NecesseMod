package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketShopItemAction;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.Container;
import necesse.inventory.container.mob.ShopContainer;

public class FormContainerShopList extends FormGeneralGridList<ShopItemComp> implements FormRecipeList {
   protected Client client;
   public final ShopContainer shopContainer;
   public final boolean isSell;
   private boolean updateCraftable;

   public FormContainerShopList(int var1, int var2, int var3, int var4, Client var5, ShopContainer var6, boolean var7) {
      super(var1, var2, var3, var4, 44, 44);
      this.client = var5;
      this.shopContainer = var6;
      this.isSell = var7;
      this.acceptMouseRepeatEvents = true;
   }

   protected void init() {
      super.init();
      this.runUpdateCraftable();
      GlobalData.craftingLists.add(this);
   }

   public void updateRecipes() {
   }

   private void runUpdateCraftable() {
      if (this.elements.isEmpty()) {
         if (this.shopContainer.items != null) {
            this.shopContainer.items.stream().filter((var1x) -> {
               return this.isSell == var1x.shopItem.price < 0;
            }).forEach((var1x) -> {
               this.elements.add(new ShopItemComp(var1x, var1x.shopItem.canTrade(this.getContainer().getClient(), this.getContainer().getCraftInventories())));
            });
         }
      } else {
         Iterator var1 = this.elements.iterator();

         while(var1.hasNext()) {
            ShopItemComp var2 = (ShopItemComp)var1.next();
            var2.canUse = var2.networkItem.shopItem.canTrade(this.getContainer().getClient(), this.getContainer().getCraftInventories());
         }
      }

      this.updateCraftable = false;
   }

   public void updateCraftable() {
      this.updateCraftable = true;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.updateCraftable) {
         this.runUpdateCraftable();
      }

      super.draw(var1, var2, var3);
   }

   public GameMessage getEmptyMessage() {
      return new LocalMessage("ui", "shopnoitems");
   }

   public FontOptions getEmptyMessageFontOptions() {
      return new FontOptions(16);
   }

   public Container getContainer() {
      return this.client.getContainer();
   }

   public void dispose() {
      super.dispose();
      GlobalData.craftingLists.remove(this);
   }

   public class ShopItemComp extends FormListGridElement<FormContainerShopList> {
      private final ShopContainer.NetworkShopItem networkItem;
      private boolean canUse;

      public ShopItemComp(ShopContainer.NetworkShopItem var2, boolean var3) {
         this.networkItem = var2;
         this.canUse = var3;
      }

      void draw(FormContainerShopList var1, TickManager var2, PlayerMob var3, int var4) {
         boolean var5 = this.isMouseOver(var1);
         Color var6 = Settings.UI.activeElementColor;
         if (this.canUse && var5) {
            var6 = Settings.UI.highlightElementColor;
         } else if (!this.canUse) {
            var6 = Settings.UI.inactiveElementColor;
         }

         if (var5 && !Screen.input().isKeyDown(-100) && !Screen.input().isKeyDown(-99)) {
            Screen.addTooltip(this.networkItem.shopItem.getTooltips(var3, new GameBlackboard()), GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
         }

         GameTexture var7 = var5 ? Settings.UI.inventoryslot_big.highlighted : Settings.UI.inventoryslot_big.active;
         var7.initDraw().color(var6).draw(2, 2);
         if (this.networkItem.shopItem.item != null) {
            this.networkItem.shopItem.item.draw(var3, 6, 6, false);
         }

      }

      void onClick(FormContainerShopList var1, int var2, InputEvent var3, PlayerMob var4) {
         if (var3.getID() == -100 || var3.isRepeatEvent((Object)this.networkItem)) {
            var3.startRepeatEvents(this.networkItem);
            char var5 = 1;
            if (Control.CRAFT_ALL.isDown()) {
               var5 = '\uffff';
            }

            int var6 = FormContainerShopList.this.shopContainer.fulfillTrade(this.networkItem, var5);
            FormContainerShopList.this.client.network.sendPacket(new PacketShopItemAction(this.networkItem, var5, var6));
            if (var6 != 0) {
               if (var3.shouldSubmitSound()) {
                  FormContainerShopList.this.playTickSound();
               }

               FormContainerShopList.this.updateCraftable();
            }
         }

      }

      void onControllerEvent(FormContainerShopList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT || var3.isRepeatEvent((Object)this.networkItem)) {
            var3.startRepeatEvents(this.networkItem);
            char var6 = 1;
            if (Control.CRAFT_ALL.isDown()) {
               var6 = '\uffff';
            }

            int var7 = FormContainerShopList.this.shopContainer.fulfillTrade(this.networkItem, var6);
            FormContainerShopList.this.client.network.sendPacket(new PacketShopItemAction(this.networkItem, var6, var7));
            if (var7 != 0) {
               if (var3.shouldSubmitSound()) {
                  FormContainerShopList.this.playTickSound();
               }

               FormContainerShopList.this.updateCraftable();
            }
         }

      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormContainerShopList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormContainerShopList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormContainerShopList)var1, var2, var3, var4);
      }
   }
}
