package necesse.inventory.container.object.fallenAltar;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.worldData.incursions.OpenIncursion;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.slots.GatewayTabletContainerSlot;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.level.maps.incursion.IncursionData;

public class FallenAltarContainer extends Container {
   public FallenAltarObjectEntity altarEntity;
   public int TABLET_SLOT;
   public IntCustomAction openIncursion;
   public EmptyCustomAction enterIncursion;
   public BooleanCustomAction closeIncursion;

   public FallenAltarContainer(final NetworkClient var1, int var2, final FallenAltarObjectEntity var3, Packet var4) {
      super(var1, var2);
      this.altarEntity = var3;
      this.TABLET_SLOT = this.addSlot(new GatewayTabletContainerSlot(var3.inventory, 0));
      this.addInventoryQuickTransfer((var1x) -> {
         return !var3.hasOpenIncursion();
      }, this.TABLET_SLOT, this.TABLET_SLOT);
      new PacketReader(var4);
      this.openIncursion = (IntCustomAction)this.registerAction(new IntCustomAction() {
         protected void run(int var1x) {
            if (var1.isServer()) {
               if (FallenAltarContainer.this.getSlot(FallenAltarContainer.this.TABLET_SLOT).getItem() != null) {
                  IncursionData var2 = GatewayTabletItem.getIncursionData(FallenAltarContainer.this.getSlot(FallenAltarContainer.this.TABLET_SLOT).getItem());
                  if (var2 != null) {
                     FallenAltarContainer.this.open(var2);
                  }
               }

            }
         }
      });
      this.enterIncursion = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer()) {
               OpenIncursion var1x = var3.getOpenIncursion();
               if (var1x != null) {
                  var3.enterIncursion(var1.getServerClient());
               }

            }
         }
      });
      this.closeIncursion = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1x) {
            if (var1.isServer()) {
               OpenIncursion var2 = var3.getOpenIncursion();
               if (var2 != null) {
                  ServerClient var3x = var1.getServerClient();
                  if (var2.canComplete == var1x) {
                     ArrayList var4 = new ArrayList();
                     if (var2.canComplete) {
                        var3.completeOpenIncursion(FallenAltarContainer.this, var3x, var4);
                     } else {
                        var3.closeOpenIncursion(FallenAltarContainer.this, var3x, var4);
                     }

                     Iterator var5 = var4.iterator();

                     while(var5.hasNext()) {
                        InventoryItem var6 = (InventoryItem)var5.next();
                        var3x.playerMob.getInv().addItemsDropRemaining(var6, "addback", var3x.playerMob, true, true);
                     }
                  }
               }

            }
         }
      });
   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else {
         return !this.altarEntity.removed() && this.altarEntity.getLevelObject().inInteractRange(var1.playerMob);
      }
   }

   public boolean open(IncursionData var1) {
      if (var1.canOpen(this)) {
         if (this.client.isServer()) {
            this.altarEntity.openIncursion(this, var1, this.client.getServerClient());
         }

         return true;
      } else {
         return false;
      }
   }

   public static Packet getContainerContent(Server var0, FallenAltarObjectEntity var1) {
      Packet var2 = new Packet();
      new PacketWriter(var2);
      return var2;
   }
}
