package necesse.inventory.container.settlement.actions;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.levelCache.SettlementCache;
import necesse.entity.levelEvent.SmokePuffLevelEvent;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementMoveErrorEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class MoveSettlerSettlementAction extends ContainerCustomAction {
   public final SettlementContainer container;

   public MoveSettlerSettlementAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1, int var2, int var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextInt(var1);
      var5.putNextInt(var2);
      var5.putNextInt(var3);
      this.runAndSendAction(var4);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextInt();
      int var4 = var1.getNextInt();
      if (this.container.client.isServer()) {
         SettlementLevelData var5 = this.container.getLevelData();
         if (var5 != null) {
            ServerClient var6 = this.container.client.getServerClient();
            Server var7 = var6.getServer();
            LocalMessage var8 = null;
            LevelSettler var9 = var5.getSettler(var2);
            if (var9 == null) {
               var8 = new LocalMessage("settlement", "notsettler");
            } else {
               SettlerMob var10 = var9.getMob();
               if (var10 == null) {
                  var8 = new LocalMessage("settlement", "notsettler");
               } else if (!var9.canMoveOut()) {
                  var8 = new LocalMessage("ui", "settlementnoperm");
               } else {
                  SettlementCache var11 = var7.levelCache.getSettlement(var3, var4);
                  if (var11 != null && SettlementContainer.hasAccess(this.container.getLevelLayer(), var11, var6)) {
                     Level var12 = var7.world.getLevel(new LevelIdentifier(var11.islandX, var11.islandY, 0));
                     SettlementLevelData var13 = SettlementLevelData.getSettlementData(var12);
                     if (var13 == null) {
                        var8 = new LocalMessage("ui", "settlementnotfound");
                     } else {
                        int var14 = var13.countTotalSettlers();
                        int var15 = var7.world.settings.maxSettlersPerSettlement;
                        if (var15 >= 0 && var14 >= var15) {
                           var8 = new LocalMessage("ui", "settlementmaxsettlers", new Object[]{"max", var15});
                        } else {
                           LevelSettler var16 = new LevelSettler(var13, var9.settler, var9.mobUniqueID, var9.settlerSeed);
                           if (!var13.canMoveIn(var16, -1)) {
                              var8 = new LocalMessage("ui", "settlementfull", "settlement", var12.settlementLayer.getSettlementName());
                           } else {
                              Point var17 = Settler.getNewSettlerSpawnPos(var10.getMob(), var12);
                              if (var17 == null) {
                                 var8 = new LocalMessage("ui", "settlementfull", "settlement", var12.settlementLayer.getSettlementName());
                              } else {
                                 var5.removeSettler(var2, (ServerClient)null);
                                 if (var5.getLevel() != var12) {
                                    var5.getLevel().entityManager.addLevelEvent(new SmokePuffLevelEvent(var10.getMob().x, var10.getMob().y, 64, new Color(50, 50, 50)));
                                    var10.getMob().getLevel().entityManager.changeMobLevel(var10.getMob(), var12, var17.x, var17.y, true);
                                 }

                                 var13.moveIn(var16);
                                 var5.sendEvent(SettlementSettlersChangedEvent.class);
                                 var13.sendEvent(SettlementSettlersChangedEvent.class);
                              }
                           }
                        }
                     }
                  } else {
                     var8 = new LocalMessage("ui", "settlementnoperm");
                  }
               }
            }

            if (var8 != null) {
               Packet var18 = new Packet();
               PacketWriter var19 = new PacketWriter(var18);
               var19.putNextContentPacket(var8.getContentPacket());
               (new SettlementMoveErrorEvent(var8)).applyAndSendToClient(var6);
            }
         }
      }

   }
}
