package necesse.entity.mobs.friendly.human.humanShop.explorerMission;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.EmptyConstructorGameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.MoveToPoint;
import necesse.gfx.gameTooltips.ListGameTooltips;

public abstract class SettlerMission implements IDDataContainer {
   public final IDData idData = new IDData();
   private boolean over;
   public static final ExplorerMissionRegistry registry = new ExplorerMissionRegistry();

   public IDData getIDData() {
      return this.idData;
   }

   public SettlerMission() {
      registry.applyIDData(this.getClass(), this.idData);
   }

   public abstract boolean canStart(HumanMob var1);

   public abstract void start(HumanMob var1);

   public abstract void addSaveData(HumanMob var1, SaveData var2);

   public abstract void applySaveData(HumanMob var1, LoadData var2);

   public abstract void setupMovementPacket(HumanMob var1, PacketWriter var2);

   public abstract void applyMovementPacket(HumanMob var1, PacketReader var2);

   public abstract MoveToPoint getMoveOutPoint(HumanMob var1);

   public void clientTick(HumanMob var1) {
   }

   public abstract void serverTick(HumanMob var1);

   public boolean isMobVisible(HumanMob var1) {
      return true;
   }

   public boolean isMobIdle(HumanMob var1) {
      return false;
   }

   public void markOver() {
      this.over = true;
   }

   public boolean isOver() {
      return this.over;
   }

   public void addDebugTooltips(ListGameTooltips var1) {
   }

   static {
      registry.registerCore();
   }

   public static class ExplorerMissionRegistry extends EmptyConstructorGameRegistry<SettlerMission> {
      private ExplorerMissionRegistry() {
         super("ExplorerMission", 32767);
      }

      public void registerCore() {
         this.registerMission("findisland", FindIslandMission.class);
         this.registerMission("expedition", ExpeditionMission.class);
      }

      protected void onRegistryClose() {
      }

      public void registerMission(String var1, Class<? extends SettlerMission> var2) {
         try {
            this.register(var1, new ClassIDDataContainer(var2, new Class[0]));
         } catch (NoSuchMethodException var4) {
            throw new IllegalArgumentException(var2.getSimpleName() + " does not have a constructor with no parameters");
         }
      }

      // $FF: synthetic method
      ExplorerMissionRegistry(Object var1) {
         this();
      }
   }
}
