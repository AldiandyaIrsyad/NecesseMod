package necesse.entity.mobs.job;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.ui.ButtonIcon;

public class JobPriority {
   public static TreeSet<JobPriority> priorities = new TreeSet(Comparator.comparingInt((var0) -> {
      return -var0.priority;
   }));
   public final int priority;
   public GameMessage displayName;
   public String iconString;
   public Supplier<ButtonIcon> icon;

   public static JobPriority getJobPriority(int var0) {
      Iterator var1 = priorities.iterator();

      JobPriority var2;
      do {
         if (!var1.hasNext()) {
            return (JobPriority)priorities.last();
         }

         var2 = (JobPriority)var1.next();
      } while(var0 < var2.priority);

      return var2;
   }

   public JobPriority(int var1, GameMessage var2, String var3, Supplier<ButtonIcon> var4) {
      this.priority = var1;
      this.displayName = var2;
      this.iconString = var3;
      this.icon = var4;
   }

   public GameMessage getFullDisplayName() {
      return (new GameMessageBuilder()).append(this.iconString != null && !this.iconString.isEmpty() ? this.iconString + " " : "").append(this.displayName);
   }

   static {
      priorities.add(new JobPriority(300, new LocalMessage("ui", "prioritytop"), "+++", () -> {
         return Settings.UI.priority_top;
      }));
      priorities.add(new JobPriority(200, new LocalMessage("ui", "priorityhigher"), "++", () -> {
         return Settings.UI.priority_higher;
      }));
      priorities.add(new JobPriority(100, new LocalMessage("ui", "priorityhigh"), "+", () -> {
         return Settings.UI.priority_high;
      }));
      priorities.add(new JobPriority(0, new LocalMessage("ui", "prioritynormal"), "", () -> {
         return Settings.UI.priority_normal;
      }));
      priorities.add(new JobPriority(-100, new LocalMessage("ui", "prioritylow"), "-", () -> {
         return Settings.UI.priority_low;
      }));
      priorities.add(new JobPriority(-200, new LocalMessage("ui", "prioritylower"), "--", () -> {
         return Settings.UI.priority_lower;
      }));
      priorities.add(new JobPriority(-300, new LocalMessage("ui", "prioritylast"), "---", () -> {
         return Settings.UI.priority_last;
      }));
   }
}
