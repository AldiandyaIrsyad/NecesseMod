package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.util.FloatDimension;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FairControlKeyGlyph implements FairGlyph {
   public final FontOptions fontOptions;
   public final Control control;
   private int lastKey;
   private boolean lastIsController;
   private TextureDrawOptionsEnd controllerGlyph;
   private boolean controllerNotBound;
   private int width;
   private int height;
   private String tooltip;
   private boolean isHovering;

   public FairControlKeyGlyph(FontOptions var1, Control var2, String var3) {
      this.fontOptions = var1;
      this.control = var2;
      this.lastKey = var2.getKey();
      this.updateDimensions();
      this.tooltip = var3;
   }

   public FairControlKeyGlyph(FontOptions var1, Control var2) {
      this(var1, var2, (String)null);
   }

   public FairControlKeyGlyph(Control var1, String var2) {
      this(new FontOptions(16), var1, var2);
   }

   public FairControlKeyGlyph(Control var1) {
      this(new FontOptions(16), var1);
   }

   public FloatDimension getDimensions() {
      if (this.lastKey != this.control.getKey() || this.lastIsController != Input.lastInputIsController) {
         this.updateDimensions();
         this.lastKey = this.control.getKey();
         this.lastIsController = Input.lastInputIsController;
      }

      return new FloatDimension((float)this.width, (float)this.height);
   }

   public void updateDimensions() {
      if (Input.lastInputIsController && this.control.controllerState != null) {
         GameTexture var1 = ControllerInput.getButtonGlyph(this.control.controllerState);
         if (var1 != null) {
            this.controllerGlyph = var1.initDraw().size(16, false);
         } else {
            this.controllerNotBound = true;
         }
      } else {
         this.controllerNotBound = false;
         this.controllerGlyph = null;
      }

      if (this.controllerGlyph != null) {
         this.width = this.controllerGlyph.getWidth() + (this.tooltip == null ? 0 : 4 + FontManager.bit.getWidthCeil(this.tooltip, this.fontOptions));
      } else if (this.controllerNotBound) {
         this.width = Control.getControlIconWidth(this.fontOptions, (String)null, (Control)null, "?", this.tooltip);
      } else {
         this.width = Control.getControlIconWidth(this.fontOptions, (String)null, this.control, Input.getName(this.control.getKey()), this.tooltip);
      }

      this.height = Math.max(16, this.fontOptions.getSize());
   }

   public void handleInputEvent(float var1, float var2, InputEvent var3) {
      if (var3.isMouseMoveEvent()) {
         Dimension var4 = this.getDimensions().toInt();
         this.isHovering = (new Rectangle((int)var1, (int)var2 - var4.height - 4, var4.width, var4.height)).contains(var3.pos.hudX, var3.pos.hudY);
      }

   }

   public void draw(float var1, float var2, Color var3) {
      String var4 = Input.getName(this.control.getKey());
      if (this.controllerGlyph != null) {
         this.controllerGlyph.draw((int)var1, (int)var2 - this.height);
         if (this.tooltip != null) {
            FontManager.bit.drawString(var1 + (float)this.controllerGlyph.getWidth() + 4.0F, var2, this.tooltip, this.fontOptions);
         }
      } else if (this.controllerNotBound) {
         Control.drawControlIcon(this.fontOptions, (int)var1 + 1, (int)var2 - this.height - 2, (String)null, (Control)null, "?", this.tooltip);
      } else {
         Control.drawControlIcon(this.fontOptions, (int)var1 + 1, (int)var2 - this.height - 2, (String)null, this.control, var4, this.tooltip);
      }

      if (this.isHovering) {
         if (this.tooltip != null) {
            Screen.addTooltip(new StringTooltips(this.tooltip), TooltipLocation.FORM_FOCUS);
         } else {
            Screen.addTooltip(new StringTooltips(var4), TooltipLocation.FORM_FOCUS);
         }
      }

   }

   public FairGlyph getTextBoxCharacter() {
      return this;
   }

   public String getParseString() {
      return TypeParsers.getInputParseString(this.control);
   }
}
