package necesse.engine.network.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketAdventurePartyAdd;
import necesse.engine.network.packet.PacketAdventurePartyBuffPolicy;
import necesse.engine.network.packet.PacketAdventurePartyRemove;
import necesse.engine.network.packet.PacketAdventurePartySync;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.events.AdventurePartyChangedEvent;
import necesse.level.maps.Level;

public class AdventureParty {
   protected ServerClient serverClient;
   protected Client client;
   protected int validTicker;
   protected int syncTicker;
   protected BuffPotionPolicy buffPotionPolicy;
   protected HashMap<Integer, HumanMob> mobs;

   public AdventureParty(ServerClient var1) {
      this.buffPotionPolicy = AdventureParty.BuffPotionPolicy.IN_COMBAT;
      this.mobs = new HashMap();
      this.serverClient = var1;
   }

   public AdventureParty(Client var1) {
      this.buffPotionPolicy = AdventureParty.BuffPotionPolicy.IN_COMBAT;
      this.mobs = new HashMap();
      this.client = var1;
   }

   public void addSaveData(SaveData var1) {
      var1.addEnum("buffPotionPolicy", this.buffPotionPolicy);
   }

   public void applyLoadData(LoadData var1) {
      this.buffPotionPolicy = (BuffPotionPolicy)var1.getEnum(BuffPotionPolicy.class, "buffPotionPolicy", this.buffPotionPolicy, false);
   }

   public void writeUpdatePacket(PacketWriter var1) {
      var1.putNextEnum(this.buffPotionPolicy);
      synchronized(this) {
         var1.putNextShortUnsigned(this.mobs.size());
         Iterator var3 = this.mobs.keySet().iterator();

         while(var3.hasNext()) {
            int var4 = (Integer)var3.next();
            var1.putNextInt(var4);
         }

      }
   }

   public void readUpdatePacket(PacketReader var1) {
      this.buffPotionPolicy = (BuffPotionPolicy)var1.getNextEnum(BuffPotionPolicy.class);
      synchronized(this) {
         this.mobs.clear();
         int var3 = var1.getNextShortUnsigned();

         for(int var4 = 0; var4 < var3; ++var4) {
            this.mobs.put(var1.getNextInt(), (Object)null);
         }

      }
   }

   public void serverTick() {
      ++this.validTicker;
      if (this.validTicker >= 100) {
         LinkedList var1 = new LinkedList();
         synchronized(this) {
            Iterator var3 = this.mobs.entrySet().iterator();

            while(true) {
               if (!var3.hasNext()) {
                  HashMap var10001 = this.mobs;
                  Objects.requireNonNull(var10001);
                  var1.forEach(var10001::remove);
                  break;
               }

               Map.Entry var4 = (Map.Entry)var3.next();
               HumanMob var5 = (HumanMob)var4.getValue();
               if (var5 == null || var5.removed() || var5.adventureParty.getServerClient() != this.serverClient) {
                  var1.add((Integer)var4.getKey());
               }
            }
         }

         this.validTicker = 0;
      }

      ++this.syncTicker;
      if (this.syncTicker >= 600) {
         this.serverClient.sendPacket(new PacketAdventurePartySync(this.serverClient));
         this.syncTicker = 0;
      }

   }

   public void clientTick() {
      ++this.validTicker;
      if (this.validTicker >= 100) {
         this.updateMobsFromLevel(this.client.getLevel());
         this.validTicker = 0;
      }

   }

   public void updateMobsFromLevel(Level var1) {
      synchronized(this) {
         Iterator var3 = this.mobs.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            Mob var5 = var1 == null ? null : (Mob)var1.entityManager.mobs.get((Integer)var4.getKey(), true);
            if (var5 instanceof HumanMob) {
               var4.setValue((HumanMob)var5);
            } else {
               var4.setValue((Object)null);
            }
         }

      }
   }

   public void serverAdd(HumanMob var1) {
      synchronized(this) {
         this.mobs.put(var1.getUniqueID(), var1);
      }

      if (this.serverClient.checkHasRequestedSelf()) {
         this.serverClient.sendPacket(new PacketAdventurePartyAdd(this, var1.getUniqueID()));
         (new AdventurePartyChangedEvent()).applyAndSendToClient(this.serverClient);
      }

   }

   public boolean serverRemove(HumanMob var1, boolean var2, boolean var3) {
      boolean var4;
      synchronized(this) {
         var4 = this.mobs.remove(var1.getUniqueID()) != null;
      }

      if (this.serverClient.checkHasRequestedSelf()) {
         this.serverClient.sendPacket(new PacketAdventurePartyRemove(this, var1.getUniqueID()));
         (new AdventurePartyChangedEvent()).applyAndSendToClient(this.serverClient);
      }

      return var4;
   }

   public void clientRemove(int var1) {
      synchronized(this) {
         this.mobs.remove(var1);
      }
   }

   public void clientAdd(int var1) {
      Level var2 = this.client.getLevel();
      Mob var3 = var2 == null ? null : (Mob)var2.entityManager.mobs.get(var1, true);
      synchronized(this) {
         if (var3 instanceof HumanMob) {
            this.mobs.put(var1, (HumanMob)var3);
         } else {
            this.mobs.put(var1, (Object)null);
         }

      }
   }

   public void setBuffPotionPolicy(BuffPotionPolicy var1, boolean var2) {
      this.buffPotionPolicy = var1;
      if (var2) {
         if (this.serverClient != null && this.serverClient.checkHasRequestedSelf()) {
            this.serverClient.sendPacket(new PacketAdventurePartyBuffPolicy(var1));
            (new AdventurePartyChangedEvent()).applyAndSendToClient(this.serverClient);
         } else if (this.client != null) {
            this.client.network.sendPacket(new PacketAdventurePartyBuffPolicy(var1));
         }
      }

   }

   public BuffPotionPolicy getBuffPotionPolicy() {
      return this.buffPotionPolicy;
   }

   public Set<Integer> getMobUniqueIDs() {
      return this.mobs.keySet();
   }

   public HumanMob getMemberMob(int var1) {
      synchronized(this) {
         return (HumanMob)this.mobs.get(var1);
      }
   }

   public Collection<HumanMob> getMobs() {
      return this.mobs.values();
   }

   public boolean contains(int var1) {
      synchronized(this) {
         return this.mobs.containsKey(var1);
      }
   }

   public boolean contains(HumanMob var1) {
      return this.contains(var1.getUniqueID());
   }

   public int getSize() {
      return this.mobs.size();
   }

   public boolean isEmpty() {
      return this.mobs.isEmpty();
   }

   public int getMobsHash() {
      synchronized(this) {
         return this.mobs.keySet().hashCode();
      }
   }

   public String getDebugString() {
      if (!this.mobs.isEmpty()) {
         synchronized(this) {
            String var2 = Arrays.toString(this.mobs.entrySet().stream().map((var0) -> {
               return var0.getKey() + (var0.getValue() == null ? "?" : "");
            }).toArray());
            return this.mobs.size() + ", " + var2;
         }
      } else {
         return null;
      }
   }

   public static enum BuffPotionPolicy {
      ALWAYS(new LocalMessage("ui", "buffpotionpolicyalways")),
      IN_COMBAT(new LocalMessage("ui", "buffpotionpolicyincombat")),
      SAME_AS_ME(new LocalMessage("ui", "buffpotionpolicysameasme")),
      ON_HOTKEY(new LocalMessage("ui", "buffpotionpolicyonhotkey")),
      NEVER(new LocalMessage("ui", "buffpotionpolicynever"));

      public final GameMessage displayName;

      private BuffPotionPolicy(GameMessage var3) {
         this.displayName = var3;
      }

      // $FF: synthetic method
      private static BuffPotionPolicy[] $values() {
         return new BuffPotionPolicy[]{ALWAYS, IN_COMBAT, SAME_AS_ME, ON_HOTKEY, NEVER};
      }
   }
}
