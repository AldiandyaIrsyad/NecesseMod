package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Objects;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.gfx.GameBackground;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;

public abstract class FormInputSize {
   public static FormInputSize SIZE_32_TO_40 = new FormInputSize(32, 4, 10, 2) {
      public DrawOptions getButtonDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
            this.drawWidthComponent(Settings.UI.button_32.getButtonTexture(var1, var2), var3, var4, var5, var6);
         };
      }

      public DrawOptions getButtonDownDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
            this.drawWidthComponent(Settings.UI.button_32.getButtonDownTexture(var1, var2), var3, var4, var5, var6);
         };
      }

      public DrawOptions getButtonEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
         };
      }

      public DrawOptions getButtonDownEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
         };
      }

      public Color getButtonColor(ButtonState var1) {
         return Settings.UI.button_32.getButtonColor(Settings.UI, var1);
      }

      public Color getTextColor(ButtonState var1) {
         return Settings.UI.button_32.getTextColor(Settings.UI, var1);
      }

      public DrawOptions getInputDrawOptions(int var1, int var2, int var3) {
         return () -> {
            this.drawWidthComponent(Settings.UI.textinput_32, var1, var2, var3, Color.WHITE);
         };
      }

      public FontOptions getFontOptions() {
         return new FontOptions(16);
      }

      public Rectangle getContentRectangle(int var1) {
         return new Rectangle(3, 7, var1 - 6, 26);
      }
   };
   public static FormInputSize SIZE_32 = new FormInputSize(32, 0, 6, 2) {
      public DrawOptions getButtonDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
            this.drawWidthComponent(Settings.UI.button_32.getButtonTexture(var1, var2), var3, var4, var5, var6);
         };
      }

      public DrawOptions getButtonDownDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
            this.drawWidthComponent(Settings.UI.button_32.getButtonDownTexture(var1, var2), var3, var4, var5, var6);
         };
      }

      public DrawOptions getButtonEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
         };
      }

      public DrawOptions getButtonDownEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
         };
      }

      public Color getButtonColor(ButtonState var1) {
         return Settings.UI.button_32.getButtonColor(Settings.UI, var1);
      }

      public Color getTextColor(ButtonState var1) {
         return Settings.UI.button_32.getTextColor(Settings.UI, var1);
      }

      public DrawOptions getInputDrawOptions(int var1, int var2, int var3) {
         return () -> {
            this.drawWidthComponent(Settings.UI.textinput_32, var1, var2, var3, Color.WHITE);
         };
      }

      public FontOptions getFontOptions() {
         return new FontOptions(16);
      }

      public Rectangle getContentRectangle(int var1) {
         return new Rectangle(3, 3, var1 - 6, 26);
      }
   };
   public static FormInputSize SIZE_24 = new FormInputSize(24, 0, 4, 1) {
      public DrawOptions getButtonDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
            this.drawWidthComponent(Settings.UI.button_24.getButtonTexture(var1, var2), var3, var4, var5, var6);
         };
      }

      public DrawOptions getButtonDownDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
            this.drawWidthComponent(Settings.UI.button_24.getButtonDownTexture(var1, var2), var3, var4, var5, var6);
         };
      }

      public DrawOptions getButtonEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
         };
      }

      public DrawOptions getButtonDownEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
         };
      }

      public Color getButtonColor(ButtonState var1) {
         return Settings.UI.button_24.getButtonColor(Settings.UI, var1);
      }

      public Color getTextColor(ButtonState var1) {
         return Settings.UI.button_24.getTextColor(Settings.UI, var1);
      }

      public DrawOptions getInputDrawOptions(int var1, int var2, int var3) {
         return () -> {
            this.drawWidthComponent(Settings.UI.textinput_24, var1, var2, var3, Color.WHITE);
         };
      }

      public FontOptions getFontOptions() {
         return new FontOptions(16);
      }

      public Rectangle getContentRectangle(int var1) {
         return new Rectangle(2, 2, var1 - 4, 20);
      }
   };
   public static FormInputSize SIZE_20 = new FormInputSize(20, 0, 2, 1) {
      public DrawOptions getButtonDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
            this.drawWidthComponent(Settings.UI.button_20.getButtonTexture(var1, var2), var3, var4, var5, var6);
         };
      }

      public DrawOptions getButtonDownDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
            this.drawWidthComponent(Settings.UI.button_20.getButtonDownTexture(var1, var2), var3, var4, var5, var6);
         };
      }

      public DrawOptions getButtonEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
         };
      }

      public DrawOptions getButtonDownEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
         };
      }

      public Color getButtonColor(ButtonState var1) {
         return Settings.UI.button_20.getButtonColor(Settings.UI, var1);
      }

      public Color getTextColor(ButtonState var1) {
         return Settings.UI.button_20.getTextColor(Settings.UI, var1);
      }

      public DrawOptions getInputDrawOptions(int var1, int var2, int var3) {
         return () -> {
            this.drawWidthComponent(Settings.UI.textinput_20, var1, var2, var3, Color.WHITE);
         };
      }

      public FontOptions getFontOptions() {
         return new FontOptions(16);
      }

      public Rectangle getContentRectangle(int var1) {
         return new Rectangle(2, 2, var1 - 4, 16);
      }
   };
   public static FormInputSize SIZE_16 = new FormInputSize(16, 0, 2, 1) {
      public DrawOptions getButtonDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
            this.drawWidthComponent(Settings.UI.button_16.getButtonTexture(var1, var2), var3, var4, var5, var6);
         };
      }

      public DrawOptions getButtonDownDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
            this.drawWidthComponent(Settings.UI.button_16.getButtonDownTexture(var1, var2), var3, var4, var5, var6);
         };
      }

      public DrawOptions getButtonEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
         };
      }

      public DrawOptions getButtonDownEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6) {
         return () -> {
         };
      }

      public Color getButtonColor(ButtonState var1) {
         return Settings.UI.button_16.getButtonColor(Settings.UI, var1);
      }

      public Color getTextColor(ButtonState var1) {
         return Settings.UI.button_16.getTextColor(Settings.UI, var1);
      }

      public DrawOptions getInputDrawOptions(int var1, int var2, int var3) {
         return () -> {
            this.drawWidthComponent(Settings.UI.textinput_16, var1, var2, var3, Color.WHITE);
         };
      }

      public FontOptions getFontOptions() {
         return new FontOptions(12);
      }

      public Rectangle getContentRectangle(int var1) {
         return new Rectangle(2, 2, var1 - 4, 12);
      }
   };
   public final int height;
   public final int textureDrawOffset;
   public final int fontDrawOffset;
   public final int buttonDownContentDrawOffset;

   public static FormInputSize background(int var0, GameBackground var1, int var2) {
      return background(var0, var1, new FontOptions(var2), var0 / 2 - var2 / 2);
   }

   public static FormInputSize background(int var0, final GameBackground var1, final FontOptions var2, int var3) {
      return new FormInputSize(var0, 0, var3, 0) {
         public DrawOptions getButtonDrawOptions(ButtonColor var1x, ButtonState var2x, int var3, int var4, int var5, Color var6) {
            SharedTextureDrawOptions var7 = var1.getDrawOptions(var3, var4, var5, this.height);
            Objects.requireNonNull(var7);
            return var7::draw;
         }

         public DrawOptions getButtonDownDrawOptions(ButtonColor var1x, ButtonState var2x, int var3, int var4, int var5, Color var6) {
            SharedTextureDrawOptions var7 = var1.getDrawOptions(var3, var4, var5, this.height);
            Objects.requireNonNull(var7);
            return var7::draw;
         }

         public DrawOptions getButtonEdgeDrawOptions(ButtonColor var1x, ButtonState var2x, int var3, int var4, int var5, Color var6) {
            SharedTextureDrawOptions var7 = var1.getEdgeDrawOptions(var3, var4, var5, this.height);
            Objects.requireNonNull(var7);
            return var7::draw;
         }

         public DrawOptions getButtonDownEdgeDrawOptions(ButtonColor var1x, ButtonState var2x, int var3, int var4, int var5, Color var6) {
            SharedTextureDrawOptions var7 = var1.getEdgeDrawOptions(var3, var4, var5, this.height);
            Objects.requireNonNull(var7);
            return var7::draw;
         }

         public Color getButtonColor(ButtonState var1x) {
            return Color.WHITE;
         }

         public Color getTextColor(ButtonState var1x) {
            return Color.WHITE;
         }

         public DrawOptions getInputDrawOptions(int var1x, int var2x, int var3) {
            SharedTextureDrawOptions var4 = var1.getDrawOptions(var1x, var2x, var3, this.height);
            Objects.requireNonNull(var4);
            return var4::draw;
         }

         public FontOptions getFontOptions() {
            return var2;
         }

         public Rectangle getContentRectangle(int var1x) {
            int var2x = var1.getContentPadding();
            return new Rectangle(var2x, var2x, var1x - var2x * 2, this.height - var2x * 2);
         }
      };
   }

   public static FormInputSize empty(int var0, int var1) {
      return empty(var0, new FontOptions(var1), var0 / 2 - var1 / 2);
   }

   public static FormInputSize empty(int var0, final FontOptions var1, int var2) {
      return new FormInputSize(var0, 0, var2, 0) {
         public DrawOptions getButtonDrawOptions(ButtonColor var1x, ButtonState var2, int var3, int var4, int var5, Color var6) {
            return () -> {
            };
         }

         public DrawOptions getButtonDownDrawOptions(ButtonColor var1x, ButtonState var2, int var3, int var4, int var5, Color var6) {
            return () -> {
            };
         }

         public DrawOptions getButtonEdgeDrawOptions(ButtonColor var1x, ButtonState var2, int var3, int var4, int var5, Color var6) {
            return () -> {
            };
         }

         public DrawOptions getButtonDownEdgeDrawOptions(ButtonColor var1x, ButtonState var2, int var3, int var4, int var5, Color var6) {
            return () -> {
            };
         }

         public Color getButtonColor(ButtonState var1x) {
            return Color.WHITE;
         }

         public Color getTextColor(ButtonState var1x) {
            return var1x == ButtonState.HIGHLIGHTED ? Color.WHITE : new Color(50, 50, 50);
         }

         public DrawOptions getInputDrawOptions(int var1x, int var2, int var3) {
            return () -> {
            };
         }

         public FontOptions getFontOptions() {
            return var1;
         }

         public Rectangle getContentRectangle(int var1x) {
            return new Rectangle(var1x, this.height);
         }
      };
   }

   public static FormInputSize line(int var0, int var1) {
      return line(var0, new FontOptions(var1), var0 / 2 - var1 / 2);
   }

   public static FormInputSize line(int var0, final FontOptions var1, int var2) {
      return new FormInputSize(var0, 0, var2, 0) {
         public DrawOptions getButtonDrawOptions(ButtonColor var1x, ButtonState var2, int var3, int var4, int var5, Color var6) {
            return () -> {
            };
         }

         public DrawOptions getButtonDownDrawOptions(ButtonColor var1x, ButtonState var2, int var3, int var4, int var5, Color var6) {
            return () -> {
            };
         }

         public DrawOptions getButtonEdgeDrawOptions(ButtonColor var1x, ButtonState var2, int var3, int var4, int var5, Color var6) {
            Color var7 = Settings.UI.activeTextColor;
            return () -> {
               Screen.drawRectangleLines(new Rectangle(var3, var4, var5, this.height), (float)var7.getRed() / 255.0F, (float)var7.getGreen() / 255.0F, (float)var7.getBlue() / 255.0F, 1.0F);
            };
         }

         public DrawOptions getButtonDownEdgeDrawOptions(ButtonColor var1x, ButtonState var2, int var3, int var4, int var5, Color var6) {
            Color var7 = Settings.UI.activeTextColor;
            return () -> {
               Screen.drawRectangleLines(new Rectangle(var3, var4, var5, this.height), (float)var7.getRed() / 255.0F, (float)var7.getGreen() / 255.0F, (float)var7.getBlue() / 255.0F, 1.0F);
            };
         }

         public Color getButtonColor(ButtonState var1x) {
            return Color.WHITE;
         }

         public Color getTextColor(ButtonState var1x) {
            return var1x == ButtonState.HIGHLIGHTED ? Color.WHITE : new Color(50, 50, 50);
         }

         public DrawOptions getInputDrawOptions(int var1x, int var2, int var3) {
            return () -> {
            };
         }

         public FontOptions getFontOptions() {
            return var1;
         }

         public Rectangle getContentRectangle(int var1x) {
            return new Rectangle(var1x, this.height);
         }
      };
   }

   public FormInputSize(int var1, int var2, int var3, int var4) {
      this.height = var1;
      this.textureDrawOffset = var2;
      this.fontDrawOffset = var3;
      this.buttonDownContentDrawOffset = var4;
   }

   public FormInputSize(FormInputSize var1) {
      this.height = var1.height;
      this.textureDrawOffset = var1.textureDrawOffset;
      this.fontDrawOffset = var1.fontDrawOffset;
      this.buttonDownContentDrawOffset = var1.buttonDownContentDrawOffset;
   }

   public abstract DrawOptions getButtonDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6);

   public abstract DrawOptions getButtonDownDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6);

   public abstract DrawOptions getButtonEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6);

   public abstract DrawOptions getButtonDownEdgeDrawOptions(ButtonColor var1, ButtonState var2, int var3, int var4, int var5, Color var6);

   public abstract Color getButtonColor(ButtonState var1);

   public abstract Color getTextColor(ButtonState var1);

   public abstract DrawOptions getInputDrawOptions(int var1, int var2, int var3);

   public abstract FontOptions getFontOptions();

   public abstract Rectangle getContentRectangle(int var1);

   protected void drawWidthComponent(GameTexture var1, int var2, int var3, int var4, Color var5) {
      FormComponent.drawWidthComponent(new GameSprite(var1, 0, 0, var1.getHeight()), new GameSprite(var1, 1, 0, var1.getHeight()), var2, var3 + this.textureDrawOffset, var4, var5);
   }
}
