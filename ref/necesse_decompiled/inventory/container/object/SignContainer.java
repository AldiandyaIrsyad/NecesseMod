package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.StringCustomAction;
import necesse.level.maps.Level;

public class SignContainer extends Container {
   public SignObjectEntity objectEntity;
   public StringCustomAction updateTextAction;

   public SignContainer(NetworkClient var1, int var2, final SignObjectEntity var3) {
      super(var1, var2);
      this.objectEntity = var3;
      this.updateTextAction = (StringCustomAction)this.registerAction(new StringCustomAction() {
         protected void run(String var1) {
            var3.setText(var1);
            var3.markDirty();
         }
      });
   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else {
         Level var2 = var1.getLevel();
         return !this.objectEntity.removed() && var2.getObject(this.objectEntity.getX(), this.objectEntity.getY()).inInteractRange(var2, this.objectEntity.getX(), this.objectEntity.getY(), var1.playerMob);
      }
   }
}
