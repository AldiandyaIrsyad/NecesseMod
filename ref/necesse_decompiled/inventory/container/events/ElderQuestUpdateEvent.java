package necesse.inventory.container.events;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.quest.Quest;
import necesse.engine.registries.QuestRegistry;
import necesse.level.maps.levelData.settlementData.SettlementClientQuests;

public class ElderQuestUpdateEvent extends ContainerEvent {
   public final GameMessage questSkipErr;
   public final Quest quest;
   public final GameMessage tierQuestErr;
   public final Quest tierQuest;

   public ElderQuestUpdateEvent(SettlementClientQuests var1) {
      this.quest = var1.getQuest();
      this.questSkipErr = var1.canSkipQuest();
      this.tierQuest = var1.getTierQuest();
      this.tierQuestErr = var1.getTierQuestError();
   }

   public ElderQuestUpdateEvent(PacketReader var1) {
      super(var1);
      if (var1.getNextBoolean()) {
         this.questSkipErr = GameMessage.fromContentPacket(var1.getNextContentPacket());
      } else {
         this.questSkipErr = null;
      }

      int var2;
      if (var1.getNextBoolean()) {
         var2 = var1.getNextShortUnsigned();
         this.quest = QuestRegistry.getNewQuest(var2);
         if (this.quest != null) {
            this.quest.applySpawnPacket(var1);
         }
      } else {
         this.quest = null;
      }

      if (var1.getNextBoolean()) {
         this.tierQuestErr = GameMessage.fromContentPacket(var1.getNextContentPacket());
      } else {
         this.tierQuestErr = null;
      }

      if (var1.getNextBoolean()) {
         var2 = var1.getNextShortUnsigned();
         this.tierQuest = QuestRegistry.getNewQuest(var2);
         if (this.tierQuest != null) {
            this.tierQuest.applySpawnPacket(var1);
         }
      } else {
         this.tierQuest = null;
      }

   }

   public void write(PacketWriter var1) {
      if (this.questSkipErr != null) {
         var1.putNextBoolean(true);
         var1.putNextContentPacket(this.questSkipErr.getContentPacket());
      } else {
         var1.putNextBoolean(false);
      }

      if (this.quest != null) {
         var1.putNextBoolean(true);
         var1.putNextShortUnsigned(this.quest.getID());
         this.quest.setupSpawnPacket(var1);
      } else {
         var1.putNextBoolean(false);
      }

      if (this.tierQuestErr != null) {
         var1.putNextBoolean(true);
         var1.putNextContentPacket(this.tierQuestErr.getContentPacket());
      } else {
         var1.putNextBoolean(false);
      }

      if (this.tierQuest != null) {
         var1.putNextBoolean(true);
         var1.putNextShortUnsigned(this.tierQuest.getID());
         this.tierQuest.setupSpawnPacket(var1);
      } else {
         var1.putNextBoolean(false);
      }

   }
}
