package necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketMobMount;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.summonFollowingMob.SummonedFollowingMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class MountFollowingMob extends SummonedFollowingMob {
   public InventoryItem summonItem;
   public boolean removeWhenNotInInventory = true;

   public MountFollowingMob(int var1) {
      super(var1);
   }

   public void serverTick() {
      super.serverTick();
      if (this.summonItem != null && this.removeWhenNotInInventory && this.getLevel().tickManager().isGameTickInSecond(5)) {
         PlayerMob var1 = this.getFollowingServerPlayer();
         if (var1 != null) {
            boolean var2 = var1.getInv().streamSlots(false, false, false).anyMatch((var2x) -> {
               InventoryItem var3 = var2x.getItem(var1.getInv());
               return var3 != null && var3.equals(this.getLevel(), this.summonItem, true, false, "equals");
            });
            if (!var2) {
               this.remove(0.0F, 0.0F, (Attacker)null, true);
            }
         }
      }

   }

   public void interact(PlayerMob var1) {
      super.interact(var1);
      if (this.isServer() && var1.isServerClient()) {
         ServerClient var2 = var1.getServerClient();
         if (var1.getUniqueID() == this.rider) {
            var1.dismount();
            var2.getServer().network.sendToClientsAt(new PacketMobMount(var2.slot, -1, false, var1.x, var1.y), (Level)this.getLevel());
         } else if (var2.isFollower(this)) {
            if (var1.mount(this, false)) {
               var2.getServer().network.sendToClientsAt(new PacketMobMount(var2.slot, this.getUniqueID(), false, var1.x, var1.y), (Level)this.getLevel());
            }
         } else {
            var2.sendChatMessage((GameMessage)(new LocalMessage("misc", "mountnotown")));
         }
      }

   }

   protected void playDeathSound() {
   }

   public boolean canInteract(Mob var1) {
      return !this.isMounted() && this.isFollowing(var1) || var1.getUniqueID() == this.rider;
   }

   private boolean isFollowing(Mob var1) {
      if (this.isServer()) {
         return this.getFollowingServerPlayer() == var1;
      } else if (this.isClient()) {
         return this.getFollowingClientPlayer() == var1;
      } else {
         return false;
      }
   }

   protected String getInteractTip(PlayerMob var1, boolean var2) {
      return this.isMounted() ? null : Localization.translate("controls", "mounttip");
   }
}
