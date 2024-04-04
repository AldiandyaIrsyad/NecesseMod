package necesse.engine.control;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.Screen;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;

public class ControllerGlyphTip {
   public static FontOptions fontOptions = new FontOptions(20);
   public final String text;
   private LinkedList<ControllerState> states = new LinkedList();
   private HashSet<ControllerState> addedStates = new HashSet();
   private LinkedList<GameTexture> glyphs = new LinkedList();
   private int width;

   public static int getHeight() {
      return fontOptions.getSize() + 4;
   }

   public ControllerGlyphTip(String var1, ControllerState... var2) {
      this.text = var1;
      this.addGlyphs(var2);
   }

   public boolean addGlyphs(ControllerState... var1) {
      boolean var2 = false;
      ControllerState[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ControllerState var6 = var3[var5];
         if (!this.addedStates.contains(var6)) {
            this.addedStates.add(var6);
            this.states.add(var6);
            var2 = true;
         }
      }

      if (var2) {
         this.updateWidth();
      }

      return var2;
   }

   public void updateWidth() {
      this.glyphs.clear();
      this.width = FontManager.bit.getWidthCeil(this.text, fontOptions);
      Iterator var1 = this.states.iterator();

      while(var1.hasNext()) {
         ControllerState var2 = (ControllerState)var1.next();
         GameTexture var3 = ControllerInput.getButtonGlyph(var2);
         if (var3 != null) {
            this.glyphs.add(var3);
            TextureDrawOptionsEnd var4 = var3.initDraw().size(fontOptions.getSize(), false);
            this.width += var4.getWidth() + 4;
         }
      }

      if (this.glyphs.isEmpty()) {
         this.width += Control.getControlIconWidth(fontOptions, (String)null, (Control)null, "?", (String)null);
      }

      this.width += 4;
   }

   public int getWidth() {
      return this.width;
   }

   public void draw(int var1, int var2) {
      Screen.initQuadDraw(this.width, fontOptions.getSize() + 4).color(new Color(0, 0, 0, 100)).draw(var1 - 2, var2 - 2);
      TextureDrawOptionsEnd var5;
      if (this.glyphs.isEmpty()) {
         Control.DrawFlow var3 = Control.getDrawControlLogic(fontOptions, var1, var2 - 2, (String)null, (Control)null, "?", (String)null);
         var3.draw();
         var1 += var3.width;
      } else {
         for(Iterator var6 = this.glyphs.iterator(); var6.hasNext(); var1 += var5.getWidth() + 4) {
            GameTexture var4 = (GameTexture)var6.next();
            var5 = var4.initDraw().size(fontOptions.getSize(), false);
            var5.draw(var1, var2);
         }
      }

      FontManager.bit.drawString((float)var1, (float)var2, this.text, fontOptions);
   }
}
