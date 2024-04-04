package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.PlayerSprite;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameTexture;

public class FormPlayerIcon extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   private int width;
   private int height;
   private PlayerMob player;
   private int spriteX;
   private int spriteY;

   public FormPlayerIcon(int var1, int var2, int var3, int var4, PlayerMob var5) {
      this.position = new FormFixedPosition(var1, var2);
      this.width = var3;
      this.height = var4;
      this.player = var5;
      this.spriteX = 0;
      this.spriteY = 2;
   }

   public void setPlayer(PlayerMob var1) {
      this.player = var1;
   }

   public void setRotation(int var1) {
      var1 = Math.abs(var1 % 4);
      this.spriteY = var1;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      GameTexture.overrideBlendQuality = GameTexture.BlendQuality.NEAREST;
      PlayerSprite.getIconDrawOptions(this.getX(), this.getY(), this.width, this.height, this.player, this.spriteX, this.spriteY).draw();
      GameTexture.overrideBlendQuality = null;
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
   }

   public boolean isMouseOver(InputEvent var1) {
      return false;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }
}
