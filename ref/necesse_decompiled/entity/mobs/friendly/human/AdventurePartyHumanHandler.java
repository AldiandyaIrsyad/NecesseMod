package necesse.entity.mobs.friendly.human;

import java.util.Iterator;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.networkField.IntNetworkField;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.SlotPriority;
import necesse.inventory.container.mob.ShopContainerPartyUpdateEvent;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;

public class AdventurePartyHumanHandler {
   private HumanMob mob;
   private IntNetworkField slot;
   private ServerClient client;
   private long nextConsumeTime;
   private long nextShowHungryTime;

   public AdventurePartyHumanHandler(final HumanMob var1) {
      this.mob = var1;
      this.slot = (IntNetworkField)var1.registerNetworkField(new IntNetworkField(-1) {
         public void onChanged(Integer var1x) {
            super.onChanged(var1x);
            if (var1.isServer()) {
               ShopContainerPartyUpdateEvent.sendAndApplyUpdate(var1);
            }

         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onChanged(Object var1x) {
            this.onChanged((Integer)var1x);
         }
      });
   }

   public void addSaveData(String var1, SaveData var2) {
      if (this.client != null) {
         var2.addLong(var1, this.client.authentication);
      }

   }

   public void applyLoadData(String var1, LoadData var2) {
      long var3 = var2.getLong(var1, -1L, false);
      if (var3 != -1L) {
         this.mob.runOnNextServerTick.add(() -> {
            if (this.mob.isServer()) {
               ServerClient var3x = this.mob.getLevel().getServer().getClientByAuth(var3);
               if (var3x != null) {
                  this.set(var3x);
               }
            }

         });
      }

   }

   public void serverTick() {
      if (this.mob.isServer()) {
         if (this.mob.isSettler() && !this.mob.isSettlerOnCurrentLevel()) {
            SettlersWorldData var1 = SettlersWorldData.getSettlersData(this.mob.getLevel().getServer());
            var1.refreshWorldSettler(this.mob, this.isInAdventureParty());
         }

         if (!this.mob.isSettler() && this.isInAdventureParty()) {
            this.set((ServerClient)null);
         } else {
            ServerClient var5 = this.getServerClient();
            if (var5 != null && (this.getPlayerMob().removed() || var5.isDisposed())) {
               this.set((ServerClient)null);
               this.mob.commandGuard((ServerClient)null, this.mob.getX(), this.mob.getY());
            } else {
               if (var5 != null) {
                  PlayerMob var2 = this.getPlayerMob();
                  if (!this.mob.isSamePlace(var2) && this.mob.commandFollowMob == var2) {
                     if (this.mob.hungerLevel <= 0.0F) {
                        LocalMessage var6 = new LocalMessage("ui", "adventurepartyleftnofood", "name", this.mob.getLocalization());
                        var5.sendChatMessage((GameMessage)var6);
                        this.set((ServerClient)null);
                        if (!SettlersWorldData.getSettlersData(this.mob.getLevel().getServer()).returnToSettlement(this.mob, false)) {
                           this.mob.commandGuard((ServerClient)null, this.mob.getX(), this.mob.getY());
                        }
                     } else {
                        this.mob.getLevel().entityManager.changeMobLevel(this.mob, var2.getLevel(), var2.getX(), var2.getY(), true);
                        this.mob.commandFollow(var5, var2);
                        this.mob.ai.blackboard.mover.stopMoving(this.mob);
                     }

                     return;
                  }

                  if (this.nextConsumeTime <= this.mob.getTime()) {
                     InventoryItem var3 = this.tryConsumeItem("tick");
                     if (var3 == null) {
                        this.nextConsumeTime = this.mob.getTime() + (long)GameRandom.globalRandom.getIntBetween(3, 5) * 1000L;
                        if (this.mob.hungerLevel <= 0.0F && !this.mob.isBeingInteractedWith() && !this.mob.isSettlerOnCurrentLevel()) {
                           LocalMessage var4 = new LocalMessage("ui", "adventurepartyleftnofood", "name", this.mob.getLocalization());
                           var5.sendChatMessage((GameMessage)var4);
                           this.set((ServerClient)null);
                           if (!SettlersWorldData.getSettlersData(this.mob.getLevel().getServer()).returnToSettlement(this.mob, false)) {
                              this.mob.commandGuard((ServerClient)null, this.mob.getX(), this.mob.getY());
                           }
                        }
                     } else {
                        this.nextConsumeTime = this.mob.getTime() + 1000L;
                     }
                  }

                  if (this.mob.hungerLevel <= 0.25F && this.nextShowHungryTime <= this.mob.getTime()) {
                     this.nextShowHungryTime = this.mob.getTime() + 15000L;
                     this.mob.showHungry.runAndSend();
                  }
               }

            }
         }
      }
   }

   public InventoryItem tryConsumeItem(String var1) {
      if (this.client == null) {
         return null;
      } else {
         PlayerMob var2 = this.getPlayerMob();
         PlayerInventory var3 = var2.getInv().party;
         Iterator var4 = AdventurePartyConsumableItem.getPartyPriorityList(this.mob.getLevel(), this.mob, this.client, var3, var1).iterator();

         SlotPriority var5;
         InventoryItem var6;
         InventoryItem var8;
         do {
            if (!var4.hasNext()) {
               return null;
            }

            var5 = (SlotPriority)var4.next();
            var6 = var3.getItem(var5.slot);
            AdventurePartyConsumableItem var7 = (AdventurePartyConsumableItem)var6.item;
            var8 = var7.onPartyConsume(this.mob.getLevel(), this.mob, this.client, var6, var1);
         } while(var8 == null);

         this.mob.showPickupAnimation(this.mob.dir == 3 ? this.mob.getX() - 10 : this.mob.getX() + 10, this.mob.getY(), var8.item, 250);
         if (var6.getAmount() <= 0) {
            var3.clearSlot(var5.slot);
         }

         var3.markDirty(var5.slot);
         return var8;
      }
   }

   public void onSecondWindAttempt(MobBeforeHitCalculatedEvent var1) {
      InventoryItem var2 = this.tryConsumeItem("secondwind");
      if (var2 != null && ((AdventurePartyConsumableItem)var2.item).shouldPreventHit(this.mob.getLevel(), this.mob, this.client, var2)) {
         var1.prevent();
      }

   }

   public void set(ServerClient var1) {
      ServerClient var2 = this.client;
      this.client = var1;
      this.slot.set(var1 == null ? -1 : var1.slot);
      if (var2 != null && var2 != var1) {
         var2.adventureParty.serverRemove(this.mob, false, false);
      }

      if (var1 != null) {
         var1.adventureParty.serverAdd(this.mob);
         this.mob.commandFollow(var1, var1.playerMob);
      }

   }

   public void clear(boolean var1) {
      this.set((ServerClient)null);
      if (var1 && !this.mob.isSettlerOnCurrentLevel()) {
         this.mob.commandGuard((ServerClient)null, this.mob.getX(), this.mob.getY());
         SettlersWorldData var2 = SettlersWorldData.getSettlersData(this.mob.getLevel().getServer());
         var2.refreshWorldSettler(this.mob, true);
      }

   }

   public ServerClient getServerClient() {
      return this.client;
   }

   public int getSlot() {
      return (Integer)this.slot.get();
   }

   public boolean isInAdventureParty() {
      return this.getSlot() != -1;
   }

   public ClientClient getClientClient() {
      return this.mob.isClient() && this.getSlot() != -1 ? this.mob.getLevel().getClient().getClient(this.getSlot()) : null;
   }

   public boolean isFollowingMe() {
      if (this.mob.isClient()) {
         return this.getSlot() == this.mob.getLevel().getClient().getSlot();
      } else {
         return false;
      }
   }

   public PlayerMob getPlayerMob() {
      if (this.client != null) {
         return this.client.playerMob;
      } else {
         if (this.mob.isClient()) {
            ClientClient var1 = this.getClientClient();
            if (var1 != null) {
               return var1.playerMob;
            }
         }

         return null;
      }
   }
}
