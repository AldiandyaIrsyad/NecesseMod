package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.util.FloatDimension;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import org.lwjgl.system.Platform;

public class FairCharacterGlyph implements FairGlyph {
   private static final char[] INVISIBLE_CHARACTERS = new char[]{'\n', '\t', '\b', '\r'};
   private static final HashMap<Character, Function<FontOptions, FloatDimension>> SPECIAL_CHARACTERS = new HashMap();
   private final FontOptions fontOptions;
   private final char character;
   private final boolean invisibleChar;
   private FloatDimension dimension;
   private final Function<InputEvent, Boolean> onEvent;
   private final Supplier<GameTooltips> tooltipsSupplier;
   private boolean isHovering;
   public int drawYOffset;

   public FairCharacterGlyph(FontOptions var1, char var2, Function<InputEvent, Boolean> var3, Supplier<GameTooltips> var4) {
      this.fontOptions = var1;
      this.character = var2;
      this.onEvent = var3;
      this.tooltipsSupplier = var4;
      boolean var5 = false;
      char[] var6 = INVISIBLE_CHARACTERS;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         char var9 = var6[var8];
         if (var2 == var9) {
            var5 = true;
            break;
         }
      }

      this.invisibleChar = var5;
      this.updateDimensions();
   }

   public FairCharacterGlyph(FontOptions var1, char var2, Function<InputEvent, Boolean> var3) {
      this(var1, var2, var3, (Supplier)null);
   }

   public FairCharacterGlyph(FontOptions var1, char var2, Supplier<GameTooltips> var3) {
      this(var1, var2, (Function)null, var3);
   }

   public FairCharacterGlyph(FontOptions var1, char var2) {
      this(var1, var2, (Function)null, (Supplier)null);
   }

   public FloatDimension getDimensions() {
      return this.dimension;
   }

   public void updateDimensions() {
      Function var1 = (Function)SPECIAL_CHARACTERS.get(this.character);
      if (var1 == null) {
         this.dimension = new FloatDimension(this.invisibleChar ? 0.0F : FontManager.bit.getWidth(this.character, this.fontOptions), FontManager.bit.getHeight(this.character, this.fontOptions));
      } else {
         this.dimension = (FloatDimension)var1.apply(this.fontOptions);
      }

   }

   public void handleInputEvent(float var1, float var2, InputEvent var3) {
      if (var3.isMouseMoveEvent()) {
         Dimension var4 = this.getDimensions().toInt();
         this.isHovering = (new Rectangle((int)var1, (int)var2 - var4.height + this.drawYOffset, var4.width, var4.height)).contains(var3.pos.hudX, var3.pos.hudY);
      }

      if (this.onEvent != null && this.isHovering && (Boolean)this.onEvent.apply(var3)) {
         var3.use();
      }

   }

   public void draw(float var1, float var2, Color var3) {
      if (!this.invisibleChar) {
         this.fontOptions.defaultColor(var3);
         FontManager.bit.drawString(var1, var2 - this.dimension.height + (float)this.drawYOffset, String.valueOf(this.character), this.fontOptions);
      }

      if (this.isHovering) {
         if (this.onEvent != null) {
            Screen.setCursor(Screen.CURSOR.INTERACT);
         }

         if (this.tooltipsSupplier != null) {
            GameTooltips var4 = (GameTooltips)this.tooltipsSupplier.get();
            if (var4 != null) {
               Screen.addTooltip(var4, TooltipLocation.FORM_FOCUS);
            }
         }
      }

   }

   public FairGlyph getTextBoxCharacter() {
      return new FairCharacterGlyph((new FontOptions(this.fontOptions)).clearOutline(), this.character, this.onEvent, this.tooltipsSupplier);
   }

   public boolean isWhiteSpaceGlyph() {
      return this.character == ' ' || this.isNewLineGlyph();
   }

   public boolean isNewLineGlyph() {
      return this.character == '\n';
   }

   public char getCharacter() {
      return this.character;
   }

   public String toString() {
      return String.valueOf(this.character);
   }

   public static FairCharacterGlyph[] fromString(String var0, Function<Character, FairCharacterGlyph> var1) {
      FairCharacterGlyph[] var2 = new FairCharacterGlyph[var0.length()];

      for(int var3 = 0; var3 < var0.length(); ++var3) {
         var2[var3] = (FairCharacterGlyph)var1.apply(var0.charAt(var3));
      }

      return var2;
   }

   public static FairCharacterGlyph[] fromString(FontOptions var0, String var1, Function<InputEvent, Boolean> var2, Supplier<GameTooltips> var3) {
      return fromString(var1, (var3x) -> {
         return new FairCharacterGlyph(var0, var3x, var2, var3);
      });
   }

   public static FairCharacterGlyph[] fromString(FontOptions var0, String var1) {
      return fromString(var0, var1, (Function)null, (Supplier)null);
   }

   public static FairCharacterGlyph[] fromStringToOpenFile(FontOptions var0, String var1, File var2) {
      return fromString(var0, var1, (var1x) -> {
         if (var1x.getID() == -100) {
            if (!var1x.state) {
               GameUtils.openExplorerAtFile(var2);
            }

            return true;
         } else {
            return false;
         }
      }, () -> {
         return Platform.get() == Platform.WINDOWS ? new StringTooltips(Localization.translate("misc", "shotexplore")) : new StringTooltips(Localization.translate("misc", "shotopen"));
      });
   }

   public boolean canBeParsed() {
      return this.onEvent == null && this.tooltipsSupplier == null;
   }

   static {
      SPECIAL_CHARACTERS.put('\t', (var0) -> {
         return new FloatDimension(FontManager.bit.getWidth(' ', var0) * 2.0F, FontManager.bit.getHeight(' ', var0));
      });
   }
}
