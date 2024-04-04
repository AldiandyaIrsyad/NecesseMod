package necesse.engine.commands.serverCommands.setupCommand;

import java.util.Objects;
import java.util.function.Function;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.PlayerInventorySlot;

public class SimpleItemBuild extends CharacterBuild {
   public final Function<PlayerInventoryManager, PlayerInventorySlot> slotGetter;
   public final Function<ServerClient, InventoryItem> itemSupplier;

   public SimpleItemBuild(Function<PlayerInventoryManager, PlayerInventorySlot> var1, Function<ServerClient, InventoryItem> var2) {
      this.slotGetter = var1;
      Objects.requireNonNull(var2);
      this.itemSupplier = var2;
   }

   public SimpleItemBuild(int var1, Function<ServerClient, InventoryItem> var2) {
      this((var1x) -> {
         return new PlayerInventorySlot(var1x.main, var1);
      }, var2);
   }

   public SimpleItemBuild(Function<ServerClient, InventoryItem> var1) {
      this((Function)null, (Function)var1);
   }

   public SimpleItemBuild(Function<PlayerInventoryManager, PlayerInventorySlot> var1, String var2) {
      this(var1, (var1x) -> {
         return new InventoryItem(var2);
      });
   }

   public SimpleItemBuild(int var1, String var2, int var3) {
      this(var1, (var2x) -> {
         return new InventoryItem(var2, var3);
      });
   }

   public SimpleItemBuild(String var1, int var2) {
      this((var2x) -> {
         return new InventoryItem(var1, var2);
      });
   }

   public void apply(ServerClient var1) {
      InventoryItem var2 = (InventoryItem)this.itemSupplier.apply(var1);
      if (var2 != null) {
         PlayerInventoryManager var3 = var1.playerMob.getInv();
         if (this.slotGetter == null) {
            var1.playerMob.getInv().main.addItem(var1.getLevel(), var1.playerMob, var2, "addbuild", (InventoryAddConsumer)null);
         } else {
            PlayerInventorySlot var4 = (PlayerInventorySlot)this.slotGetter.apply(var3);
            if (var4.slot < 0) {
               var4.getInv(var3).addItem(var1.getLevel(), var1.playerMob, var2, "addbuild", (InventoryAddConsumer)null);
            } else {
               var1.playerMob.getInv().setItem(var4, var2);
            }
         }
      }

   }
}
