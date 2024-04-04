package necesse.gfx.ui.debug;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.network.PacketManager;
import necesse.engine.network.SizePacket;
import necesse.engine.network.StatPacket;
import necesse.engine.network.client.Client;
import necesse.engine.registries.PacketRegistry;
import necesse.gfx.TableContentDraw;

public class DebugNetwork extends Debug {
   private boolean recent = true;
   private boolean sortByAmount = true;

   public DebugNetwork() {
   }

   protected void submitDebugInputEvent(InputEvent var1, Client var2) {
      if (var1.state && var1.getID() == 79) {
         this.recent = !this.recent;
         var1.use();
      } else if (var1.state && var1.getID() == 80) {
         this.sortByAmount = !this.sortByAmount;
         var1.use();
      }

   }

   protected void drawDebug(Client var1) {
      PacketManager var2 = var1.packetManager;
      if (Settings.serverPerspective && var1.getLocalServer() != null) {
         var2 = var1.getLocalServer().packetManager;
      }

      StatPacket[] var3 = new StatPacket[PacketRegistry.getTotalRegistered()];
      StatPacket[] var4 = new StatPacket[PacketRegistry.getTotalRegistered()];
      if (this.recent) {
         Iterable var5 = var2.getRecentInPackets();
         synchronized(var5) {
            Iterator var7 = var5.iterator();

            while(true) {
               if (!var7.hasNext()) {
                  break;
               }

               SizePacket var8 = (SizePacket)var7.next();
               if (var8.type != -1) {
                  StatPacket var9 = var3[var8.type];
                  if (var9 == null) {
                     var9 = new StatPacket(var8.type);
                     var3[var8.type] = var9;
                  }

                  ++var9.amount;
                  var9.bytes += var8.byteSize;
               }
            }
         }

         Iterable var6 = var2.getRecentOutPackets();
         synchronized(var6) {
            Iterator var19 = var6.iterator();

            while(var19.hasNext()) {
               SizePacket var21 = (SizePacket)var19.next();
               if (var21.type != -1) {
                  StatPacket var10 = var4[var21.type];
                  if (var10 == null) {
                     var10 = new StatPacket(var21.type);
                     var4[var21.type] = var10;
                  }

                  ++var10.amount;
                  var10.bytes += var21.byteSize;
               }
            }
         }
      } else {
         for(int var15 = 0; var15 < PacketRegistry.getTotalRegistered(); ++var15) {
            var3[var15] = var2.getTotalInStats(var15);
            var4[var15] = var2.getTotalOutStats(var15);
         }
      }

      if (this.sortByAmount) {
         Arrays.sort(var3, Comparator.nullsLast(Comparator.comparingInt((var0) -> {
            return -var0.amount;
         })));
         Arrays.sort(var4, Comparator.nullsLast(Comparator.comparingInt((var0) -> {
            return -var0.amount;
         })));
      } else {
         Arrays.sort(var3, Comparator.nullsLast(Comparator.comparingInt((var0) -> {
            return -var0.bytes;
         })));
         Arrays.sort(var4, Comparator.nullsLast(Comparator.comparingInt((var0) -> {
            return -var0.bytes;
         })));
      }

      if (this.recent) {
         this.drawString("Press 'O' to show total packets");
      } else {
         this.drawString("Press 'O' to show recent packets");
      }

      if (this.sortByAmount) {
         this.drawString("Press 'P' to sort by bytes");
      } else {
         this.drawString("Press 'P' to sort by count");
      }

      TableContentDraw var16 = new TableContentDraw();
      if (this.recent) {
         var16.newRow().addTextColumn("Recent packets in: ", bigFontOptions, 5, 5).addTextColumn(var2.getAverageIn() + "/s", bigFontOptions, 5, 0).addTextColumn("(" + var2.getAverageInPackets() + ")", bigFontOptions, 10, 0);
      } else {
         var16.newRow().addTextColumn("Total packets in: ", bigFontOptions, 5, 5).addTextColumn(var2.getTotalIn(), bigFontOptions, 5, 0).addTextColumn("(" + var2.getTotalInPackets() + ")", bigFontOptions, 10, 0);
      }

      String var11;
      String var12;
      int var17;
      StatPacket var18;
      TableContentDraw.TableRow var20;
      float var22;
      float var23;
      for(var17 = 0; var17 < Math.min(10, var3.length); ++var17) {
         var18 = var3[var17];
         var20 = var16.newRow();
         if (var18 == null) {
            var20.addTextColumn(" ", smallFontOptions);
         } else {
            var22 = (float)((int)((float)var18.amount / (float)var2.getTotalInPackets() * 10000.0F)) / 100.0F;
            var23 = (float)((int)((float)var18.bytes / (float)var2.getTotalInBytes() * 10000.0F)) / 100.0F;
            var11 = var23 + "% (" + var18.getBytes() + ")";
            var12 = var22 + "% (" + var18.amount + ")";
            var20.addTextColumn(PacketRegistry.getPacketSimpleName(var18.type) + ":", smallFontOptions, 5, 2).addTextColumn(this.sortByAmount ? var12 : var11, smallFontOptions, 10, 0).addTextColumn(this.sortByAmount ? var11 : var12, smallFontOptions, 10, 0);
         }
      }

      var16.newRow().addTextColumn("", smallFontOptions, 0, 2);
      if (this.recent) {
         var16.newRow().addTextColumn("Recent packets out: ", bigFontOptions, 5, 5).addTextColumn(var2.getAverageOut() + "/s", bigFontOptions, 5, 0).addTextColumn("(" + var2.getAverageOutPackets() + ")", bigFontOptions, 10, 0);
      } else {
         var16.newRow().addTextColumn("Total packets out: ", bigFontOptions, 5, 5).addTextColumn(var2.getTotalOut(), bigFontOptions, 5, 0).addTextColumn("(" + var2.getTotalOutPackets() + ")", bigFontOptions, 10, 0);
      }

      for(var17 = 0; var17 < Math.min(10, var4.length); ++var17) {
         var18 = var4[var17];
         var20 = var16.newRow();
         if (var18 == null) {
            var20.addTextColumn(" ", smallFontOptions);
         } else {
            var22 = (float)((int)((float)var18.amount / (float)var2.getTotalOutPackets() * 10000.0F)) / 100.0F;
            var23 = (float)((int)((float)var18.bytes / (float)var2.getTotalOutBytes() * 10000.0F)) / 100.0F;
            var11 = var23 + "% (" + var18.getBytes() + ")";
            var12 = var22 + "% (" + var18.amount + ")";
            var20.addTextColumn(PacketRegistry.getPacketSimpleName(var18.type) + ":", smallFontOptions, 5, 2).addTextColumn(this.sortByAmount ? var12 : var11, smallFontOptions, 10, 0).addTextColumn(this.sortByAmount ? var11 : var12, smallFontOptions, 10, 0);
         }
      }

      var16.draw(10, this.skipY(var16.getHeight() + 10));
   }
}
