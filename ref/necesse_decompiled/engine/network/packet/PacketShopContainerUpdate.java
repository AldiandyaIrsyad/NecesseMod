package necesse.engine.network.packet;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.levelCache.SettlementCache;
import necesse.entity.levelEvent.SmokePuffLevelEvent;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.inventory.container.Container;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.events.SettlementOpenSettlementListEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class PacketShopContainerUpdate extends Packet {
   public final Type secondType;
   public final Packet content;

   public PacketShopContainerUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.secondType = PacketShopContainerUpdate.Type.values()[var2.getNextByteUnsigned()];
      this.content = var2.getNextContentPacket();
   }

   private PacketShopContainerUpdate(Type var1, Packet var2) {
      this.secondType = var1;
      this.content = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1.ordinal());
      var3.putNextContentPacket(var2);
   }

   public static PacketShopContainerUpdate settlementsList(Server var0, ServerClient var1, HumanMob var2) {
      ArrayList var3 = new ArrayList();
      Iterator var4 = var0.levelCache.getSettlements().iterator();

      while(var4.hasNext()) {
         SettlementCache var5 = (SettlementCache)var4.next();
         if (var5.hasAccess(var1) && var2.isValidRecruitment(var5, var1) && var5.name != null) {
            var3.add(var5);
         }
      }

      return settlementsList(var2, var3);
   }

   public static PacketShopContainerUpdate settlementsList(HumanMob var0, ArrayList<SettlementCache> var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextInt(var0.getUniqueID());
      var3.putNextShortUnsigned(var1.size());
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         SettlementCache var5 = (SettlementCache)var4.next();
         var3.putNextInt(var5.islandX);
         var3.putNextInt(var5.islandY);
         var3.putNextContentPacket(var5.name.getContentPacket());
      }

      return new PacketShopContainerUpdate(PacketShopContainerUpdate.Type.SETTLEMENTS_LIST, var2);
   }

   public static PacketShopContainerUpdate recruitSettler(int var0, int var1, int var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var0);
      var4.putNextInt(var1);
      var4.putNextInt(var2);
      return new PacketShopContainerUpdate(PacketShopContainerUpdate.Type.RECRUIT_SETTLER, var3);
   }

   public static PacketShopContainerUpdate recruitResponse(HumanMob var0, GameMessage var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextInt(var0.getUniqueID());
      var3.putNextBoolean(var1 != null);
      if (var1 != null) {
         var3.putNextContentPacket(var1.getContentPacket());
      }

      return new PacketShopContainerUpdate(PacketShopContainerUpdate.Type.RECRUIT_RESPONSE, var2);
   }

   public ShopContainer checkContainer(Container var1) {
      return var1 instanceof ShopContainer ? (ShopContainer)var1 : null;
   }

   public static void recruitSettler(Server var0, ServerClient var1, ShopContainer var2, Supplier<Level> var3) {
      LocalMessage var4 = null;
      if (!var2.canPayForRecruit()) {
         var4 = new LocalMessage("ui", "settlerrecruitnotpay");
      } else {
         Level var5 = (Level)var3.get();
         SettlementLevelData var6 = SettlementLevelData.getSettlementData(var5);
         if (var6 == null) {
            var4 = new LocalMessage("ui", "settlementnotfound");
         } else if (!var5.settlementLayer.doesClientHaveAccess(var1)) {
            var4 = new LocalMessage("ui", "settlerrecruitnoperm");
         } else {
            Settler var7 = var2.humanShop.getSettler();
            if (var7 == null) {
               var4 = new LocalMessage("settlement", "notsettler");
            } else {
               int var8 = var6.countTotalSettlers();
               int var9 = var0.world.settings.maxSettlersPerSettlement;
               if (var9 >= 0 && var8 >= var9) {
                  var4 = new LocalMessage("ui", "settlementmaxsettlers", new Object[]{"max", var9});
               } else {
                  LevelSettler var10 = new LevelSettler(var6, var7, var2.humanShop.getUniqueID(), var2.humanShop.getSettlerSeed());
                  if (!var6.canMoveIn(var10, -1)) {
                     var4 = new LocalMessage("ui", "settlementfull", "settlement", var5.settlementLayer.getSettlementName());
                  } else {
                     Point var11 = Settler.getNewSettlerSpawnPos(var2.humanShop, var5);
                     if (var11 == null) {
                        var4 = new LocalMessage("ui", "settlementfull", "settlement", var5.settlementLayer.getSettlementName());
                     } else {
                        if (var5 != var2.humanShop.getLevel()) {
                           var2.humanShop.getLevel().entityManager.addLevelEvent(new SmokePuffLevelEvent(var2.humanShop.x, var2.humanShop.y, 64, new Color(50, 50, 50)));
                           var2.humanShop.getLevel().entityManager.changeMobLevel(var2.humanShop, var5, var11.x, var11.y, true);
                        }

                        var6.moveIn(var10);
                        var2.payForRecruit();
                        var2.humanShop.onRecruited(var1, var6, var10);
                        var6.sendEvent(SettlementSettlersChangedEvent.class);
                        LocalMessage var12 = new LocalMessage("ui", "settlementjoined", new Object[]{"settler", var2.humanShop.getLocalization(), "settlement", var5.settlementLayer.getSettlementName()});
                        var5.settlementLayer.streamTeamMembers().forEach((var1x) -> {
                           var1x.sendChatMessage((GameMessage)var12);
                        });
                     }
                  }
               }
            }
         }
      }

      var1.sendPacket(recruitResponse(var2.humanShop, var4));
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      ShopContainer var4 = this.checkContainer(var3.getContainer());
      if (var4 != null) {
         PacketReader var5 = new PacketReader(this.content);
         switch (this.secondType) {
            case RECRUIT_SETTLER:
               int var6 = var5.getNextInt();
               int var7 = var5.getNextInt();
               int var8 = var5.getNextInt();
               LocalMessage var9 = null;
               if (var4.humanShop.getUniqueID() != var6) {
                  var9 = new LocalMessage("ui", "settlerrecruitnotfound");
               } else {
                  SettlementCache var10 = var2.levelCache.getSettlement(var7, var8);
                  if (var10 != null && var10.hasAccess(var3) && var4.humanShop.isValidRecruitment(var10, var3)) {
                     recruitSettler(var2, var3, var4, () -> {
                        return var2.world.getLevel(new LevelIdentifier(var10.islandX, var10.islandY, 0));
                     });
                  } else {
                     var9 = new LocalMessage("ui", "settlementnoperm");
                  }
               }

               if (var9 != null) {
                  var3.sendPacket(recruitResponse(var4.humanShop, var9));
               }
            default:
         }
      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ShopContainer var3 = this.checkContainer(var2.getContainer());
      if (var3 != null) {
         PacketReader var4 = new PacketReader(this.content);
         int var5;
         switch (this.secondType) {
            case SETTLEMENTS_LIST:
               var5 = var4.getNextInt();
               int var12 = var4.getNextShortUnsigned();
               ArrayList var13 = new ArrayList(var12);

               for(int var8 = 0; var8 < var12; ++var8) {
                  int var9 = var4.getNextInt();
                  int var10 = var4.getNextInt();
                  GameMessage var11 = GameMessage.fromContentPacket(var4.getNextContentPacket());
                  var13.add(new SettlementOpenSettlementListEvent.SettlementOption(var9, var10, var11));
               }

               FormComponent var14 = var2.getFocusForm();
               if (var14 instanceof ShopContainerForm) {
                  ((ShopContainerForm)var14).openSettlementList(var5, var13);
               }
               break;
            case RECRUIT_RESPONSE:
               var5 = var4.getNextInt();
               GameMessage var6 = var4.getNextBoolean() ? GameMessage.fromContentPacket(var4.getNextContentPacket()) : null;
               FormComponent var7 = var2.getFocusForm();
               if (var7 instanceof ShopContainerForm) {
                  ((ShopContainerForm)var7).submitRecruitResponse(var5, var6);
               }
         }

      }
   }

   private static enum Type {
      SETTLEMENTS_LIST,
      RECRUIT_SETTLER,
      RECRUIT_RESPONSE;

      private Type() {
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{SETTLEMENTS_LIST, RECRUIT_SETTLER, RECRUIT_RESPONSE};
      }
   }
}
