package necesse.gfx.forms.components.lists;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.shader.GameShader;

public class FormShaderList extends FormSelectedList<ShaderElement> {
   private FormEventsHandler<FormShaderSelectEvent> shaderSelect = new FormEventsHandler();

   public FormShaderList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 20);
   }

   public FormShaderList onShaderSelect(FormEventListener<FormShaderSelectEvent> var1) {
      this.shaderSelect.addListener(var1);
      return this;
   }

   public void reset() {
      super.reset();
      GameResources.getShaders().forEach((var1, var2) -> {
         this.elements.add(new ShaderElement(var1, var2));
      });
   }

   public class ShaderElement extends FormSelectedElement<FormShaderList> {
      public String name;
      public GameShader shader;

      public ShaderElement(String var2, GameShader var3) {
         this.name = var2;
         this.shader = var3;
      }

      void draw(FormShaderList var1, TickManager var2, PlayerMob var3, int var4) {
         Color var5 = this.isSelected() ? Settings.UI.highlightTextColor : Settings.UI.activeTextColor;
         String var6 = this.name;
         FontOptions var7 = (new FontOptions(16)).color(var5);
         String var8 = GameUtils.maxString(var6, var7, var1.width - 20);
         FontManager.bit.drawString(10.0F, 2.0F, var8, var7);
         if (this.isMouseOver(var1) && !var8.equals(var6)) {
            Screen.addTooltip(new StringTooltips(var6), TooltipLocation.FORM_FOCUS);
         }

      }

      void onClick(FormShaderList var1, int var2, InputEvent var3, PlayerMob var4) {
         if (var3.getID() == -100) {
            super.onClick((FormSelectedList)var1, var2, var3, var4);
            if (this.isSelected()) {
               FormShaderList.this.playTickSound();
               var1.shaderSelect.onEvent(FormShaderList.this.new FormShaderSelectEvent(var1, var2, this.name, this.shader));
            }

         }
      }

      void onControllerEvent(FormShaderList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            super.onControllerEvent((FormSelectedList)var1, var2, var3, var4, var5);
            if (this.isSelected()) {
               FormShaderList.this.playTickSound();
               var1.shaderSelect.onEvent(FormShaderList.this.new FormShaderSelectEvent(var1, var2, this.name, this.shader));
            }

            var3.use();
         }
      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormSelectedList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormShaderList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormSelectedList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormShaderList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormShaderList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormShaderList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormShaderList)var1, var2, var3, var4);
      }
   }

   public class FormShaderSelectEvent extends FormEvent<FormShaderList> {
      public final int index;
      public final String name;
      public final GameShader shader;

      public FormShaderSelectEvent(FormShaderList var2, int var3, String var4, GameShader var5) {
         super(var2);
         this.index = var3;
         this.name = var4;
         this.shader = var5;
      }
   }
}
