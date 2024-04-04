package necesse.engine.control;

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerHandle;
import java.util.List;
import necesse.engine.tickManager.TickManager;

public class ControllerEmptyState extends ControllerState {
   public ControllerEmptyState() {
   }

   public void init(SteamController var1) {
   }

   protected SteamControllerHandle updateSteamData(SteamController var1, SteamControllerHandle[] var2, int var3, List<ControllerEvent> var4, ControllerOriginsUsedHandler var5, boolean var6, TickManager var7) {
      return null;
   }

   public SteamController.ActionOrigin[] getSteamOriginsOut(SteamController var1, SteamControllerHandle var2) {
      return null;
   }
}
