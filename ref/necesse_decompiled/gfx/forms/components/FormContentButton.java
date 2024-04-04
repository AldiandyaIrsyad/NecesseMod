package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;

public abstract class FormContentButton extends FormButton implements FormPositionContainer {
   private FormPosition position;
   private int width;
   public FormInputSize size;
   public ButtonColor color;

   public FormContentButton(int var1, int var2, int var3, FormInputSize var4, ButtonColor var5) {
      this.position = new FormFixedPosition(var1, var2);
      this.width = var3;
      this.size = var4;
      this.color = var5;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Color var4 = this.getDrawColor();
      ButtonState var5 = this.getButtonState();
      int var6 = 0;
      boolean var7 = this.isButtonDown();
      if (var7) {
         this.size.getButtonDownDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
         var6 = this.size.buttonDownContentDrawOffset;
      } else {
         this.size.getButtonDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
      }

      Rectangle var8 = this.size.getContentRectangle(this.width);
      FormShader.FormShaderState var9 = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(var8.x, var8.y, var8.width, var8.height));

      try {
         this.drawContent(var8.x, var8.y + var6, var8.width, var8.height);
      } finally {
         var9.end();
      }

      if (var7) {
         this.size.getButtonDownEdgeDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
      } else {
         this.size.getButtonEdgeDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
      }

      if (this.isHovering()) {
         this.addTooltips(var2);
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY() + this.size.textureDrawOffset, this.width, this.size.height));
   }

   protected abstract void drawContent(int var1, int var2, int var3, int var4);

   protected void addTooltips(PlayerMob var1) {
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int var1) {
      this.width = var1;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   public boolean isButtonDown() {
      return this.isDown() && this.isHovering();
   }
}
