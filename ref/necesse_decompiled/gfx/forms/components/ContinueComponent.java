package necesse.gfx.forms.components;

public interface ContinueComponent {
   void onContinue(Runnable var1);

   void applyContinue();

   default boolean canContinue() {
      return true;
   }

   boolean isContinued();
}
