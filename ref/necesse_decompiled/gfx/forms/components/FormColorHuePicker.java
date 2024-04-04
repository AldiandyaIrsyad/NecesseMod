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
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.ui.HUD;
import org.lwjgl.opengl.GL11;

public class FormColorHuePicker extends FormComponent implements FormPositionContainer {
   public Color backgroundColor;
   public int circleExtraSize;
   private FormPosition position;
   private int width;
   private int height;
   private Point mouseDownPoint;
   private InputEvent mouseDownEvent;
   private boolean isControllerSelected;
   private float lastHue;
   private float currentHue;
   protected FormEventsHandler<FormEvent<FormColorHuePicker>> changedEvents;

   public FormColorHuePicker(int var1, int var2, int var3, int var4, float var5) {
      this.backgroundColor = GameBackground.form.getCenterColor();
      this.circleExtraSize = 10;
      this.changedEvents = new FormEventsHandler();
      this.position = new FormFixedPosition(var1, var2);
      this.width = var3;
      this.height = var4;
      this.lastHue = var5;
      this.currentHue = var5;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      int var4;
      if (var1.isMouseMoveEvent()) {
         var1 = InputEvent.MouseMoveEvent(Screen.mousePos(), var2);
         if (this.mouseDownPoint != null) {
            var4 = GameMath.limit(this.mouseDownPoint.x + var1.pos.hudX - this.mouseDownEvent.pos.hudX, 0, this.width);
            this.currentHue = getHueAt(var4, this.width);
            this.queryNewColor();
         }
      }

      if (var1.getID() == -100) {
         if (!var1.state) {
            if (this.mouseDownPoint != null) {
               var1.use();
               this.mouseDownPoint = null;
               if (var1.pos.hudX != Integer.MIN_VALUE && var1.pos.hudY != Integer.MIN_VALUE) {
                  var4 = GameMath.limit(var1.pos.hudX - this.getX(), 0, this.width);
                  this.currentHue = getHueAt(var4, this.width);
                  this.queryNewColor();
               }
            }
         } else if (this.isMouseOver(var1)) {
            var1.use();
            this.mouseDownEvent = InputEvent.MouseMoveEvent(Screen.mousePos(), var2);
            var4 = GameMath.limit(var1.pos.hudX - this.getX(), 0, this.width);
            this.mouseDownPoint = new Point(var4, 0);
            this.currentHue = getHueAt(var4, this.width);
            this.queryNewColor();
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.getState() == ControllerInput.MENU_SELECT) {
         if (this.isControllerFocus() && var1.buttonState) {
            this.isControllerSelected = true;
            var1.use();
         }
      } else if ((var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU) && this.isControllerSelected && var1.buttonState) {
         this.isControllerSelected = false;
         var1.use();
      }

   }

   public void onControllerUnfocused(ControllerFocus var1) {
      super.onControllerUnfocused(var1);
      this.isControllerSelected = false;
   }

   public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
      if (this.isControllerSelected) {
         switch (var1) {
            case 1:
               if (this.currentHue < 1.0F) {
                  this.currentHue = Math.min(this.currentHue + 0.02F, 1.0F);
                  this.queryNewColor();
               }

               var2.use();
               break;
            case 3:
               if (this.currentHue > 0.0F) {
                  this.currentHue = Math.max(this.currentHue - 0.02F, 0.0F);
                  this.queryNewColor();
               }

               var2.use();
         }

         return true;
      } else {
         return super.handleControllerNavigate(var1, var2, var3, var4);
      }
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   protected void queryNewColor() {
      if (this.lastHue != this.currentHue) {
         this.lastHue = this.currentHue;
         FormEvent var1 = new FormEvent(this);
         this.changedEvents.onEvent(var1);
      }

   }

   public FormColorHuePicker onChanged(FormEventListener<FormEvent<FormColorHuePicker>> var1) {
      this.changedEvents.addListener(var1);
      return this;
   }

   public float getSelectedHue() {
      return this.currentHue;
   }

   public void setSelectedHue(float var1) {
      this.currentHue = var1;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      drawHueBar(this.getX(), this.getY(), this.width, this.height);
      int var4 = this.getX() + (int)(this.currentHue * (float)this.width);
      int var5 = this.height / 2 + this.circleExtraSize / 2;
      int var6 = GameMath.limit(var4, this.getX() + var5 - 2, this.getX() + this.width - var5 + 2);
      Screen.drawCircle(var6, this.getY() + this.height / 2, var5, 30, this.backgroundColor, true);
      Screen.drawCircle(var6, this.getY() + this.height / 2, var5 - 2, 30, Color.getHSBColor(this.currentHue, 1.0F, 1.0F), true);
   }

   public void drawControllerFocus(ControllerFocus var1) {
      if (this.isControllerSelected) {
         Rectangle var2 = var1.boundingBox;
         byte var3 = 5;
         var2 = new Rectangle(var2.x - var3, var2.y - var3, var2.width + var3 * 2, var2.height + var3 * 2);
         HUD.selectBoundOptions(Settings.UI.controllerFocusBoundsHighlightColor, true, var2).draw();
      } else {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

   }

   public static Color getColorAt(int var0, int var1, int var2, int var3, float var4) {
      var0 = GameMath.limit(var0, 0, var2);
      var1 = GameMath.limit(var1, 0, var3);
      float var5 = (float)var0 / (float)var2;
      float var6 = Math.abs((float)var1 / (float)var3 - 1.0F);
      return Color.getHSBColor(var4, var5, var6);
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
