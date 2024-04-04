package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.entity.objectEntity.MusicPlayerObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.LongCustomAction;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.OEInventoryContainerSlot;

public class MusicPlayerContainer extends OEInventoryContainer {
   public final MusicPlayerObjectEntity objectEntity;
   public final BooleanCustomAction setPaused;
   public final LongCustomAction setOffset;
   public final LongCustomAction forwardMilliseconds;
   protected boolean isDirty = false;

   public MusicPlayerContainer(NetworkClient var1, int var2, OEInventory var3, PacketReader var4) {
      super(var1, var2, var3, var4);
      this.objectEntity = (MusicPlayerObjectEntity)var3;
      this.setPaused = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            MusicPlayerContainer.this.objectEntity.getMusicManager().setIsPaused(var1);
         }
      });
      this.setOffset = (LongCustomAction)this.registerAction(new LongCustomAction() {
         protected void run(long var1) {
            MusicPlayerContainer.this.objectEntity.getMusicManager().setOffset(var1);
         }
      });
      this.forwardMilliseconds = (LongCustomAction)this.registerAction(new LongCustomAction() {
         protected void run(long var1) {
            MusicPlayerContainer.this.objectEntity.getMusicManager().forwardMilliseconds(var1);
         }
      });
   }

   public ContainerSlot getOEContainerSlot(OEInventory var1, int var2) {
      return new OEInventoryContainerSlot(var1, var2) {
         public void markDirty() {
            super.markDirty();
            MusicPlayerContainer.this.isDirty = true;
         }
      };
   }

   public ContainerActionResult applyContainerAction(int var1, ContainerAction var2) {
      long var3 = this.objectEntity.getMusicManager().getMusicPlayingOffset();
      ContainerActionResult var5 = super.applyContainerAction(var1, var2);
      if (this.client.isClient() && this.isDirty) {
         long var6 = this.objectEntity.getMusicManager().getMusicPlayingOffset();
         long var8 = var6 - var3;
         if (Math.abs(var8) >= 500L) {
            this.setOffset.runAndSend(-var6);
         }
      }

      return var5;
   }
}
