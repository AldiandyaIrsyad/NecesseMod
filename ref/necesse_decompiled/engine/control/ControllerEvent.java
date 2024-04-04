package necesse.engine.control;

import com.codedisaster.steamworks.SteamControllerHandle;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;

public class ControllerEvent {
   public final SteamControllerHandle controller;
   private ControllerState state;
   private ControllerState usedState;
   private ControllerState repeatState;
   public final boolean isButton;
   public final boolean isAnalog;
   public final boolean buttonState;
   public final float analogX;
   public final float analogY;
   private Object[] repeatCallers;
   private int repeatCounter;
   private int repeatDelay;
   protected LinkedList<InputEvent> inputEvents = new LinkedList();

   private ControllerEvent(SteamControllerHandle var1, ControllerState var2, boolean var3, boolean var4, boolean var5, float var6, float var7) {
      this.controller = var1;
      this.state = var2;
      this.isButton = var3;
      this.isAnalog = var4;
      this.buttonState = var5;
      this.analogX = var6;
      this.analogY = var7;
   }

   public static ControllerEvent customEvent(SteamControllerHandle var0, ControllerState var1) {
      return new ControllerEvent(var0, var1, false, false, false, 0.0F, 0.0F);
   }

   public static ControllerEvent buttonEvent(SteamControllerHandle var0, ControllerState var1, boolean var2) {
      return new ControllerEvent(var0, var1, true, false, var2, 0.0F, 0.0F);
   }

   public static ControllerEvent repeatEvent(SteamControllerHandle var0, Object[] var1, int var2, int var3, ControllerState var4) {
      ControllerEvent var5 = new ControllerEvent(var0, ControllerInput.REPEAT_EVENT, true, false, true, 0.0F, 0.0F);
      var5.repeatCallers = var1;
      var5.repeatCounter = var2;
      var5.repeatDelay = var3;
      var5.repeatState = var4;
      return var5;
   }

   public static ControllerEvent analogEvent(SteamControllerHandle var0, ControllerState var1, float var2, float var3) {
      return new ControllerEvent(var0, var1, false, true, false, var2, var3);
   }

   public boolean isRepeatEvent(Object var1) {
      return this.isRepeatEvent((var1x) -> {
         return Objects.equals(var1x, var1);
      });
   }

   public boolean isRepeatEvent(int var1, Object var2) {
      return this.isRepeatEvent(var1, (var1x) -> {
         return Objects.equals(var1x, var2);
      });
   }

   public boolean isRepeatEvent(Function<Object, Boolean> var1) {
      return this.isRepeatEvent(0, (Function)var1);
   }

   public boolean isRepeatEvent(int var1, Function<Object, Boolean> var2) {
      if (this.state == ControllerInput.REPEAT_EVENT) {
         return this.repeatCallers != null && var1 < this.repeatCallers.length ? (Boolean)var2.apply(this.repeatCallers[var1]) : (Boolean)var2.apply((Object)null);
      } else {
         return false;
      }
   }

   public ControllerState getState() {
      return this.state;
   }

   public ControllerState getUsedState() {
      return this.usedState;
   }

   public ControllerState getRepeatState() {
      return this.repeatState;
   }

   public void startRepeatEvents(Object... var1) {
      ControllerInput.startRepeatEvents(this, var1);
   }

   public boolean shouldSubmitSound() {
      if (this.state != ControllerInput.REPEAT_EVENT) {
         return true;
      } else {
         byte var1 = 11;
         if (this.repeatDelay >= var1) {
            return true;
         } else {
            return this.repeatCounter % (var1 / this.repeatDelay) == 0;
         }
      }
   }

   public void use() {
      if (!this.isUsed()) {
         this.inputEvents.forEach(InputEvent::use);
         this.inputEvents.clear();
         this.usedState = this.state;
         this.state = null;
      }

   }

   public boolean isUsed() {
      return this.state == null;
   }
}
