package necesse.entity.mobs.friendly.human.humanWorkSetting;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.mob.ShopContainer;

public abstract class HumanWorkSetting<T> {
   private HumanWorkSettingRegistry registry;
   private int id;
   public int dialogueSort = 0;

   public HumanWorkSetting() {
   }

   public void onRegister(HumanWorkSettingRegistry var1, int var2) {
      if (this.registry != null) {
         throw new IllegalStateException("HumanWorkSetting already registered");
      } else {
         this.registry = var1;
         this.id = var2;
      }
   }

   public int getID() {
      return this.id;
   }

   public abstract void writeContainerPacket(ServerClient var1, PacketWriter var2);

   public abstract T readContainer(ShopContainer var1, PacketReader var2);

   public abstract HumanWorkContainerHandler<T> setupHandler(ShopContainer var1, ShopContainer.ContainerWorkSetting<T> var2);

   public void runAction(Packet var1) {
   }
}
