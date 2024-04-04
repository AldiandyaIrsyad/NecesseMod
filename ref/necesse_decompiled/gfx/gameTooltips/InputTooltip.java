package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.ControllerState;
import necesse.engine.control.Input;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;

public class InputTooltip implements GameTooltips {
   public final Control control;
   public final ControllerState controllerState;
   private TextureDrawOptionsEnd controllerGlyph;
   private boolean controllerNotBound;
   public final String modifierKey;
   public final int inputKey;
   public final String tooltip;
   public final int width;
   public final int height;
   public final String keyName;
   public final FontOptions fontOptions;

   protected InputTooltip(Control var1, ControllerState var2, String var3, int var4, String var5, float var6) {
      this.control = var1;
      if (var1 != null && Input.lastInputIsController) {
         this.controllerState = var1.controllerState;
      } else {
         this.controllerState = var2;
      }

      this.modifierKey = var3;
      this.inputKey = var4;
      this.tooltip = var5;
      this.fontOptions = (new FontOptions(Settings.tooltipTextSize)).outline().alphaf(var6);
      this.keyName = Input.getName(var4);
      this.width = this.calcWidth();
      this.height = 20;
   }

   public InputTooltip(String var1, int var2, String var3, float var4) {
      this((Control)null, (ControllerState)null, var1, var2, var3, var4);
   }

   public InputTooltip(int var1, String var2, float var3) {
      this((String)null, var1, var2, var3);
   }

   public InputTooltip(String var1, int var2, String var3) {
      this(var1, var2, var3, 1.0F);
   }

   public InputTooltip(int var1, String var2) {
      this((String)null, var1, var2);
   }

   public InputTooltip(String var1, Control var2, String var3, float var4) {
      this(var2, (ControllerState)null, var1, var2.getKey(), var3, var4);
   }

   public InputTooltip(Control var1, String var2, float var3) {
      this((String)null, (Control)var1, var2, var3);
   }

   public InputTooltip(String var1, Control var2, String var3) {
      this(var1, var2, var3, 1.0F);
   }

   public InputTooltip(Control var1, String var2) {
      this((String)null, (Control)var1, var2);
   }

   public InputTooltip(String var1, ControllerState var2, String var3, float var4) {
      this((Control)null, var2, var1, -1, var3, var4);
   }

   public InputTooltip(ControllerState var1, String var2, float var3) {
      this((String)null, (ControllerState)var1, var2, var3);
   }

   public InputTooltip(String var1, ControllerState var2, String var3) {
      this(var1, var2, var3, 1.0F);
   }

   public InputTooltip(ControllerState var1, String var2) {
      this((String)null, (ControllerState)var1, var2);
   }

   private int calcWidth() {
      if (this.controllerState != null) {
         GameTexture var1 = ControllerInput.getButtonGlyph(this.controllerState);
         if (var1 != null) {
            this.controllerGlyph = var1.initDraw().size(16, false);
         } else {
            this.controllerNotBound = true;
         }
      }

      if (this.controllerGlyph != null) {
         return this.controllerGlyph.getWidth() + (this.tooltip == null ? 0 : 4 + FontManager.bit.getWidthCeil(this.tooltip, this.fontOptions));
      } else if (this.controllerNotBound) {
         return Control.getControlIconWidth(this.fontOptions, this.modifierKey, (Control)null, "?", this.tooltip);
      } else {
         return this.control != null ? Control.getControlIconWidth(this.fontOptions, this.modifierKey, this.control, this.keyName, this.tooltip) : Control.getControlIconWidth(this.fontOptions, this.modifierKey, this.inputKey, this.keyName, this.tooltip);
      }
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      FontOptions var4 = new FontOptions(this.fontOptions);
      if (var3 != null) {
         var4.defaultColor((Color)var3.get());
      }

      if (this.controllerGlyph != null) {
         this.controllerGlyph.draw(var1, var2);
         if (this.tooltip != null) {
            FontManager.bit.drawString((float)(var1 + this.controllerGlyph.getWidth() + 4), (float)var2, this.tooltip, var4);
         }
      } else if (this.controllerNotBound) {
         Control.drawControlIcon(var4, var1, var2, this.modifierKey, (Control)null, "?", this.tooltip);
      } else if (this.control != null) {
         Control.drawControlIcon(var4, var1, var2, this.modifierKey, this.control, this.keyName, this.tooltip);
      } else {
         Control.drawControlIcon(var4, var1, var2, this.modifierKey, this.inputKey, this.keyName, this.tooltip);
      }

   }

   public int getDrawOrder() {
      return Integer.MIN_VALUE;
   }
}
