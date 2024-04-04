package necesse.engine.events;

public class PreventableGameEvent extends GameEvent {
   private boolean isPrevented;

   public PreventableGameEvent() {
   }

   public void preventDefault() {
      this.isPrevented = true;
   }

   public boolean isPrevented() {
      return this.isPrevented;
   }
}
