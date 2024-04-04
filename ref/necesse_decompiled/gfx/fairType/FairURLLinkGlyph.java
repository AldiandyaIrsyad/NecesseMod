package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.net.URI;
import java.util.Objects;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.util.FloatDimension;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FairURLLinkGlyph implements FairGlyph {
   public final FairGlyph glyph;
   public URI uri;
   public boolean parsedGlyph;
   private boolean isHovering;

   public FairURLLinkGlyph(FairGlyph var1, URI var2, boolean var3) {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      this.glyph = var1;
      this.uri = var2;
      this.parsedGlyph = var3;
   }

   public FloatDimension getDimensions() {
      return this.glyph.getDimensions();
   }

   public void updateDimensions() {
      this.glyph.updateDimensions();
   }

   public void handleInputEvent(float var1, float var2, InputEvent var3) {
      if (var3.isMouseMoveEvent()) {
         Dimension var4 = this.getDimensions().toInt();
         this.isHovering = (new Rectangle((int)var1, (int)var2 - var4.height, var4.width, var4.height)).contains(var3.pos.hudX, var3.pos.hudY);
      }

      if (this.isHovering && var3.getID() == -100) {
         if (!var3.state) {
            GameUtils.openURL(this.uri);
         }

         var3.use();
      }

   }

   public void draw(float var1, float var2, Color var3) {
      this.glyph.draw(var1, var2, var3);
      if (this.isHovering) {
         Screen.setCursor(Screen.CURSOR.INTERACT);
         Screen.addTooltip(new StringTooltips(Localization.translate("misc", "openurl", "url", this.uri.toString())), TooltipLocation.FORM_FOCUS);
      }

   }

   public FairGlyph getTextBoxCharacter() {
      return new FairURLLinkGlyph(this.glyph.getTextBoxCharacter(), this.uri, this.parsedGlyph);
   }

   public boolean isWhiteSpaceGlyph() {
      return this.glyph.isWhiteSpaceGlyph();
   }

   public boolean isNewLineGlyph() {
      return this.glyph.isNewLineGlyph();
   }

   public Supplier<Supplier<Color>> getDefaultColor() {
      return this.glyph.getDefaultColor();
   }

   public char getCharacter() {
      return this.glyph.getCharacter();
   }

   public String getParseString() {
      return this.glyph.getParseString();
   }

   public boolean canBeParsed() {
      return this.glyph.canBeParsed();
   }
}
