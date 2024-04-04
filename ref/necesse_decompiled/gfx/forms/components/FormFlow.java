package necesse.gfx.forms.components;

import java.awt.Rectangle;
import necesse.gfx.forms.position.FormPositionContainer;

public class FormFlow {
   private int y;

   public FormFlow(int var1) {
      this.y = var1;
   }

   public FormFlow() {
      this(0);
   }

   public int next(int var1) {
      int var2 = this.y;
      this.y += var1;
      return var2;
   }

   public int next() {
      return this.next(0);
   }

   /** @deprecated */
   @Deprecated
   public <T extends FormPositionContainer> T next(T var1) {
      return this.nextY(var1, 0);
   }

   /** @deprecated */
   @Deprecated
   public <T extends FormPositionContainer> T next(T var1, int var2) {
      return this.nextY(var1, var2);
   }

   public <T extends FormPositionContainer> T nextY(T var1) {
      return this.nextY(var1, 0);
   }

   public <T extends FormPositionContainer> T nextY(T var1, int var2) {
      Rectangle var3 = ((FormComponent)var1).getBoundingBox();
      var1.setY(this.next(var3.height + var2));
      return var1;
   }

   public <T extends FormPositionContainer> T nextX(T var1) {
      return this.nextX(var1, 0);
   }

   public <T extends FormPositionContainer> T nextX(T var1, int var2) {
      Rectangle var3 = ((FormComponent)var1).getBoundingBox();
      var1.setX(this.next(var3.width + var2));
      return var1;
   }

   public int prev(int var1) {
      this.y -= var1;
      return this.y;
   }

   public <T extends FormPositionContainer> T prevY(T var1) {
      return this.prevY(var1, 0);
   }

   public <T extends FormPositionContainer> T prevY(T var1, int var2) {
      Rectangle var3 = ((FormComponent)var1).getBoundingBox();
      var1.setY(this.prev(var3.height));
      this.y -= var2;
      return var1;
   }

   public <T extends FormPositionContainer> T prevX(T var1) {
      return this.prevX(var1, 0);
   }

   public <T extends FormPositionContainer> T prevX(T var1, int var2) {
      Rectangle var3 = ((FormComponent)var1).getBoundingBox();
      var1.setX(this.prev(var3.width));
      this.y -= var2;
      return var1;
   }

   public FormFlow split(int var1) {
      this.next(var1);
      return new FormFlow(this.y);
   }

   public FormFlow split() {
      return this.split(0);
   }
}
