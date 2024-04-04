package necesse.engine.control;

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerActionSetHandle;
import com.codedisaster.steamworks.SteamControllerAnalogActionData;
import com.codedisaster.steamworks.SteamControllerAnalogActionHandle;
import com.codedisaster.steamworks.SteamControllerHandle;
import java.util.List;
import necesse.engine.tickManager.TickManager;

public class ControllerAnalogState extends ControllerState {
   public final ControllerActionSetHandle actionSet;
   public final String actionName;
   protected SteamControllerAnalogActionData[] steamData = new SteamControllerAnalogActionData[16];
   protected SteamControllerAnalogActionHandle steamHandle;
   protected float x;
   protected float y;
   protected boolean changed;

   public ControllerAnalogState(ControllerActionSetHandle var1, String var2) {
      this.actionSet = var1;
      this.actionName = var2;

      for(int var3 = 0; var3 < this.steamData.length; ++var3) {
         this.steamData[var3] = new SteamControllerAnalogActionData();
      }

   }

   public void init(SteamController var1) {
      this.steamHandle = var1.getAnalogActionHandle(this.actionName);
   }

   protected SteamControllerHandle updateSteamData(SteamController var1, SteamControllerHandle[] var2, int var3, List<ControllerEvent> var4, ControllerOriginsUsedHandler var5, boolean var6, TickManager var7) {
      this.changed = false;
      SteamControllerHandle var8 = null;

      for(int var9 = 0; var9 < var3; ++var9) {
         if (var2[var9] != null) {
            SteamControllerAnalogActionData var10 = this.steamData[var9];
            float var11 = var10.getX();
            float var12 = var10.getY();
            var1.getAnalogActionData(var2[var9], this.steamHandle, var10);
            if (var10.getActive() && (var11 != var10.getX() || var12 != var10.getY())) {
               var8 = var2[var9];
               this.changed = true;
               this.x = var10.getX();
               this.y = -var10.getY();
               var4.add(ControllerEvent.analogEvent(var2[var9], this, this.x, this.y));
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
            var1.getAnalogActionOrigins(var2, var3, this.steamHandle, var4);
            return var4;
         }
      }
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public boolean hasChanged() {
      return this.changed;
   }
}
