package necesse.engine.control;

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerActionSetHandle;
import com.codedisaster.steamworks.SteamControllerDigitalActionHandle;
import com.codedisaster.steamworks.SteamControllerHandle;
import java.util.HashMap;
import java.util.LinkedList;
import necesse.engine.util.ObjectValue;

public class ControllerOriginsUsedHandler {
   private HashMap<SteamController.ActionOrigin, Integer> usedOrigins = new HashMap();
   private LinkedList<ObjectValue<SteamController.ActionOrigin, Long>> clearTimes = new LinkedList();

   public ControllerOriginsUsedHandler() {
   }

   public boolean getIsUsedAndUpdate(ControllerState var1, SteamController var2, SteamControllerHandle var3, SteamControllerActionSetHandle var4, SteamControllerDigitalActionHandle var5) {
      SteamController.ActionOrigin[] var6 = new SteamController.ActionOrigin[8];

      try {
         int var7 = var2.getDigitalActionOrigins(var3, var4, var5, var6);

         for(int var8 = 0; var8 < var7; ++var8) {
            int var9 = (Integer)this.usedOrigins.getOrDefault(var6[var8], -1);
            if (var9 != -1 && var9 != var1.getID()) {
               return true;
            }

            this.clearTimes.add(new ObjectValue(var6[var8], System.currentTimeMillis() + 500L));
            this.usedOrigins.put(var6[var8], var1.getID());
         }

         return false;
      } catch (ArrayIndexOutOfBoundsException var10) {
         return false;
      }
   }

   public void clearUsed() {
      while(true) {
         if (!this.clearTimes.isEmpty()) {
            ObjectValue var1 = (ObjectValue)this.clearTimes.getFirst();
            if ((Long)var1.value <= System.currentTimeMillis()) {
               this.usedOrigins.remove(var1.object);
               this.clearTimes.removeFirst();
               continue;
            }
         }

         return;
      }
   }
}
