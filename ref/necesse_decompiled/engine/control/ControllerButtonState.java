package necesse.engine.control;

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerActionSetHandle;
import com.codedisaster.steamworks.SteamControllerDigitalActionData;
import com.codedisaster.steamworks.SteamControllerDigitalActionHandle;
import com.codedisaster.steamworks.SteamControllerHandle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.tickManager.TickManager;

public class ControllerButtonState extends ControllerState {
   public final ControllerActionSetHandle actionSet;
   public final String actionName;
   protected Supplier<Control> controlSupplier;
   protected Control control;
   protected SteamControllerDigitalActionData[] steamData;
   protected SteamControllerDigitalActionHandle steamHandle;
   protected boolean[] lastState;
   protected boolean pressed;
   protected boolean released;
   protected boolean down;

   public ControllerButtonState(ControllerActionSetHandle var1, String var2, Supplier<Control> var3) {
      this.actionSet = var1;
      this.actionName = var2;
      this.controlSupplier = var3;
      this.steamData = new SteamControllerDigitalActionData[16];

      for(int var4 = 0; var4 < this.steamData.length; ++var4) {
         this.steamData[var4] = new SteamControllerDigitalActionData();
      }

      this.lastState = new boolean[16];
   }

   public void init(SteamController var1) {
      this.steamHandle = var1.getDigitalActionHandle(this.actionName);
      if (this.controlSupplier != null) {
         this.control = (Control)this.controlSupplier.get();
         if (this.control != null) {
            this.control.controllerState = this;
         }
      }

   }

   protected SteamControllerHandle updateSteamData(SteamController var1, SteamControllerHandle[] var2, int var3, List<ControllerEvent> var4, ControllerOriginsUsedHandler var5, boolean var6, TickManager var7) {
      this.pressed = false;
      this.released = false;
      this.down = false;
      SteamControllerHandle var8 = null;

      for(int var9 = 0; var9 < var3; ++var9) {
         if (var2[var9] != null) {
            SteamControllerDigitalActionData var10 = this.steamData[var9];
            var1.getDigitalActionData(var2[var9], this.steamHandle, var10);
            this.down = this.down || var10.getState();
            if (var10.getActive() && this.lastState[var9] != var10.getState()) {
               var8 = var2[var9];
               ControllerEvent var11;
               if (!this.lastState[var9] && var10.getState()) {
                  this.lastState[var9] = true;
                  if (!var5.getIsUsedAndUpdate(this, var1, var2[var9], this.actionSet.getHandle(), this.steamHandle)) {
                     this.pressed = true;
                     var11 = ControllerEvent.buttonEvent(var2[var9], this, true);
                     var4.add(var11);
                     if (this.control != null) {
                        this.control.activate(InputEvent.ControllerButtonEvent(var11, var7));
                     }
                  }
               }

               if (this.lastState[var9] && !var10.getState()) {
                  this.released = true;
                  this.lastState[var9] = false;
                  ControllerInput.repeatEvents.remove(this.getID());
                  var11 = ControllerEvent.buttonEvent(var2[var9], this, false);
                  var4.add(var11);
                  if (this.control != null) {
                     this.control.activate(InputEvent.ControllerButtonEvent(var11, var7));
                  }
               }
            }
         }
      }

      return var8;
   }

   public SteamController.ActionOrigin[] getSteamOriginsOut(SteamController var1, SteamControllerHandle var2) {
      if (this.actionSet == null) {
         return null;
      } else {
         SteamControllerActionSetHandle var3 = this.actionSet.getHandle();
         if (var3 == null) {
            return null;
         } else {
            SteamController.ActionOrigin[] var4 = new SteamController.ActionOrigin[8];

            try {
               var1.getDigitalActionOrigins(var2, var3, this.steamHandle, var4);
               return var4;
            } catch (Exception var6) {
               return null;
            }
         }
      }
   }

   public boolean isPressed() {
      return this.pressed;
   }

   public boolean isDown() {
      return this.down;
   }

   public boolean isReleased() {
      return this.released;
   }
}
