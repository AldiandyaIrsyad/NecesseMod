package necesse.level.gameObject;

import java.awt.Rectangle;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketObjectSwitched;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;

public class SwitchObject extends GameObject {
   public int counterID;

   public SwitchObject(Rectangle var1, int var2, boolean var3) {
      super(var1);
      this.isSwitch = true;
      this.isSwitched = var3;
      this.counterID = var2;
   }

   public GameMessage getNewLocalization() {
      return this.counterID != -1 && this.isSwitched ? ObjectRegistry.getObject(this.counterID).getNewLocalization() : super.getNewLocalization();
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (this.counterID != -1) {
         if (var1.isServer()) {
            this.onSwitched(var1, var2, var3);
         }

      }
   }

   public void onSwitched(Level var1, int var2, int var3) {
      var1.setObject(var2, var3, this.counterID);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsWithTile(new PacketObjectSwitched(var1, var2, var3, this.getID()), var1, var2, var3);
      }

      if (var1.isClient()) {
         this.playSwitchSound(var1, var2, var3);
      }

   }

   public void playSwitchSound(Level var1, int var2, int var3) {
   }
}
