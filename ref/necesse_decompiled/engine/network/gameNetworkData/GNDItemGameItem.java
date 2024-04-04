package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.inventory.item.Item;

public class GNDItemGameItem extends GNDItemIDData<Item> {
   public GNDItemGameItem(int var1) {
      super(var1);
   }

   public GNDItemGameItem(String var1) {
      super(var1);
   }

   public GNDItemGameItem(Item var1) {
      super((Object)var1);
   }

   public GNDItemGameItem(PacketReader var1) {
      super(var1);
   }

   public GNDItemGameItem(LoadData var1) {
      super(var1);
   }

   protected String getStringID(int var1) {
      return ItemRegistry.getItemStringID(var1);
   }

   protected int getID(String var1) {
      return ItemRegistry.getItemID(var1);
   }

   public Item getItem() {
      return ItemRegistry.getItem(this.id);
   }

   public GNDItem copy() {
      return new GNDItemGameItem(this.id);
   }

   protected int getID(Item var1) {
      return var1.getID();
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected int getID(Object var1) {
      return this.getID((Item)var1);
   }
}
