package necesse.gfx.drawOptions;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;

public class ProgressBarDrawOptions {
   protected final GameTexture background;
   protected final int width;
   protected LinkedList<ProgressBarOptions> bars = new LinkedList();
   protected Color backgroundColor;
   protected FontOptions fontOptions;
   protected GameMessage text;
   protected int limitText;
   protected int textAlign;

   public ProgressBarDrawOptions(GameTexture var1, int var2) {
      this.backgroundColor = Color.WHITE;
      this.fontOptions = new FontOptions(16);
      this.text = null;
      this.limitText = -1;
      this.textAlign = 1;
      this.background = var1;
      this.width = var2;
   }

   public ProgressBarDrawOptions color(Color var1) {
      this.backgroundColor = var1;
      return this;
   }

   public ProgressBarDrawOptions text(GameMessage var1) {
      this.text = var1;
      return this;
   }

   public ProgressBarDrawOptions text(String var1) {
      return this.text((GameMessage)(new StaticMessage(var1)));
   }

   public ProgressBarDrawOptions limitText(int var1) {
      this.limitText = var1;
      return this;
   }

   public ProgressBarDrawOptions textAlignLeft() {
      this.textAlign = 0;
      return this;
   }

   public ProgressBarDrawOptions textAlignCenter() {
      this.textAlign = 1;
      return this;
   }

   public ProgressBarDrawOptions textAlignRight() {
      this.textAlign = 2;
      return this;
   }

   public ProgressBarDrawOptions fontOptions(FontOptions var1) {
      this.fontOptions = var1;
      return this;
   }

   public ProgressBarOptions addBar(GameTexture var1, float var2) {
      ProgressBarOptions var3 = new ProgressBarOptions(var1, var2);
      this.bars.add(var3);
      return var3;
   }

   public DrawOptions pos(int var1, int var2) {
      DrawOptionsList var3 = new DrawOptionsList();
      var3.add(() -> {
         FormComponent.drawWidthComponent(new GameSprite(this.background, 0, 0, this.background.getHeight()), new GameSprite(this.background, 1, 0, this.background.getHeight()), var1, var2, this.width, this.backgroundColor);
      });
      Iterator var4 = this.bars.iterator();

      while(var4.hasNext()) {
         ProgressBarOptions var5 = (ProgressBarOptions)var4.next();
         var3.add(var5.pos(var1, var2));
      }

      if (this.text != null) {
         int var10 = Math.max(this.limitText, 0);
         String var11 = this.limitText >= 0 ? GameUtils.maxString(this.text.translate(), this.fontOptions, this.width - var10 * 2) : this.text.translate();
         int var6 = FontManager.bit.getWidthCeil(var11, this.fontOptions);
         int var7 = this.fontOptions.getSize();
         int var9 = var2 + this.background.getHeight() / 2 - var7 / 2;
         int var8;
         switch (this.textAlign) {
            case 0:
               var8 = var1 + var10;
               break;
            case 2:
               var8 = var1 + this.width - var6 - var10;
               break;
            default:
               var8 = var1 + this.width / 2 - var6 / 2;
         }

         var3.add(() -> {
            FontManager.bit.drawString((float)var8, (float)var9, var11, this.fontOptions);
         });
      }

      return var3;
   }

   public void draw(int var1, int var2) {
      this.pos(var1, var2).draw();
   }

   public class ProgressBarOptions {
      protected final GameTexture texture;
      protected float percent;
      protected Color color;
      protected int minWidth;

      public ProgressBarOptions(GameTexture var2, float var3) {
         this.color = Color.WHITE;
         this.texture = var2;
         this.percent = var3;
      }

      public ProgressBarOptions color(Color var1) {
         this.color = var1;
         return this;
      }

      public ProgressBarOptions minWidth(int var1) {
         this.minWidth = var1;
         return this;
      }

      public ProgressBarDrawOptions end() {
         return ProgressBarDrawOptions.this;
      }

      protected DrawOptions pos(int var1, int var2) {
         int var3 = ProgressBarDrawOptions.this.background.getHeight() - this.texture.getHeight();
         int var4 = var3 / 2;
         float var5;
         if (this.minWidth > 0) {
            float var6 = (float)this.minWidth / (float)ProgressBarDrawOptions.this.width;
            var5 = GameMath.lerp(this.percent, var6, 1.0F);
         } else {
            var5 = this.percent;
         }

         int var7 = (int)Math.ceil((double)((float)(ProgressBarDrawOptions.this.width - var3) * var5));
         return var7 > 0 ? () -> {
            FormComponent.drawWidthComponent(new GameSprite(this.texture, 0, 0, this.texture.getHeight()), new GameSprite(this.texture, 1, 0, this.texture.getHeight()), var1 + var4, var2 + var4, var7, this.color);
         } : () -> {
         };
      }
   }
}
