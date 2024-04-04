package necesse.inventory.item.questItem;

import java.util.ArrayList;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;

public class EyePatchQuestItem extends QuestItem {
   public static final ArrayList<String> droppedByMobs = new ArrayList();

   public EyePatchQuestItem() {
      super(new LocalMessage("itemtooltip", "eyepatchobtain"));
   }

   public LootTable getExtraMobDrops(ServerClient var1, Mob var2) {
      return droppedByMobs.contains(var2.getStringID()) && var1.playerMob.getInv().getAmount(this, false, true, true, "questdrop") <= 0 ? new LootTable(new LootItemInterface[]{new ChanceLootItem(0.75F, this.getStringID())}) : super.getExtraMobDrops(var1, var2);
   }

   static {
      droppedByMobs.add("piratecaptain");
      droppedByMobs.add("piraterecruit");
   }
}
