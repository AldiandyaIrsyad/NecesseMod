package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.Comparator;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormControlList extends FormSelectedList<ControlElement> {
   protected FormEventsHandler<FormInputEvent<FormControlList>> changedEvents = new FormEventsHandler();

   public FormControlList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 20);
      this.limitListElementDraw = false;
   }

   public void reset() {
      super.reset();
      Comparator var1 = Comparator.comparingInt((var0) -> {
         return var0.sort;
      });
      var1 = var1.thenComparing((var0) -> {
         return var0.displayName.translate();
      });
      Control.streamGroups().sorted(var1).forEach((var1x) -> {
         this.elements.add(new ControlElement(var1x.displayName));
         Iterator var2 = var1x.getControls().iterator();

         while(var2.hasNext()) {
            Control var3 = (Control)var2.next();
            this.elements.add(new ControlElement(var3));
         }

      });
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      ControlElement var4 = (ControlElement)this.getSelectedElement();
      if (var4 != null && var1.getID() == 256) {
         if (!var1.state) {
            if (var4.control != null) {
               var4.control.changeKey(-1);
               this.updateOverlaps();
               this.playTickSound();
               this.changedEvents.onEvent(new FormInputEvent(this, var1));
            }

            this.clearSelected();
         }

         var1.use();
      } else {
         if (var4 != null && var4.control != null && var1.state) {
            if (var1.isMouseClickEvent() || var1.isMouseWheelEvent()) {
               ControlElement var5 = (ControlElement)this.getMouseOverElement(var1);
               if (var5 == var4) {
                  var4.control.changeKey(var1.getID());
                  this.updateOverlaps();
                  this.clearSelected();
                  this.playTickSound();
                  this.changedEvents.onEvent(new FormInputEvent(this, var1));
                  return;
               }

               this.clearSelected();
            }

            if (var1.getID() > 0) {
               var4.control.changeKey(var1.getID());
               this.updateOverlaps();
               this.clearSelected();
               this.playTickSound();
               this.changedEvents.onEvent(new FormInputEvent(this, var1));
               return;
            }
         }

         super.handleInputEvent(var1, var2, var3);
      }
   }

   public FormControlList onChanged(FormEventListener<FormInputEvent<FormControlList>> var1) {
      this.changedEvents.addListener(var1);
      return this;
   }

   private void updateOverlaps() {
      this.elements.forEach((var0) -> {
         var0.controlOverlapped = false;
      });
      Iterator var1 = this.elements.iterator();

      while(true) {
         ControlElement var2;
         do {
            do {
               if (!var1.hasNext()) {
                  return;
               }

               var2 = (ControlElement)var1.next();
            } while(var2.isHeader());
         } while(var2.controlOverlapped);

         Iterator var3 = this.elements.iterator();

         while(var3.hasNext()) {
            ControlElement var4 = (ControlElement)var3.next();
            if (!var4.isHeader() && !var4.controlOverlapped && var2.control.overlaps(var4.control)) {
               var2.controlOverlapped = true;
               var4.controlOverlapped = true;
            }
         }
      }
   }

   public class ControlElement extends FormSelectedElement<FormControlList> {
      public Control control;
      public boolean controlOverlapped;
      public GameMessage header;

      public ControlElement(Control var2) {
         this.control = var2;
      }

      public ControlElement(GameMessage var2) {
         this.header = var2;
      }

      public boolean isHeader() {
         return this.control == null;
      }

      void draw(FormControlList var1, TickManager var2, PlayerMob var3, int var4) {
         if (this.isHeader()) {
            String var5 = this.header.translate();
            FontOptions var6 = (new FontOptions(20)).color(Settings.UI.activeTextColor);
            int var7 = FontManager.bit.getWidthCeil(var5, var6);
            FontManager.bit.drawString((float)(var1.width / 2 - var7 / 2), 0.0F, var5, var6);
         } else {
            Color var11 = Settings.UI.activeTextColor;
            String var12;
            if (this.isSelected()) {
               var12 = "???";
            } else if (this.control.getKey() == -1) {
               var12 = "---";
            } else {
               var12 = this.control.getKeyName();
               if (this.controlOverlapped) {
                  var11 = new Color(150, 50, 50);
               }
            }

            FontOptions var13 = (new FontOptions(16)).color(var11);
            int var8 = FontManager.bit.getWidthCeil(var12, var13);
            FontManager.bit.drawString((float)(FormControlList.this.width - var8 - 5), 2.0F, var12, var13);
            String var9 = this.control.text.translate();
            String var10 = GameUtils.maxString(var9, var13, FormControlList.this.width - var8 - 10);
            FontManager.bit.drawString(5.0F, 2.0F, var10, var13);
            if (!var10.equals(var9) && this.isMouseOver(var1)) {
               Screen.addTooltip(new StringTooltips(var9), TooltipLocation.FORM_FOCUS);
            }

            if (this.control.tooltip != null && this.isMouseOver(var1)) {
               Screen.addTooltip(new StringTooltips(this.control.tooltip.translate(), 400), TooltipLocation.FORM_FOCUS);
            }
         }

      }

      void onControllerEvent(FormControlList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormSelectedList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormControlList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormControlList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormControlList)var1, var2, var3, var4);
      }
   }
}
