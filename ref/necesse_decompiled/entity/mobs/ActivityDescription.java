package necesse.entity.mobs;

import java.util.Comparator;
import java.util.HashMap;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.ability.MobAbility;

public class ActivityDescription {
   public final Mob mob;
   public static int activityTimeoutMS = 500;
   protected HashMap<String, Activity> currentActivities = new HashMap();
   protected GameLinkedList<Activity> priorities = new GameLinkedList();
   protected GameLinkedList<Activity> timeouts = new GameLinkedList();
   protected MobSetActivityAbility setActivityAbility;
   protected GameMessage currentActivity;

   public ActivityDescription(Mob var1) {
      this.mob = var1;
      this.setActivityAbility = (MobSetActivityAbility)var1.registerAbility(new MobSetActivityAbility());
   }

   public void writeSpawnPacket(PacketWriter var1) {
      var1.putNextBoolean(this.currentActivity != null);
      if (this.currentActivity != null) {
         this.currentActivity.writePacket(var1);
      }

   }

   public void readSpawnPacket(PacketReader var1) {
      if (var1.getNextBoolean()) {
         this.currentActivity = GameMessage.fromPacket(var1);
      } else {
         this.currentActivity = null;
      }

   }

   public void serverTick() {
      GameMessage var1 = this.currentActivity;
      this.refreshCurrentActive();
      GameMessage var2 = this.currentActivity;
      if (!GameMessage.isSame(var1, var2) && this.mob.isServer()) {
         this.setActivityAbility.updateActivity(var2);
      }

   }

   public void refreshCurrentActive() {
      while(!this.timeouts.isEmpty() && ((Activity)this.timeouts.getFirst()).shouldTimeout()) {
         ((Activity)this.timeouts.getFirst()).remove();
      }

      Activity var1 = (Activity)this.priorities.getFirst();
      if (var1 != null) {
         this.currentActivity = var1.description;
      } else {
         this.currentActivity = null;
      }

   }

   public void setActivity(String var1, int var2, GameMessage var3) {
      this.currentActivities.compute(var1, (var4, var5) -> {
         if (var5 == null) {
            return new Activity(var1, var2, activityTimeoutMS, var3);
         } else {
            var5.refreshTimeoutTime(activityTimeoutMS);
            var5.setPriority(var2);
            var5.description = var3;
            return var5;
         }
      });
   }

   public void clearActivity(String var1) {
      Activity var2 = (Activity)this.currentActivities.get(var1);
      if (var2 != null) {
         var2.remove();
      }

   }

   public GameMessage getCurrentActivity() {
      return this.currentActivity;
   }

   private class MobSetActivityAbility extends MobAbility {
      public MobSetActivityAbility() {
      }

      public void updateActivity(GameMessage var1) {
         Packet var2 = new Packet();
         PacketWriter var3 = new PacketWriter(var2);
         var3.putNextBoolean(var1 != null);
         if (var1 != null) {
            var1.writePacket(var3);
         }

         this.runAndSendAbility(var2);
      }

      public void executePacket(PacketReader var1) {
         if (var1.getNextBoolean()) {
            ActivityDescription.this.currentActivity = GameMessage.fromPacket(var1);
         } else {
            ActivityDescription.this.currentActivity = null;
         }

      }
   }

   private class Activity {
      public final String type;
      public int priority;
      public long timeoutLocalTime;
      public GameLinkedList<Activity>.Element priorityElement;
      public GameLinkedList<Activity>.Element timeoutElement;
      public GameMessage description;

      public Activity(String var2, int var3, int var4, GameMessage var5) {
         this.type = var2;
         this.description = var5;
         this.setPriority(var3);
         this.refreshTimeoutTime(var4);
      }

      public void setPriority(int var1) {
         if (this.priority != var1 || this.priorityElement == null) {
            if (this.priorityElement != null) {
               this.priorityElement.remove();
            }

            this.priorityElement = GameUtils.insertSortedList((GameLinkedList)ActivityDescription.this.priorities, this, Comparator.comparingInt((var0) -> {
               return -var0.priority;
            }));
         }
      }

      public void refreshTimeoutTime(int var1) {
         long var2 = ActivityDescription.this.mob.getWorldEntity().getLocalTime() + (long)var1;
         if (this.timeoutLocalTime != var2 || this.timeoutElement == null) {
            this.timeoutLocalTime = var2;
            if (this.timeoutElement != null) {
               this.timeoutElement.remove();
            }

            this.timeoutElement = GameUtils.insertSortedListReversed(ActivityDescription.this.timeouts, this, Comparator.comparingLong((var0) -> {
               return var0.timeoutLocalTime;
            }));
         }
      }

      public boolean shouldTimeout() {
         return ActivityDescription.this.mob.getWorldEntity().getLocalTime() >= this.timeoutLocalTime;
      }

      public void remove() {
         if (this.priorityElement != null) {
            this.priorityElement.remove();
            this.priorityElement = null;
         }

         if (this.timeoutElement != null) {
            this.timeoutElement.remove();
            this.timeoutElement = null;
         }

         ActivityDescription.this.currentActivities.remove(this.type);
      }
   }
}
