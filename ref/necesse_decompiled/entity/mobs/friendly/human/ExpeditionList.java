package necesse.entity.mobs.friendly.human;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.inventory.container.mob.ContainerExpedition;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ExpeditionList {
   public GameMessage selectDialogue;
   public GameMessage selectMessage;
   public GameMessage focusMessage;
   public GameMessage moreOptionsDialogue;
   public List<ContainerExpedition> expeditions;

   public ExpeditionList(GameMessage var1, GameMessage var2, GameMessage var3, GameMessage var4, List<ContainerExpedition> var5) {
      this.selectDialogue = var1;
      this.selectMessage = var2;
      this.focusMessage = var3;
      this.moreOptionsDialogue = var4;
      this.expeditions = var5;
   }

   public ExpeditionList(GameMessage var1, GameMessage var2, GameMessage var3, GameMessage var4, SettlementLevelData var5, HumanShop var6, List<SettlerExpedition> var7) {
      this(var1, var2, var3, var4, convertToContainerExpeditions(var5, var6, var7));
   }

   private static List<ContainerExpedition> convertToContainerExpeditions(SettlementLevelData var0, HumanShop var1, List<SettlerExpedition> var2) {
      ArrayList var3 = new ArrayList(var2.size());
      long var4 = var1.getShopSeed();
      int var6 = 0;

      for(Iterator var7 = var2.iterator(); var7.hasNext(); ++var6) {
         SettlerExpedition var8 = (SettlerExpedition)var7.next();
         boolean var9 = var8.isAvailable(var0, var1);
         if (var9) {
            float var10 = var8.getSuccessChance(var0, var1);
            int var11 = var8.getBaseCost(var0, var1);
            int var12 = 0;
            if (var11 >= 0) {
               var12 = Math.max((int)((new GameRandom(var4)).nextSeeded(var6).getFloatBetween(0.85F, 1.15F) * (float)var11), 0);
            }

            var3.add(new ContainerExpedition(var8, var9, var10, var12));
         } else {
            var3.add(new ContainerExpedition(var8));
         }
      }

      return var3;
   }

   public ExpeditionList(PacketReader var1) {
      this.selectDialogue = GameMessage.fromPacket(var1);
      this.selectMessage = GameMessage.fromPacket(var1);
      this.focusMessage = GameMessage.fromPacket(var1);
      this.moreOptionsDialogue = GameMessage.fromPacket(var1);
      int var2 = var1.getNextShortUnsigned();
      this.expeditions = new ArrayList(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.expeditions.add(new ContainerExpedition(var1));
      }

   }

   public void writePacket(PacketWriter var1) {
      this.selectDialogue.writePacket(var1);
      this.selectMessage.writePacket(var1);
      this.focusMessage.writePacket(var1);
      this.moreOptionsDialogue.writePacket(var1);
      var1.putNextShortUnsigned(this.expeditions.size());
      Iterator var2 = this.expeditions.iterator();

      while(var2.hasNext()) {
         ContainerExpedition var3 = (ContainerExpedition)var2.next();
         var3.writePacket(var1);
      }

   }
}
