package necesse.gfx.shader;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Objects;
import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.control.InputPosition;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.shader.shaderVariable.ShaderBooleanVariable;

public class FormShader extends GameShader {
   private FormShaderState currentState = null;

   public FormShader() {
      super("vertForm", "fragForm");
      this.addVariable(new ShaderBooleanVariable("drawOutside"));
   }

   public FormShaderState startState(Point var1, Rectangle var2) {
      this.currentState = new FormShaderState(this.currentState, var1, var2);
      this.currentState.apply();
      return this.currentState;
   }

   public void use() {
      super.use();
      this.currentState = new FormShaderState((FormShaderState)null, new Point(0, 0), new Rectangle(0, 0, Screen.getHudWidth(), Screen.getHudHeight()));
      this.currentState.apply();
   }

   public class FormShaderState {
      private final FormShaderState prevState;
      public final Point offset;
      public final Rectangle drawLimit;
      public final InputEvent mouseEvent;

      private FormShaderState(FormShaderState var2, Point var3, Rectangle var4) {
         this.prevState = var2;
         if (var2 != null) {
            if (var3 == null) {
               var3 = var2.offset;
            } else {
               var3.x += var2.offset.x;
               var3.y += var2.offset.y;
            }
         }

         Objects.requireNonNull(var3);
         var3 = new Point(var3);
         this.offset = var3;
         Objects.requireNonNull(var4);
         var4 = new Rectangle(var4);
         var4.x += var3.x;
         var4.y += var3.y;
         int var5;
         int var6;
         if (var2 != null) {
            var5 = var4.x;
            var6 = var4.y;
            var4.x = Math.min(Math.max(var2.drawLimit.x, var4.x), var2.drawLimit.x + var2.drawLimit.width);
            var4.y = Math.min(Math.max(var2.drawLimit.y, var4.y), var2.drawLimit.y + var2.drawLimit.height);
            var4.width = Math.min(var2.drawLimit.x + var2.drawLimit.width - var4.x, var4.width - (var4.x - var5));
            var4.height = Math.min(var2.drawLimit.y + var2.drawLimit.height - var4.y, var4.height - (var4.y - var6));
         }

         this.drawLimit = var4;
         var5 = Integer.MIN_VALUE;
         int var10001;
         if (Screen.mousePos().hudX >= var4.x) {
            var10001 = var4.x + var4.width;
            if (Screen.mousePos().hudX < var10001) {
               var5 = Screen.mousePos().hudX - var3.x;
            }
         }

         var6 = Integer.MIN_VALUE;
         if (Screen.mousePos().hudY >= var4.y) {
            var10001 = var4.y + var4.height;
            if (Screen.mousePos().hudY < var10001) {
               var6 = Screen.mousePos().hudY - var3.y;
            }
         }

         this.mouseEvent = InputEvent.MouseMoveEvent(InputPosition.fromHudPos(Screen.input(), var5, var6), Screen.tickManager);
      }

      private void apply() {
         GameFrameBuffer var1 = Screen.getCurrentBuffer();
         FormShader.this.pass2f("pixelOffset", (float)this.offset.x / (float)var1.getWidth() * 2.0F, (float)this.offset.y / (float)var1.getHeight() * 2.0F);
         int var2 = Math.abs(this.drawLimit.y - var1.getHeight() + this.drawLimit.height);
         FormShader.this.pass4f("drawLimit", (float)this.drawLimit.x, (float)var2, (float)this.drawLimit.width, (float)this.drawLimit.height);
      }

      public void end() {
         if (this.prevState == null) {
            throw new IllegalStateException("Cannot end Form shader state: Not started yet");
         } else {
            FormShader.this.currentState = this.prevState;
            FormShader.this.currentState.apply();
         }
      }

      public void drawDebugRects() {
         Screen.initQuadDraw(this.drawLimit.x + this.drawLimit.width - this.offset.x, this.drawLimit.y + this.drawLimit.height - this.offset.y).color(new Color(0, 0, 255, 100)).draw(0, 0);
      }

      // $FF: synthetic method
      FormShaderState(FormShaderState var2, Point var3, Rectangle var4, Object var5) {
         this(var2, var3, var4);
      }
   }
}
