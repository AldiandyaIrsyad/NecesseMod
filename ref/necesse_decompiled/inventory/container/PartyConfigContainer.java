package necesse.inventory.container;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.server.AdventureParty;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.container.customAction.CommandPartyAttackAction;
import necesse.inventory.container.customAction.CommandPartyDisbandAction;
import necesse.inventory.container.customAction.CommandPartyFollowMeAction;
import necesse.inventory.container.customAction.CommandPartyGuardAction;
import necesse.inventory.container.slots.PartyInventoryContainerSlot;

public class PartyConfigContainer extends Container {
   public int PARTY_SLOTS_START = -1;
   public int PARTY_SLOTS_END = -1;
   public final CommandPartyFollowMeAction commandFollowMeAction;
   public final CommandPartyDisbandAction commandDisbandAction;
   public final CommandPartyGuardAction commandGuardAction;
   public final CommandPartyAttackAction commandAttackAction;

   public PartyConfigContainer(NetworkClient var1, int var2, Packet var3) {
      super(var1, var2);
      AdventureParty var4 = null;
      if (var1.isServer()) {
         var4 = var1.getServerClient().adventureParty;
      } else if (var1.isClient() && var1.getClientClient().isLocalClient()) {
         var4 = var1.getClientClient().getClient().adventureParty;
      }

      for(int var5 = 0; var5 < PlayerInventoryManager.MAX_PARTY_INVENTORY_SIZE; ++var5) {
         int var6 = this.addSlot(new PartyInventoryContainerSlot(var1.playerMob.getInv().party, var5, var4));
         if (this.PARTY_SLOTS_START == -1) {
            this.PARTY_SLOTS_START = var6;
         }

         if (this.PARTY_SLOTS_END == -1) {
            this.PARTY_SLOTS_END = var6;
         }

         this.PARTY_SLOTS_START = Math.min(this.PARTY_SLOTS_START, var6);
         this.PARTY_SLOTS_END = Math.max(this.PARTY_SLOTS_END, var6);
      }

      this.addInventoryQuickTransfer(this.PARTY_SLOTS_START, this.PARTY_SLOTS_END);
      this.commandFollowMeAction = (CommandPartyFollowMeAction)this.registerAction(new CommandPartyFollowMeAction(this));
      this.commandDisbandAction = (CommandPartyDisbandAction)this.registerAction(new CommandPartyDisbandAction(this));
      this.commandGuardAction = (CommandPartyGuardAction)this.registerAction(new CommandPartyGuardAction(this));
      this.commandAttackAction = (CommandPartyAttackAction)this.registerAction(new CommandPartyAttackAction(this));
   }
}
