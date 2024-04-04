package necesse.gfx.forms.events;

@FunctionalInterface
public interface FormEventListener<T extends FormEvent> {
   void onEvent(T var1);

   default boolean disposed() {
      return false;
   }
}
