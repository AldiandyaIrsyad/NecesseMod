package necesse.gfx.forms.components.lists;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketSpawnItem;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class FormDebugItemList extends FormItemList {
   private String nameFilter = "";
   private boolean showUnobtainable;
   private Client client;

   public FormDebugItemList(int var1, int var2, int var3, int var4, Client var5) {
      super(var1, var2, var3, var4);
      this.client = var5;
      this.reset();
      this.setFilter(this.nameFilter);
   }

   public void showUnobtainable(boolean var1) {
      this.showUnobtainable = var1;
      this.setFilter(this.nameFilter);
   }

   public void addAllItems(List<InventoryItem> var1) {
      Iterator var2 = ItemRegistry.getItems().iterator();

      while(var2.hasNext()) {
         Item var3 = (Item)var2.next();
         if (var3 != null) {
            var3.addDefaultItems(var1, this.client == null ? null : this.client.getPlayer());
         }
      }

   }

   public void onItemClicked(InventoryItem var1, InputEvent var2) {
      PlayerMob var3 = this.client.getPlayer();
      var1 = var1.copy(var2.getID() != -100 && !var2.isControllerEvent() ? 1 : var1.item.getStackSize());
      boolean var4 = var3.isInventoryExtended() && !Screen.isKeyDown(340);
      this.client.network.sendPacket(new PacketSpawnItem(var1, var4));
      if (var4) {
         if (var3.getDraggingItem() == null) {
            var3.setDraggingItem(var1);
         } else if (!var3.getDraggingItem().combine(var3.getLevel(), var3, var3.getInv().drag, 0, var1, "spawnitem", (InventoryAddConsumer)null).success) {
            var3.setDraggingItem((InventoryItem)null);
         }

         this.playTickSound();
      } else {
         int var5 = var1.getAmount();
         var3.getInv().addItem(var1, true, "give", (InventoryAddConsumer)null);
         if (var1.getAmount() != var5) {
            Screen.playSound(GameResources.pop, SoundEffect.ui());
         }
      }

   }

   public void setFilter(String var1) {
      this.nameFilter = var1.toLowerCase();
      String[] var2 = this.nameFilter.split("[|]");
      this.setFilter((var2x) -> {
         return !this.showUnobtainable && !ItemRegistry.isObtainable(var2x.item.getID()) ? false : Arrays.stream(var2).anyMatch((var1) -> {
            return var2x.item.matchesSearch(var2x, (PlayerMob)null, var1);
         });
      });
   }
}
