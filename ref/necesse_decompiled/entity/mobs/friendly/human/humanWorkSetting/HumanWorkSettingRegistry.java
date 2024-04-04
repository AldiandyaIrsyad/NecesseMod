package necesse.entity.mobs.friendly.human.humanWorkSetting;

import java.util.ArrayList;

public class HumanWorkSettingRegistry {
   private boolean registryOpen = true;
   private ArrayList<HumanWorkSetting<?>> actions = new ArrayList();

   public HumanWorkSettingRegistry() {
   }

   public void closeRegistry() {
      this.registryOpen = false;
   }

   public final HumanWorkSetting<?> getSetting(int var1) {
      if (var1 >= 0 && var1 < this.actions.size()) {
         return (HumanWorkSetting)this.actions.get(var1);
      } else {
         System.err.println("Could not find HumanWorkSetting with id " + var1);
         return null;
      }
   }

   public final <C extends HumanWorkSetting<?>> C registerSetting(C var1) {
      if (!this.registryOpen) {
         throw new IllegalStateException("Cannot register HumanWorkSetting after initialization, must be done in constructor.");
      } else if (this.actions.size() >= 32767) {
         throw new IllegalStateException("Cannot register any more HumanWorkSetting");
      } else {
         this.actions.add(var1);
         var1.onRegister(this, this.actions.size() - 1);
         return var1;
      }
   }

   public boolean isEmpty() {
      return this.actions.isEmpty();
   }

   public Iterable<HumanWorkSetting<?>> getSettings() {
      return this.actions;
   }
}
