package necesse.engine.quest;

import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class KillMobsTitleQuest extends KillMobsQuest {
   protected GameMessage title;

   public KillMobsTitleQuest() {
   }

   public KillMobsTitleQuest(GameMessage var1, KillMobsQuest.KillObjective var2, KillMobsQuest.KillObjective... var3) {
      super(var2, var3);
      this.title = var1;
   }

   public KillMobsTitleQuest(GameMessage var1, String var2, int var3) {
      super(var2, var3);
      this.title = var1;
   }

   public KillMobsTitleQuest(int var1, int var2, GameMessage var3) {
      super(var1, var2);
      this.title = var3;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      if (this.title != null) {
         var1.addSaveData(this.title.getSaveData("title"));
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      LoadData var2 = var1.getFirstLoadDataByName("title");
      if (var2 != null) {
         try {
            this.title = GameMessage.loadSave(var2);
         } catch (Exception var4) {
            GameLog.warn.println("Could not load kill mobs quest title");
            this.title = new LocalMessage("quests", "genericquest");
         }
      } else {
         this.title = new LocalMessage("quests", "genericquest");
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.title != null);
      if (this.title != null) {
         var1.putNextContentPacket(this.title.getContentPacket());
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      if (var1.getNextBoolean()) {
         this.title = GameMessage.fromContentPacket(var1.getNextContentPacket());
      }

   }

   public GameMessage getTitle() {
      return (GameMessage)(this.title == null ? new LocalMessage("quests", "genericquest") : this.title);
   }
}
