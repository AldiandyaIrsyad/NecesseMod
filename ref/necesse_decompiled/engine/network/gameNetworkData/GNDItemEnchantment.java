package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.save.LoadData;

public class GNDItemEnchantment extends GNDRegistryItem {
   public GNDItemEnchantment(String var1) {
      super(var1);
   }

   public GNDItemEnchantment(int var1) {
      super(var1);
   }

   public GNDItemEnchantment(PacketReader var1) {
      super(var1);
   }

   public GNDItemEnchantment(LoadData var1) {
      super(var1);
   }

   protected int toID(String var1) {
      return EnchantmentRegistry.getEnchantmentID(var1);
   }

   protected String toStringID(int var1) {
      return EnchantmentRegistry.getEnchantmentStringID(var1);
   }

   public GNDItem copy() {
      return new GNDItemEnchantment(this.getRegistryID());
   }

   public static GNDItemEnchantment convertEnchantmentID(GNDItem var0) {
      if (var0 instanceof GNDItemEnchantment) {
         return (GNDItemEnchantment)var0;
      } else if (var0 instanceof GNDItem.GNDPrimitive) {
         return new GNDItemEnchantment(((GNDItem.GNDPrimitive)var0).getShort());
      } else {
         return var0 == null ? new GNDItemEnchantment(-1) : new GNDItemEnchantment(var0.toString());
      }
   }
}
