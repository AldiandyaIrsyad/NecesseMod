package necesse.gfx.forms.position;

public interface FormPositionContainer {
   FormPosition getPosition();

   void setPosition(FormPosition var1);

   default int getX() {
      return this.getPosition().getX();
   }

   default int getY() {
      return this.getPosition().getY();
   }

   default void setX(int var1) {
      this.setPosition(new FormFixedPosition(var1, this.getY()));
   }

   default void setY(int var1) {
      this.setPosition(new FormFixedPosition(this.getX(), var1));
   }

   default void setPosition(int var1, int var2) {
      this.setPosition(new FormFixedPosition(var1, var2));
   }

   default void addPosition(int var1, int var2) {
      this.setX(this.getX() + var1);
      this.setY(this.getY() + var2);
   }
}
