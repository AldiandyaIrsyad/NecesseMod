package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.LinkedList;
import necesse.engine.control.InputEvent;
import necesse.gfx.GameColor;
import necesse.gfx.gameFont.FontOptions;

public abstract class FairTypeDrawOptions {
   public final FairType.TextAlign align;
   public final int maxWidth;
   public final int maxLines;
   public final boolean forceMaxWidth;
   public final boolean cutLastLineWord;
   public final boolean removeWhitespaceLeft;
   public final boolean removeWhiteSpaceRight;
   public final FontOptions ellipsisFO;

   public FairTypeDrawOptions(FairType.TextAlign var1, int var2, boolean var3, int var4, boolean var5, FontOptions var6, boolean var7, boolean var8) {
      this.align = var1;
      this.maxWidth = var2;
      this.forceMaxWidth = var3;
      this.maxLines = var4;
      this.cutLastLineWord = var5;
      this.ellipsisFO = var6;
      this.removeWhitespaceLeft = var7;
      this.removeWhiteSpaceRight = var8;
   }

   public abstract void handleInputEvent(int var1, int var2, InputEvent var3);

   public void draw(int var1, int var2) {
      this.draw(var1, var2, (Color)GameColor.DEFAULT_COLOR.get());
   }

   public abstract Rectangle getBoundingBox(int var1, int var2);

   public Rectangle getBoundingBox() {
      return this.getBoundingBox(0, 0);
   }

   public abstract void draw(int var1, int var2, Color var3);

   public abstract LinkedList<GlyphContainer> getDrawList();

   public abstract int getLineCount();

   public abstract boolean displaysEverything();

   public abstract FairType getType();

   public abstract boolean shouldUpdate();
}
