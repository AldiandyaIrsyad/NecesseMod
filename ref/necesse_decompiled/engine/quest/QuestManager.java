package necesse.engine.quest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketQuest;
import necesse.engine.network.packet.PacketQuestRemove;
import necesse.engine.network.server.Server;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;

public class QuestManager {
   private ArrayList<Quest> quests = new ArrayList();
   private Client client;
   private Server server;
   private boolean dirty;

   public QuestManager(Client var1) {
      this.client = var1;
   }

   public QuestManager(Server var1) {
      this.server = var1;
   }

   public void addQuest(Quest var1, boolean var2) {
      if (!this.quests.contains(var1)) {
         this.removeQuest(var1.getUniqueID());
         this.quests.add(var1);
         var1.init(this);
         if (this.server != null) {
            this.server.network.sendPacket(new PacketQuest(var1, var2), (Predicate)((var1x) -> {
               return var1.isActiveFor(var1x.authentication);
            }));
         } else if (this.client != null && var2 && Settings.trackNewQuests) {
            TrackedSidebarForm.addTrackedQuest(this.client, var1);
         }

      }
   }

   public Quest getQuest(int var1) {
      return (Quest)this.quests.stream().filter((var1x) -> {
         return var1x.getUniqueID() == var1;
      }).findFirst().orElse((Object)null);
   }

   public boolean removeQuest(int var1) {
      boolean var2 = false;
      Iterator var3 = this.quests.iterator();

      while(var3.hasNext()) {
         Quest var4 = (Quest)var3.next();
         if (var4.getUniqueID() == var1) {
            var3.remove();
            var4.remove();
            if (this.server != null) {
               this.server.network.sendToAllClients(new PacketQuestRemove(var1));
            } else if (this.client != null) {
               TrackedSidebarForm.removeTrackedQuest(this.client, var4);
            }

            var2 = true;
         }
      }

      return var2;
   }

   public boolean removeQuest(Quest var1) {
      if (!var1.isRemoved()) {
         var1.remove();
      }

      boolean var2 = this.quests.remove(var1);
      if (var2 && this.server != null) {
         this.server.network.sendPacket(new PacketQuestRemove(var1), (Predicate)((var1x) -> {
            return var1.isActiveFor(var1x.authentication);
         }));
      }

      return var2;
   }

   public void removeAll() {
      while(!this.quests.isEmpty()) {
         this.removeQuest((Quest)this.quests.get(0));
      }

   }

   public void clearQuests() {
      this.quests.clear();
   }

   public Iterable<Quest> getQuests() {
      return this.quests;
   }

   public int getTotalQuests() {
      return this.quests.size();
   }

   public void markDirty() {
      this.dirty = true;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public void cleanAll() {
      this.quests.forEach(Quest::clean);
      this.dirty = false;
   }
}
