package necesse.engine.control;

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerActionSetHandle;
import com.codedisaster.steamworks.SteamControllerHandle;
import java.util.List;
import necesse.engine.tickManager.TickManager;

public class ControllerActionSetLayerState extends ControllerState implements ControllerActionSetHandle {
   public final String actionSetName;
   protected boolean isActive;
   protected boolean nextActive;
   protected SteamControllerActionSetHandle steamHandle;

   public ControllerActionSetLayerState(String var1) {
      this.actionSetName = var1;
   }

   public void init(SteamController var1) {
      this.steamHandle = var1.getActionSetHandle(this.actionSetName);
   }

   public void setActive(boolean var1) {
      this.nextActive = var1;
   }

   public boolean isActive() {
      return this.nextActive;
   }

   protected SteamControllerHandle updateSteamData(SteamController var1, SteamControllerHandle[] var2, int var3, List<ControllerEvent> var4, ControllerOriginsUsedHandler var5, boolean var6, TickManager var7) {
      if (var7.isFirstGameTickInSecond()) {
         var6 = true;
      }

      boolean var8 = this.nextActive != this.isActive;
      if (var8 || var6) {
         this.isActive = this.nextActive;

         for(int var9 = 0; var9 < var3; ++var9) {
            if (var2[var9] != null) {
               if (var6) {
                  var1.deactivateActionSetLayer(var2[var9], this.steamHandle);
               }

               if (this.isActive) {
                  var1.activateActionSetLayer(var2[var9], this.steamHandle);
               } else {
                  var1.deactivateActionSetLayer(var2[var9], this.steamHandle);
               }
            }
         }

         if (var8) {
            ControllerInput.submitNextRefreshFocusEvent();
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
