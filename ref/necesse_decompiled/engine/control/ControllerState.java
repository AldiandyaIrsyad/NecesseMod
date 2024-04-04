package necesse.engine.control;

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerHandle;
import java.util.List;
import necesse.engine.tickManager.TickManager;

public abstract class ControllerState {
   private int id = -1;

   public ControllerState() {
   }

   public int getID() {
      return this.id;
   }

   protected final void init(int var1, SteamController var2) {
      if (this.id != -1) {
         throw new IllegalStateException("Controller state already initialized");
      } else {
         this.id = var1;
         this.init(var2);
      }
   }

   public abstract void init(SteamController var1);

   protected abstract SteamControllerHandle updateSteamData(SteamController var1, SteamControllerHandle[] var2, int var3, List<ControllerEvent> var4, ControllerOriginsUsedHandler var5, boolean var6, TickManager var7);

   public abstract SteamController.ActionOrigin[] getSteamOriginsOut(SteamController var1, SteamControllerHandle var2);
}
