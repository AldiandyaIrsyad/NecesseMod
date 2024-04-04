package necesse.entity.manager;

import java.util.ArrayList;
import necesse.engine.network.server.ServerClient;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.GameObject;

public interface ObjectDestroyedListenerEntityComponent extends EntityComponent {
   void onObjectDestroyed(GameObject var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5);
}
