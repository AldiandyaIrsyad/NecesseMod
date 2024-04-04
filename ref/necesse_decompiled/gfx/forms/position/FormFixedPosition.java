package necesse.gfx.forms.position;

public class FormFixedPosition implements FormPosition {
   private int x;
   private int y;

   public FormFixedPosition(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public FormFixedPosition() {
      this(0, 0);
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }
}
