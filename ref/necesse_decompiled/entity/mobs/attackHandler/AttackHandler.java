package necesse.entity.mobs.attackHandler;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerAttackHandler;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.maps.Level;

public abstract class AttackHandler {
   public final PlayerMob player;
   public final PlayerInventorySlot slot;
   public final InventoryItem item;
   public final int updateInterval;
   private double updateTimer;

   public AttackHandler(PlayerMob var1, PlayerInventorySlot var2, int var3) {
      this.player = var1;
      this.slot = var2;
      this.item = var1.getInv().getItem(var2);
      if (this.item == null) {
         throw new IllegalStateException("Could not find item for attack handler: " + var1.getDisplayName() + ", " + var2.inventoryID + ", " + var2.slot);
      } else {
         this.updateInterval = var3;
         this.updateTimer = 0.0;
      }
   }

   public final void tickUpdate(float var1) {
      if (this.slot.equals(this.player.getSelectedItemSlot()) && this.sameItem(this.player.getSelectedItem())) {
         this.updateTimer += (double)var1;

         while(this.updateTimer >= (double)this.updateInterval) {
            this.updateTimer -= (double)this.updateInterval;
            this.onUpdate();
         }

      } else {
         this.player.endAttackHandler(false);
      }
   }

   public void onMouseInteracted(int var1, int var2) {
   }

   public void onControllerInteracted(float var1, float var2) {
   }

   private boolean sameItem(InventoryItem var1) {
      return var1 != null && this.item.item.getID() == var1.item.getID();
   }

   public final void endAttack() {
      this.player.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.clientEnd());
      this.player.endAttackHandler(true);
   }

   protected void setupContentPacket(PacketWriter var1) {
   }

   protected final void sendPacketUpdate(boolean var1) {
      Packet var2 = new Packet();
      this.setupContentPacket(new PacketWriter(var2));
      if (this.player.isServer()) {
         this.player.getLevel().getServer().network.sendPacket(PacketPlayerAttackHandler.update(var2), (ServerClient)this.player.getServerClient());
      } else if (this.player.isClient()) {
         this.player.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.update(var2));
      }

      if (var1) {
         this.onPacketUpdate(new PacketReader(var2));
      }

   }

   public void onPacketUpdate(PacketReader var1) {
   }

   public abstract void onUpdate();

   public abstract void onEndAttack(boolean var1);

   public boolean isFrom(InventoryItem var1, PlayerInventorySlot var2) {
      if (var1 != null && !this.item.equals(this.player.getLevel(), var1, true, false, "equals")) {
         return false;
      } else {
         return var2 == null || this.slot.equals(var2);
      }
   }

   public boolean canRunAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PlayerInventorySlot var6) {
      return false;
   }

   public void drawControllerAimPos(GameCamera var1, Level var2, PlayerMob var3, InventoryItem var4) {
      var4.item.drawControllerAimPos(var1, var2, var3, var4);
   }
}
