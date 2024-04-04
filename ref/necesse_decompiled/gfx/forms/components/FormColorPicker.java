package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameResources;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.ui.HUD;
import org.lwjgl.opengl.GL11;

public class FormColorPicker extends FormComponent implements FormPositionContainer {
   private static final int HUE_BAR_HEIGHT = 30;
   private static final int HUE_BAR_PADDING = 10;
   public Color backgroundColor;
   private FormPosition position;
   private int width;
   private int height;
   private boolean mouseDownHueBar;
   private boolean mouseDownSpectrum;
   private Point mouseDownPoint;
   private InputEvent mouseDownEvent;
   private boolean isControllerSelected;
   private boolean isControllerControllingHue;
   private float currentHue;
   private Color currentSelected;
   private Point currentPoint;
   protected FormEventsHandler<FormEvent<FormColorPicker>> changedEvents;
   protected ControllerFocusHandler colorSelectorHandler;
   protected ControllerFocusHandler hueSelectorHandler;

   public FormColorPicker(int var1, int var2, int var3, int var4) {
      this.backgroundColor = GameBackground.form.getCenterColor();
      this.currentHue = 0.0F;
      this.currentSelected = null;
      this.currentPoint = null;
      this.changedEvents = new FormEventsHandler();
      this.colorSelectorHandler = new ControllerFocusHandler() {
         public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
            if (var1.getState() == ControllerInput.MENU_SELECT) {
               if (FormColorPicker.this.isControllerFocus(this) && var1.buttonState) {
                  FormColorPicker.this.isControllerSelected = true;
                  FormColorPicker.this.isControllerControllingHue = false;
                  if (FormColorPicker.this.currentPoint == null) {
                     FormColorPicker.this.currentPoint = new Point(0, 0);
                  }

                  var1.use();
               }
            } else if ((var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU) && FormColorPicker.this.isControllerSelected && var1.buttonState) {
               FormColorPicker.this.isControllerSelected = false;
               var1.use();
            }

         }

         public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
            if (FormColorPicker.this.isControllerSelected && !FormColorPicker.this.isControllerControllingHue) {
               if (FormColorPicker.this.currentPoint == null) {
                  FormColorPicker.this.currentPoint = new Point(0, 0);
               }

               switch (var1) {
                  case 0:
                     if (FormColorPicker.this.currentPoint.y > 0) {
                        FormColorPicker.this.currentPoint.y = Math.max(FormColorPicker.this.currentPoint.y - 3, 0);
                        FormColorPicker.this.queryNewColor();
                     }

                     var2.use();
                     break;
                  case 1:
                     if (FormColorPicker.this.currentPoint.x < FormColorPicker.this.width) {
                        FormColorPicker.this.currentPoint.x = Math.min(FormColorPicker.this.currentPoint.x + 3, FormColorPicker.this.width - 1);
                        FormColorPicker.this.queryNewColor();
                     }

                     var2.use();
                     break;
                  case 2:
                     int var5 = FormColorPicker.this.getSpectrumHeight();
                     if (FormColorPicker.this.currentPoint.y < var5) {
                        FormColorPicker.this.currentPoint.y = Math.min(FormColorPicker.this.currentPoint.y + 3, var5 - 1);
                        FormColorPicker.this.queryNewColor();
                     }

                     var2.use();
                     break;
                  case 3:
                     if (FormColorPicker.this.currentPoint.x > 0) {
                        FormColorPicker.this.currentPoint.x = Math.max(FormColorPicker.this.currentPoint.x - 3, 0);
                        FormColorPicker.this.queryNewColor();
                     }

                     var2.use();
               }

               return true;
            } else {
               return false;
            }
         }

         public void drawControllerFocus(ControllerFocus var1) {
            Rectangle var2 = var1.boundingBox;
            byte var3 = 5;
            var2 = new Rectangle(var2.x - var3, var2.y - var3, var2.width + var3 * 2, var2.height + var3 * 2);
            Color var4 = FormColorPicker.this.isControllerSelected && !FormColorPicker.this.isControllerControllingHue ? Settings.UI.controllerFocusBoundsHighlightColor : Settings.UI.controllerFocusBoundsColor;
            HUD.selectBoundOptions(var4, true, var2).draw();
            if (!FormColorPicker.this.isControllerSelected || FormColorPicker.this.isControllerControllingHue) {
               Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
            }

         }
      };
      this.hueSelectorHandler = new ControllerFocusHandler() {
         public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
            if (var1.getState() == ControllerInput.MENU_SELECT) {
               if (FormColorPicker.this.isControllerFocus(this) && var1.buttonState) {
                  FormColorPicker.this.isControllerSelected = true;
                  FormColorPicker.this.isControllerControllingHue = true;
                  var1.use();
               }
            } else if ((var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU) && FormColorPicker.this.isControllerSelected && var1.buttonState) {
               FormColorPicker.this.isControllerSelected = false;
               var1.use();
            }

         }

         public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
            if (FormColorPicker.this.isControllerSelected && FormColorPicker.this.isControllerControllingHue) {
               switch (var1) {
                  case 1:
                     if (FormColorPicker.this.currentHue < 1.0F) {
                        FormColorPicker.this.currentHue = Math.min(FormColorPicker.this.currentHue + 0.02F, 1.0F);
                        FormColorPicker.this.queryNewColor();
                     }

                     var2.use();
                     break;
                  case 3:
                     if (FormColorPicker.this.currentHue > 0.0F) {
                        FormColorPicker.this.currentHue = Math.max(FormColorPicker.this.currentHue - 0.02F, 0.0F);
                        FormColorPicker.this.queryNewColor();
                     }

                     var2.use();
               }

               return true;
            } else {
               return false;
            }
         }

         public void drawControllerFocus(ControllerFocus var1) {
            Rectangle var2 = var1.boundingBox;
            byte var3 = 5;
            var2 = new Rectangle(var2.x - var3, var2.y - var3, var2.width + var3 * 2, var2.height + var3 * 2);
            Color var4 = FormColorPicker.this.isControllerSelected && FormColorPicker.this.isControllerControllingHue ? Settings.UI.controllerFocusBoundsHighlightColor : Settings.UI.controllerFocusBoundsColor;
            HUD.selectBoundOptions(var4, true, var2).draw();
            if (!FormColorPicker.this.isControllerSelected || !FormColorPicker.this.isControllerControllingHue) {
               Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
            }

         }
      };
      if (var4 < 30) {
         throw new IllegalArgumentException("Height must be greater than 30");
      } else {
         this.position = new FormFixedPosition(var1, var2);
         this.width = var3;
         this.height = var4;
      }
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      int var4;
      int var5;
      if (var1.isMouseMoveEvent()) {
         var1 = InputEvent.MouseMoveEvent(Screen.mousePos(), var2);
         if (this.mouseDownHueBar) {
            var4 = GameMath.limit(this.mouseDownPoint.x + var1.pos.hudX - this.mouseDownEvent.pos.hudX, 0, this.width);
            this.currentHue = getHueAt(var4, this.width);
            this.queryNewColor();
         } else if (this.mouseDownSpectrum) {
            var4 = GameMath.limit(this.mouseDownPoint.x + var1.pos.hudX - this.mouseDownEvent.pos.hudX, 0, this.width);
            var5 = GameMath.limit(this.mouseDownPoint.y + var1.pos.hudY - this.mouseDownEvent.pos.hudY, 0, this.getSpectrumHeight());
            this.currentPoint = new Point(var4, var5);
            this.queryNewColor();
         }
      }

      if (var1.getID() == -100) {
         if (!var1.state) {
            if (this.mouseDownHueBar) {
               var1.use();
               this.mouseDownHueBar = false;
               if (var1.pos.hudX != Integer.MIN_VALUE && var1.pos.hudY != Integer.MIN_VALUE) {
                  var4 = GameMath.limit(var1.pos.hudX - this.getX(), 0, this.width);
                  this.currentHue = getHueAt(var4, this.width);
                  this.queryNewColor();
               }
            } else if (this.mouseDownSpectrum) {
               var1.use();
               this.mouseDownSpectrum = false;
               if (var1.pos.hudX != Integer.MIN_VALUE && var1.pos.hudY != Integer.MIN_VALUE) {
                  var4 = GameMath.limit(var1.pos.hudX - this.getX(), 0, this.width);
                  var5 = GameMath.limit(var1.pos.hudY - this.getY(), 0, this.getSpectrumHeight());
                  this.currentPoint = new Point(var4, var5);
                  this.queryNewColor();
               }
            }
         } else if (this.isMouseOverHueBar(var1)) {
            var1.use();
            this.mouseDownHueBar = true;
            this.mouseDownEvent = InputEvent.MouseMoveEvent(Screen.mousePos(), var2);
            var4 = GameMath.limit(var1.pos.hudX - this.getX(), 0, this.width);
            this.mouseDownPoint = new Point(var4, 0);
            this.currentHue = getHueAt(var4, this.width);
            this.queryNewColor();
         } else if (this.isMouseOverSpectrum(var1)) {
            var1.use();
            this.mouseDownSpectrum = true;
            this.mouseDownEvent = InputEvent.MouseMoveEvent(Screen.mousePos(), var2);
            var4 = GameMath.limit(var1.pos.hudX - this.getX(), 0, this.width);
            var5 = GameMath.limit(var1.pos.hudY - this.getY(), 0, this.getSpectrumHeight());
            this.mouseDownPoint = new Point(var4, var5);
            this.currentPoint = new Point(var4, var5);
            this.queryNewColor();
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      this.colorSelectorHandler.handleControllerEvent(var1, var2, var3);
      this.hueSelectorHandler.handleControllerEvent(var1, var2, var3);
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      ControllerFocus.add(var1, var5, this.colorSelectorHandler, new Rectangle(this.getX(), this.getY(), this.width, this.getSpectrumHeight()), var2, var3, this.controllerInitialFocusPriority, var4);
      ControllerFocus.add(var1, var5, this.hueSelectorHandler, new Rectangle(this.getX(), this.getY() + this.getSpectrumHeight(), this.width, 30), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   protected void queryNewColor() {
      if (this.currentPoint != null) {
         Color var1 = this.currentSelected;
         this.currentSelected = getColorAt(this.currentPoint.x, this.currentPoint.y, this.width, this.getSpectrumHeight(), this.currentHue);
         if (!this.currentSelected.equals(var1)) {
            FormEvent var2 = new FormEvent(this);
            this.changedEvents.onEvent(var2);
         }
      }

   }

   public FormColorPicker onChanged(FormEventListener<FormEvent<FormColorPicker>> var1) {
      this.changedEvents.addListener(var1);
      return this;
   }

   public Color getSelectedColor() {
      return this.currentSelected;
   }

   public void setSelectedColor(Color var1) {
      if (var1 == null) {
         this.currentHue = 0.0F;
         this.currentPoint = null;
         this.currentSelected = null;
      } else {
         float[] var2 = Color.RGBtoHSB(var1.getRed(), var1.getGreen(), var1.getBlue(), (float[])null);
         this.currentHue = var2[0];
         float var3 = var2[1];
         float var4 = var2[2];
         float var5 = Math.abs(var4 - 1.0F);
         int var6 = (int)(var3 * (float)this.width);
         int var7 = (int)(var5 * (float)this.getSpectrumHeight());
         this.currentPoint = new Point(var6, var7);
         this.currentSelected = var1;
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      drawSatValSpectrum(this.getX(), this.getY(), this.width, this.getSpectrumHeight(), this.currentHue);
      int var4 = this.getHueBarY();
      drawHueBar(this.getX(), var4, this.width, this.getHueBarHeight());
      int var5 = this.getX() + (int)(this.currentHue * (float)this.width);
      int var6 = this.getHueBarHeight() / 2 + 5;
      int var7 = GameMath.limit(var5, this.getX() + var6 - 2, this.getX() + this.width - var6 + 2);
      Screen.drawCircle(var7, var4 + this.getHueBarHeight() / 2, var6, 30, this.backgroundColor, true);
      Screen.drawCircle(var7, var4 + this.getHueBarHeight() / 2, var6 - 2, 30, Color.getHSBColor(this.currentHue, 1.0F, 1.0F), true);
      if (this.currentPoint != null) {
         Screen.drawCircle(this.getX() + this.currentPoint.x, this.getY() + this.currentPoint.y, 10, 30, this.backgroundColor, true);
         Screen.drawCircle(this.getX() + this.currentPoint.x, this.getY() + this.currentPoint.y, 8, 30, this.currentSelected, true);
      }

   }

   public static Color getColorAt(int var0, int var1, int var2, int var3, float var4) {
      var0 = GameMath.limit(var0, 0, var2);
      var1 = GameMath.limit(var1, 0, var3);
      float var5 = (float)var0 / (float)var2;
      float var6 = Math.abs((float)var1 / (float)var3 - 1.0F);
      return Color.getHSBColor(var4, var5, var6);
   }

   public static void drawSatValSpectrum(int var0, int var1, int var2, int var3, float var4) {
      var4 = GameMath.limit(var4, 0.0F, 1.0F);
      GameResources.empty.bind();
      GL11.glLoadIdentity();
      GL11.glBegin(1);

      for(int var5 = 0; var5 <= var2; ++var5) {
         float var6 = (float)var5 / (float)var2;
         Color var7 = Color.getHSBColor(var4, var6, 1.0F);
         GL11.glColor4f((float)var7.getRed() / 255.0F, (float)var7.getGreen() / 255.0F, (float)var7.getBlue() / 255.0F, 1.0F);
         GL11.glVertex2f((float)(var0 + var5), (float)var1);
         Color var8 = Color.getHSBColor(var4, var6, 0.0F);
         GL11.glColor4f((float)var8.getRed() / 255.0F, (float)var8.getGreen() / 255.0F, (float)var8.getBlue() / 255.0F, 1.0F);
         GL11.glVertex2f((float)(var0 + var5), (float)(var1 + var3));
      }

      GL11.glEnd();
   }

   public static float getHueAt(int var0, int var1) {
      var0 = GameMath.limit(var0, 0, var1);
      return (float)var0 / (float)var1;
   }

   public static void drawHueBar(int var0, int var1, int var2, int var3) {
      GameResources.empty.bind();
      GL11.glLoadIdentity();
      GL11.glBegin(1);

      for(int var4 = 0; var4 <= var2; ++var4) {
         float var5 = (float)var4 / (float)var2;
         Color var6 = Color.getHSBColor(var5, 1.0F, 1.0F);
         GL11.glColor4f((float)var6.getRed() / 255.0F, (float)var6.getGreen() / 255.0F, (float)var6.getBlue() / 255.0F, 1.0F);
         GL11.glVertex2f((float)(var0 + var4), (float)var1);
         GL11.glVertex2f((float)(var0 + var4), (float)(var1 + var3));
      }

      GL11.glEnd();
   }

   protected int getSpectrumHeight() {
      return this.height - 30;
   }

   protected int getHueBarHeight() {
      return 10;
   }

   protected int getHueBarY() {
      return this.getY() + this.height - this.getHueBarHeight() - 10;
   }

   protected boolean isMouseOverSpectrum(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX(), this.getY(), this.width, this.getSpectrumHeight())).contains(var1.pos.hudX, var1.pos.hudY);
   }

   protected boolean isMouseOverHueBar(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX(), this.getHueBarY(), this.width, this.getHueBarHeight())).contains(var1.pos.hudX, var1.pos.hudY);
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   public static float[] RGBtoHSL(float var0, float var1, float var2) {
      float var3 = Math.min(var0, Math.min(var1, var2));
      float var4 = Math.max(var0, Math.max(var1, var2));
      float var5 = 0.0F;
      if (var4 == var3) {
         var5 = 0.0F;
      } else if (var4 == var0) {
         var5 = (60.0F * (var1 - var2) / (var4 - var3) + 360.0F) % 360.0F;
      } else if (var4 == var1) {
         var5 = 60.0F * (var2 - var0) / (var4 - var3) + 120.0F;
      } else if (var4 == var2) {
         var5 = 60.0F * (var0 - var1) / (var4 - var3) + 240.0F;
      }

      float var6 = (var4 + var3) / 2.0F;
      float var7;
      if (var4 == var3) {
         var7 = 0.0F;
      } else if (var6 <= 0.5F) {
         var7 = (var4 - var3) / (var4 + var3);
      } else {
         var7 = (var4 - var3) / (2.0F - var4 - var3);
      }

      return new float[]{var5 / 360.0F, var7, var6};
   }

   public static float[] HSLtoRGB(float var0, float var1, float var2) {
      float var3;
      if ((double)var2 < 0.5) {
         var3 = var2 * (1.0F + var1);
      } else {
         var3 = var2 + var1 - var1 * var2;
      }

      float var4 = 2.0F * var2 - var3;
      float var5 = Math.max(0.0F, HueToRGB(var4, var3, var0 + 0.33333334F));
      float var6 = Math.max(0.0F, HueToRGB(var4, var3, var0));
      float var7 = Math.max(0.0F, HueToRGB(var4, var3, var0 - 0.33333334F));
      var5 = Math.min(var5, 1.0F);
      var6 = Math.min(var6, 1.0F);
      var7 = Math.min(var7, 1.0F);
      return new float[]{var5, var6, var7};
   }

   public static float[] toHSL(Color var0) {
      return RGBtoHSL((float)var0.getRed() / 255.0F, (float)var0.getGreen() / 255.0F, (float)var0.getBlue() / 255.0F);
   }

   public static Color fromHSL(float var0, float var1, float var2) {
      return fromHSL(var0, var1, var2, 1.0F);
   }

   public static Color fromHSL(float var0, float var1, float var2, float var3) {
      float[] var4 = HSLtoRGB(var0, var1, var2);
      return new Color(var4[0], var4[1], var4[2], var3);
   }

   private static float HueToRGB(float var0, float var1, float var2) {
      if (var2 < 0.0F) {
         ++var2;
      }

      if (var2 > 1.0F) {
         --var2;
      }

      if (6.0F * var2 < 1.0F) {
         return var0 + (var1 - var0) * 6.0F * var2;
      } else if (2.0F * var2 < 1.0F) {
         return var1;
      } else {
         return 3.0F * var2 < 2.0F ? var0 + (var1 - var0) * 6.0F * (0.6666667F - var2) : var0;
      }
   }
}
