package necesse.inventory.item.mountItem;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketMobMount;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MountFollowingMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.ContainerTransferResult;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class MountItem extends Item {
   public String mobStringID;
   public boolean singleUse;
   public boolean setMounterPos = true;

   public MountItem(String var1) {
      super(1);
      this.keyWords.add("mount");
      this.setItemCategory(new String[]{"misc", "mounts"});
      this.mobStringID = var1;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "mountslot"));
      if (this.singleUse) {
         var4.add(Localization.translate("itemtooltip", "singleuse"));
      } else {
         var4.add(Localization.translate("itemtooltip", "infiniteuse"));
      }

      var4.add(Localization.translate("itemtooltip", "summonmounttip", "mob", MobRegistry.getDisplayName(MobRegistry.getMobID(this.mobStringID))));
      return var4;
   }

   public String getInventoryRightClickControlTip(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return var3 == var1.CLIENT_MOUNT_SLOT ? Localization.translate("controls", "removetip") : Localization.translate("controls", "equiptip");
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return () -> {
         if (var3 == var1.CLIENT_MOUNT_SLOT) {
            ContainerTransferResult var4x = var1.transferToSlots(var4, Arrays.asList(new SlotIndexRange(var1.CLIENT_HOTBAR_START, var1.CLIENT_HOTBAR_END), new SlotIndexRange(var1.CLIENT_INVENTORY_START, var1.CLIENT_INVENTORY_END)));
            return new ContainerActionResult(146355839, var4x.error);
         } else {
            ItemCombineResult var3x = var1.getSlot(var1.CLIENT_MOUNT_SLOT).swapItems(var4);
            return var3x.success ? new ContainerActionResult(1755925617) : new ContainerActionResult(151661709, var3x.error);
         }
      };
   }

   public boolean getConstantUse(InventoryItem var1) {
      return false;
   }

   public String canUseMount(InventoryItem var1, PlayerMob var2, Level var3) {
      Mob var4 = var2.getMount();
      if (var4 != null) {
         return null;
      } else {
         Mob var5 = MobRegistry.getMob(this.mobStringID, var3);
         return var5.collidesWith(var3, var2.getX(), var2.getY()) ? Localization.translate("misc", "cannotusemounthere", "mount", this.getDisplayName(var1)) : null;
      }
   }

   public InventoryItem useMount(ServerClient var1, float var2, float var3, InventoryItem var4, Level var5) {
      PlayerMob var6 = var1.playerMob;
      Mob var7 = var6.getMount();
      if (var7 != null) {
         var6.dx = var7.dx;
         var6.dy = var7.dy;
      }

      if (var7 != null) {
         if (var7.getStringID().equals(this.mobStringID)) {
            var1.playerMob.buffManager.removeBuff("summonedmount", true);
         } else {
            var1.playerMob.dismount();
            var5.getServer().network.sendToClientsAt(new PacketMobMount(var1.slot, -1, true, var2, var3), (Level)var5);
         }
      } else {
         Mob var8 = MobRegistry.getMob(this.mobStringID, var5);
         FollowPosition var9 = this.getFollowPosition(var4, var6);
         var1.addFollower("summonedmount", var8, var9, "summonedmount", 1.0F, 1, (BiConsumer)null, false);
         Point2D.Float var10 = this.getMountSpawnPos(var8, var1, var2, var3, var4, var5);
         var8.setPos(var10.x, var10.y, true);
         boolean var11 = var1.playerMob.mount(var8, this.setMounterPos);
         if (var11) {
            var8.dx = var6.dx;
            var8.dy = var6.dy;
            if (var8 instanceof MountFollowingMob) {
               ((MountFollowingMob)var8).summonItem = var4;
            }

            this.beforeSpawn(var8, var4, var6);
            var5.entityManager.addMob(var8, var10.x, var10.y);
            var5.getServer().network.sendToClientsAt(new PacketMobMount(var1.slot, var8.getUniqueID(), this.setMounterPos, var2, var3), (Level)var5);
         }
      }

      if (this.singleUse) {
         var4.setAmount(var4.getAmount() - 1);
      }

      return var4;
   }

   public Point2D.Float getMountSpawnPos(Mob var1, ServerClient var2, float var3, float var4, InventoryItem var5, Level var6) {
      return new Point2D.Float(var2.playerMob.x, var2.playerMob.y);
   }

   public FollowPosition getFollowPosition(InventoryItem var1, PlayerMob var2) {
      return FollowPosition.WALK_CLOSE;
   }

   protected void beforeSpawn(Mob var1, InventoryItem var2, PlayerMob var3) {
   }

   public void setupAttackContentPacket(PacketWriter var1, Level var2, int var3, int var4, PlayerMob var5, InventoryItem var6) {
      super.setupAttackContentPacket(var1, var2, var3, var4, var5, var6);
      var1.putNextFloat(var5.x);
      var1.putNextFloat(var5.y);
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return this.canUseMount(var5, var4, var1);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      float var11 = var10.getNextFloat();
      float var12 = var10.getNextFloat();
      if (var1.isServer()) {
         ServerClient var13 = var4.getServerClient();
         double var14 = var13.playerMob.allowServerMovement(var1.getServer(), var13, var11, var12);
         if (var14 <= 0.0) {
            var4.setPos(var11, var12, false);
         } else {
            GameLog.warn.println(var13.getName() + " attempted to use mount from wrong position, snapping back " + var14);
            var1.getServer().network.sendToClientsAt(new PacketPlayerMovement(var13, false), (ServerClient)var13);
         }

         return this.useMount(var13, var13.playerMob.x, var13.playerMob.y, var6, var1);
      } else {
         if (this.singleUse) {
            var6.setAmount(var6.getAmount() - 1);
         }

         return var6;
      }
   }

   public float getSinkingRate(ItemPickupEntity var1, float var2) {
      return super.getSinkingRate(var1, var2) / 5.0F;
   }
}
