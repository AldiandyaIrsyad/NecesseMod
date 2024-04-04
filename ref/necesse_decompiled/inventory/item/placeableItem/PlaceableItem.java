package necesse.inventory.item.placeableItem;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.LinkedList;
import necesse.engine.GlobalData;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerPlaceItem;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class PlaceableItem extends Item implements PlaceableItemInterface {
   public final boolean singleUse;
   protected boolean controllerIsTileBasedPlacing;

   public PlaceableItem(int var1, boolean var2) {
      super(var1);
      this.singleUse = var2;
   }

   public Point getControllerAttackLevelPos(Level var1, float var2, float var3, PlayerMob var4, InventoryItem var5) {
      Point2D.Float var6 = GameMath.normalize(var2, var3);
      int var7 = this.getPlaceRange(var5, var4);
      if (!this.controllerIsTileBasedPlacing) {
         for(int var20 = 32; var20 < var7; var20 += 10) {
            Point var21 = new Point((int)(var4.x + var6.x * (float)var7), (int)(var4.y + var6.y * (float)var7));
            Packet var22 = new Packet();
            this.setupAttackContentPacket(new PacketWriter(var22), var1, var21.x, var21.y, var4, var5);
            if (this.canPlace(var1, var21.x, var21.y, var4, var5, new PacketReader(var22)) == null) {
               return var21;
            }
         }

         return super.getControllerAttackLevelPos(var1, var2, var3, var4, var5);
      } else {
         Line2D.Float var8 = new Line2D.Float(var4.x + var6.x * 32.0F, var4.y + var6.y * 32.0F, var4.x + var6.x * (float)var7, var4.y + var6.y * (float)var7);
         Rectangle var9 = var8.getBounds();
         int var10 = var9.x / 32 - 1;
         int var11 = var9.y / 32 - 1;
         int var12 = (var9.x + var9.width) / 32 + 1;
         int var13 = (var9.y + var9.height) / 32 + 1;
         if (var10 < 0) {
            var10 = 0;
         }

         if (var11 < 0) {
            var11 = 0;
         }

         if (var12 > var1.width) {
            var12 = var1.width;
         }

         if (var13 > var1.height) {
            var13 = var1.height;
         }

         LinkedList var14 = new LinkedList();

         for(int var15 = var10; var15 <= var12; ++var15) {
            for(int var16 = var11; var16 <= var13; ++var16) {
               Rectangle var17 = new Rectangle(var15 * 32, var16 * 32, 32, 32);
               if (var8.intersects(var17)) {
                  Point var18 = new Point(var15 * 32 + 16, var16 * 32 + 16);
                  Packet var19 = new Packet();
                  this.setupAttackContentPacket(new PacketWriter(var19), var1, var18.x, var18.y, var4, var5);
                  if (this.canPlace(var1, var18.x, var18.y, var4, var5, new PacketReader(var19)) == null) {
                     var14.add(var18);
                  }
               }
            }
         }

         Point var23 = (Point)var14.stream().min(Comparator.comparingDouble((var1x) -> {
            return (double)var4.getDistance((float)var1x.x, (float)var1x.y);
         })).orElse((Object)null);
         if (var23 != null) {
            return var23;
         } else {
            return new Point((int)(var4.x + var2 * (float)var7), (int)(var4.y + var3 * (float)var7));
         }
      }
   }

   public void drawControllerAimPos(GameCamera var1, Level var2, PlayerMob var3, InventoryItem var4) {
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      String var11 = this.canPlace(var1, var2, var3, var4, var6, new PacketReader(var10));
      if (var1.isServer() && var4.isServerClient() && this.shouldSendToOtherClients(var1, var2, var3, var4, var6, var11, new PacketReader(var10))) {
         Packet var12 = (new PacketReader(var10)).getRemainingBytesPacket();
         ServerClient var13 = var4.getServerClient();
         var1.getServer().network.sendToClientsAtExcept(new PacketPlayerPlaceItem(var1, var13, var7, this, var2, var3, var11, var12), (ServerClient)var13, var13);
      }

      if (var11 == null) {
         return this.onPlace(var1, var2, var3, var4, var6, var10);
      } else {
         if (var1.isClient() && GlobalData.debugActive()) {
            System.out.println(this.getStringID() + " place failed: " + var11);
         }

         return this.onAttemptPlace(var1, var2, var3, var4, var6, var10, var11);
      }
   }

   public boolean shouldSendToOtherClients(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, String var6, PacketReader var7) {
      return false;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return var4.getPositionPoint().distance((double)var2, (double)var3) > (double)this.getPlaceRange(var5, var4) ? "outofrange" : null;
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return var5;
   }

   public void onOtherPlayerPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
   }

   public InventoryItem onAttemptPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, String var7) {
      return var5;
   }

   public void onOtherPlayerPlaceAttempt(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, String var7) {
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotation(var5);
   }

   public boolean getConstantUse(InventoryItem var1) {
      return true;
   }

   public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7) {
   }
}
