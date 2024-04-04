package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;

public class FormTextBox extends FormFairTypeEdit implements FormPositionContainer {
   protected FormPosition position;

   public FormTextBox(FontOptions var1, FairType.TextAlign var2, Color var3, int var4, int var5, int var6, int var7, int var8) {
      super(var1, var2, var3, var6, var7, var8);
      this.position = new FormFixedPosition(var4, var5);
   }

   public FormTextBox(FontOptions var1, FairType.TextAlign var2, Color var3, int var4, int var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, -1, -1);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      super.draw(var1, var2, var3);
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(this.getTextBoundingBox());
   }

   public void setEmptyTextSpace(Rectangle var1) {
      this.textSelectEmptySpace = var1;
   }

   protected int getTextX() {
      return this.getX();
   }

   protected int getTextY() {
      return this.getY();
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
