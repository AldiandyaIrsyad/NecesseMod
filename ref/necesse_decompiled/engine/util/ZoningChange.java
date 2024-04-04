package necesse.engine.util;

import java.awt.Rectangle;
import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class ZoningChange {
   private Packet packet;

   private ZoningChange(Packet var1) {
      this.packet = var1;
   }

   public boolean applyTo(Zoning var1) {
      if (var1 == null) {
         return false;
      } else {
         PacketReader var2 = new PacketReader(this.packet);
         ChangeType[] var3 = ZoningChange.ChangeType.values();
         int var4 = var2.getNextMaxValue(var3.length + 1);
         if (var4 >= 0 && var4 < var3.length) {
            ChangeType var5 = var3[var4];
            int var6;
            int var7;
            int var8;
            int var9;
            switch (var5) {
               case EXPAND:
                  var6 = var2.getNextInt();
                  var7 = var2.getNextInt();
                  var8 = var2.getNextInt();
                  var9 = var2.getNextInt();
                  return var1.addRectangle(new Rectangle(var6, var7, var8, var9));
               case SHRINK:
                  var6 = var2.getNextInt();
                  var7 = var2.getNextInt();
                  var8 = var2.getNextInt();
                  var9 = var2.getNextInt();
                  return var1.removeRectangle(new Rectangle(var6, var7, var8, var9));
               case INVERT:
                  var6 = var2.getNextInt();
                  var7 = var2.getNextInt();
                  var8 = var2.getNextInt();
                  var9 = var2.getNextInt();
                  var1.invert(new Rectangle(var6, var7, var8, var9));
                  return true;
               case INVERT_FULL:
                  var1.invert();
                  return true;
               case FULL:
                  var1.readZonePacket(var2);
                  return true;
               default:
                  return false;
            }
         } else {
            GameLog.warn.println("Tried to apply invalid ItemCategoriesFilterChange to type index " + var4);
            return false;
         }
      }
   }

   public void write(PacketWriter var1) {
      var1.putNextContentPacket(this.packet);
   }

   public static ZoningChange fromPacket(PacketReader var0) {
      return new ZoningChange(var0.getNextContentPacket());
   }

   public static ZoningChange expand(Rectangle var0) {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      var2.putNextMaxValue(ZoningChange.ChangeType.EXPAND.ordinal(), ZoningChange.ChangeType.values().length + 1);
      var2.putNextInt(var0.x);
      var2.putNextInt(var0.y);
      var2.putNextInt(var0.width);
      var2.putNextInt(var0.height);
      return new ZoningChange(var1);
   }

   public static ZoningChange shrink(Rectangle var0) {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      var2.putNextMaxValue(ZoningChange.ChangeType.SHRINK.ordinal(), ZoningChange.ChangeType.values().length + 1);
      var2.putNextInt(var0.x);
      var2.putNextInt(var0.y);
      var2.putNextInt(var0.width);
      var2.putNextInt(var0.height);
      return new ZoningChange(var1);
   }

   public static ZoningChange invert(Rectangle var0) {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      var2.putNextMaxValue(ZoningChange.ChangeType.INVERT.ordinal(), ZoningChange.ChangeType.values().length + 1);
      var2.putNextInt(var0.x);
      var2.putNextInt(var0.y);
      var2.putNextInt(var0.width);
      var2.putNextInt(var0.height);
      return new ZoningChange(var1);
   }

   public static ZoningChange fullInvert() {
      Packet var0 = new Packet();
      PacketWriter var1 = new PacketWriter(var0);
      var1.putNextMaxValue(ZoningChange.ChangeType.INVERT_FULL.ordinal(), ZoningChange.ChangeType.values().length + 1);
      return new ZoningChange(var0);
   }

   public static ZoningChange full(Zoning var0) {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      var2.putNextMaxValue(ZoningChange.ChangeType.FULL.ordinal(), ZoningChange.ChangeType.values().length + 1);
      var0.writeZonePacket(var2);
      return new ZoningChange(var1);
   }

   private static enum ChangeType {
      EXPAND,
      SHRINK,
      INVERT,
      INVERT_FULL,
      FULL;

      private ChangeType() {
      }

      // $FF: synthetic method
      private static ChangeType[] $values() {
         return new ChangeType[]{EXPAND, SHRINK, INVERT, INVERT_FULL, FULL};
      }
   }
}
