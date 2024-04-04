package necesse.engine.registries;

import necesse.engine.quest.DeliverItemsSettlementQuest;
import necesse.engine.quest.HaveKilledMobsSettlementQuest;
import necesse.engine.quest.KillMobsSettlementQuest;
import necesse.engine.quest.KillMobsTitleQuest;
import necesse.engine.quest.Quest;

public class QuestRegistry extends EmptyConstructorGameRegistry<Quest> {
   public static final QuestRegistry instance = new QuestRegistry();

   private QuestRegistry() {
      super("Quest", 32762);
   }

   public void registerCore() {
      registerQuest("killmobstitle", KillMobsTitleQuest.class);
      registerQuest("killmobssettlement", KillMobsSettlementQuest.class);
      registerQuest("deliveritemssettlement", DeliverItemsSettlementQuest.class);
      registerQuest("havekilledmobssettlement", HaveKilledMobsSettlementQuest.class);
   }

   protected void onRegistryClose() {
   }

   public static void registerQuest(String var0, Class<? extends Quest> var1) {
      instance.registerClass(var0, var1);
   }

   public static Quest getNewQuest(int var0) {
      return (Quest)instance.getNewInstance(var0);
   }

   public static int getQuestID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getQuestID(Class<? extends Quest> var0) {
      return instance.getElementID(var0);
   }

   public static Quest getNewQuest(String var0) {
      return (Quest)instance.getNewInstance(var0);
   }
}
