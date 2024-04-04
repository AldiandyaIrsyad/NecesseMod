package necesse.engine.network.gameNetworkData;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.GNDRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemArray extends GNDItem implements Iterable<GNDItem> {
   private GNDItem[] items;

   public GNDItemArray(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemArray(LoadData var1) {
      this.applyLoadData(var1);
   }

   public GNDItemArray(int var1) {
      this.items = new GNDItem[var1];
   }

   public GNDItemArray(GNDItem... var1) {
      this.items = var1;
   }

   public GNDItem get(int var1) {
      return this.items[var1];
   }

   public void set(int var1, GNDItem var2) {
      this.items[var1] = var2;
   }

   public int length() {
      return this.items.length;
   }

   public Iterator<GNDItem> iterator() {
      return Arrays.stream(this.items).iterator();
   }

   public void forEach(Consumer<? super GNDItem> var1) {
      GNDItem[] var2 = this.items;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GNDItem var5 = var2[var4];
         var1.accept(var5);
      }

   }

   public Spliterator<GNDItem> spliterator() {
      return Arrays.stream(this.items).spliterator();
   }

   public String toString() {
      return Arrays.toString(this.items);
   }

   public boolean isDefault() {
      return this.items.length == 0;
   }

   public boolean equals(GNDItem var1) {
      if (!(var1 instanceof GNDItemArray)) {
         return false;
      } else {
         GNDItemArray var2 = (GNDItemArray)var1;
         if (var2.length() != this.length()) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.items.length; ++var3) {
               if (this.items[var3] != null && var2.items[var3] != null) {
                  if (!this.items[var3].equals(var2.items[var3])) {
                     return false;
                  }
               } else {
                  if (this.items[var3] == null && var2.items[var3] != null) {
                     return false;
                  }

                  if (this.items[var3] != null && var2.items[var3] == null) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public GNDItem copy() {
      GNDItem[] var1 = new GNDItem[this.items.length];

      for(int var2 = 0; var2 < this.items.length; ++var2) {
         GNDItem var3 = this.items[var2];
         var1[var2] = var3 == null ? null : var3.copy();
      }

      return new GNDItemArray(var1);
   }

   public void addSaveData(SaveData var1) {
      GNDItem[] var2 = this.items;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         if (var5 == null) {
            var5 = new GNDItemNull();
         }

         SaveData var6 = new SaveData("");
         GNDRegistry.writeGNDItem((SaveData)var6, (GNDItem)var5);
         var1.addSaveData(var6);
      }

   }

   private void applyLoadData(LoadData var1) {
      List var2 = var1.getLoadData();
      this.items = new GNDItem[var2.size()];

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         this.items[var3] = GNDRegistry.loadGNDItem((LoadData)var2.get(var3));
         if (this.items[var3] instanceof GNDItemNull) {
            this.items[var3] = null;
         }
      }

   }

   public Packet getContentPacket() {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      this.writePacket(var2);
      return var1;
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.items.length);
      GNDItem[] var2 = this.items;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         if (var5 == null) {
            var5 = new GNDItemNull();
         }

         GNDRegistry.writeGNDItem((PacketWriter)var1, (GNDItem)var5);
      }

   }

   public void readPacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();
      this.items = new GNDItem[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.items[var3] = GNDRegistry.readGNDItem(var1);
         if (this.items[var3] instanceof GNDItemNull) {
            this.items[var3] = null;
         }
      }

   }
}
