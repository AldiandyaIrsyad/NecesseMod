package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormMapBox;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.ui.ButtonColor;

public class MapForm extends Form {
   private Form mapForm;
   private FormMapBox map;
   private FormFlow buttonFlow;
   private List<FormLocalTextButton> buttons;
   private FormLocalTextButton hide;
   private FormLocalTextButton center;
   private FormLocalTextButton zoomOut;
   private FormLocalTextButton zoomIn;

   public MapForm(String var1, Client var2, int var3, boolean var4, int[] var5, int var6, Consumer<Integer> var7, boolean var8) {
      super(var1, var3, var3 + 10);
      this.drawBase = false;
      this.shouldLimitDrawArea = false;
      this.buttons = new ArrayList();
      this.buttonFlow = new FormFlow(0);
      this.mapForm = (Form)this.addComponent(new Form(var1, var3, var3));
      this.mapForm.setPosition(0, 10);
      this.map = (FormMapBox)this.mapForm.addComponent(new FormMapBox(var2, 0, 0, this.getWidth(), this.getHeight() - 10, var5, var6, var7, var8) {
         public void setCentered(boolean var1) {
            super.setCentered(var1);
            if (MapForm.this.center != null) {
               MapForm.this.center.setActive(!var1);
            }

         }
      }, -1000);
      this.map.setCentered(true);
      if (var4) {
         this.hide = this.addButton((GameMessage)(new LocalMessage("ui", "maphide")), 60);
         this.hide.onClicked((var1x) -> {
            if (this.canClickButtons()) {
               this.setMapHidden(!this.map.isHidden());
            } else {
               var1x.preventDefault();
            }

         });
      }

      this.zoomOut = this.addButton((String)"-", 20);
      this.zoomOut.onClicked((var1x) -> {
         if (this.canClickButtons()) {
            this.map.zoomOut();
         } else {
            var1x.preventDefault();
         }

      });
      this.zoomIn = this.addButton((String)"+", 20);
      this.zoomIn.onClicked((var1x) -> {
         if (this.canClickButtons()) {
            this.map.zoomIn();
         } else {
            var1x.preventDefault();
         }

      });
      this.center = this.addButton((String)"c", 20);
      this.center.onClicked((var1x) -> {
         if (this.canClickButtons()) {
            this.map.setCentered(true);
         } else {
            var1x.preventDefault();
         }

      });
      this.center.setActive(!this.map.isCentered());
      this.onWindowResized();
   }

   public static MapForm getMiniMapForm(String var0, Client var1, int var2) {
      return new MapForm(var0, var1, var2, true, new int[]{2, 4, 6, 8, 12, 16, 20, 24, 28, 32}, Settings.minimapZoomLevel, (var0x) -> {
         Settings.minimapZoomLevel = var0x;
      }, false) {
         public void onWindowResized() {
            super.onWindowResized();
            this.setPosition(Screen.getHudWidth() - this.getWidth() - 10, 10);
         }

         protected void onMapHidden(boolean var1) {
            if (Settings.minimapHidden != var1) {
               Settings.minimapHidden = var1;
               Settings.saveClientSettings();
            }

         }

         public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
            if (ControllerInput.isCursorVisible()) {
               super.addNextControllerFocus(var1, var2, var3, var4, var5, var6);
            }

         }
      };
   }

   public static MapForm getFullMapForm(String var0, Client var1) {
      int var2 = Math.min(Screen.getHudWidth(), Screen.getHudHeight());
      return new MapForm(var0, var1, var2 - 50, false, new int[]{2, 4, 6, 8, 12, 16, 20, 24, 28, 32}, Settings.islandMapZoomLevel, (var0x) -> {
         Settings.islandMapZoomLevel = var0x;
      }, true) {
         public void onWindowResized() {
            super.onWindowResized();
            int var1 = Math.min(Screen.getHudWidth(), Screen.getHudHeight());
            this.setSize(var1 - 50);
            this.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
         }
      };
   }

   public FormLocalTextButton addButton(String var1, int var2) {
      return this.addButton((GameMessage)(new StaticMessage(var1)), var2);
   }

   public FormLocalTextButton addButton(GameMessage var1, int var2) {
      FormLocalTextButton var3 = (FormLocalTextButton)this.addComponent(new FormLocalTextButton(var1, this.buttonFlow.next(var2 + 2), 0, var2, FormInputSize.SIZE_16, ButtonColor.BASE), 1000);
      this.buttons.add(var3);
      return var3;
   }

   public void setMapHidden(boolean var1) {
      this.mapForm.setHidden(var1);
      this.map.setHidden(var1);
      this.onMapHidden(var1);
      this.zoomOut.setActive(!var1);
      this.zoomIn.setActive(!var1);
      if (this.hide != null) {
         this.hide.setLocalization("ui", var1 ? "mapshow" : "maphide");
      }

   }

   public boolean isMinimized() {
      return this.mapForm.isHidden();
   }

   protected void onMapHidden(boolean var1) {
   }

   public boolean canClickButtons() {
      return !this.map.isMouseDown();
   }

   public void setSize(int var1) {
      this.setWidth(var1);
      this.setHeight(var1);
      this.mapForm.setWidth(var1);
      this.mapForm.setHeight(var1);
      this.map.setWidth(var1);
      this.map.setHeight(var1 + 10);
   }

   public boolean isMouseOver(InputEvent var1) {
      InputEvent var2 = this.getComponentList().offsetEvent(var1, false);
      Iterator var3 = this.getComponents().iterator();

      while(var3.hasNext()) {
         FormComponent var4 = (FormComponent)var3.next();
         if (var4.isMouseOver(var2)) {
            return true;
         }
      }

      return !this.mapForm.isHidden() && super.isMouseOver(var1);
   }
}
