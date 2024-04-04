package necesse.engine.control;

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerActionSetHandle;
import com.codedisaster.steamworks.SteamControllerHandle;
import java.util.List;
import necesse.engine.tickManager.TickManager;

public class ControllerActionSetState extends ControllerState implements ControllerActionSetHandle {
   private static ControllerActionSetState currentState;
   protected boolean setActive;
   public final String actionSetName;
   protected SteamControllerActionSetHandle steamHandle;

   public ControllerActionSetState(String var1) {
      this.actionSetName = var1;
      if (currentState == null) {
         currentState = this;
      }

   }

   public void init(SteamController var1) {
      this.steamHandle = var1.getActionSetHandle(this.actionSetName);
   }

   public void setActive() {
      this.setActive = true;
   }

   public boolean isActive() {
      return currentState == this;
   }

   protected SteamControllerHandle updateSteamData(SteamController var1, SteamControllerHandle[] var2, int var3, List<ControllerEvent> var4, ControllerOriginsUsedHandler var5, boolean var6, TickManager var7) {
      if (this.setActive || var6) {
         currentState = this;

         for(int var8 = 0; var8 < var3; ++var8) {
            if (var2[var8] != null) {
               var1.activateActionSet(var2[var8], this.steamHandle);
            }
         }
      }

      return null;
   }

   public SteamController.ActionOrigin[] getSteamOriginsOut(SteamController var1, SteamControllerHandle var2) {
      return null;
   }

   public SteamControllerActionSetHandle getHandle() {
      return this.steamHandle;
   }
}
