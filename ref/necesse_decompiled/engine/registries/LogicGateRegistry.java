package necesse.engine.registries;

import java.util.Iterator;
import java.util.stream.Collectors;
import necesse.engine.modLoader.LoadedMod;
import necesse.inventory.item.Item;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.SimpleEntityLogicGate;
import necesse.level.gameLogicGate.entities.AndLogicGateEntity;
import necesse.level.gameLogicGate.entities.BufferLogicGateEntity;
import necesse.level.gameLogicGate.entities.CountdownLogicGateEntity;
import necesse.level.gameLogicGate.entities.CountdownRelayLogicGateEntity;
import necesse.level.gameLogicGate.entities.CounterLogicGateEntity;
import necesse.level.gameLogicGate.entities.DelayLogicGateEntity;
import necesse.level.gameLogicGate.entities.NAndLogicGateEntity;
import necesse.level.gameLogicGate.entities.NOrLogicGateEntity;
import necesse.level.gameLogicGate.entities.OrLogicGateEntity;
import necesse.level.gameLogicGate.entities.SRLatchLogicGateEntity;
import necesse.level.gameLogicGate.entities.SensorLogicGateEntity;
import necesse.level.gameLogicGate.entities.SoundLogicGateEntity;
import necesse.level.gameLogicGate.entities.TFlipFlopLogicGateEntity;
import necesse.level.gameLogicGate.entities.TimerLogicGateEntity;
import necesse.level.gameLogicGate.entities.XOrLogicGateEntity;

public class LogicGateRegistry extends GameRegistry<LogicGateRegistryElement> {
   public static final LogicGateRegistry instance = new LogicGateRegistry();
   private static String[] stringIDs = null;

   private LogicGateRegistry() {
      super("LogicGate", 32762);
   }

   public void registerCore() {
      registerLogicGate("andgate", new SimpleEntityLogicGate(AndLogicGateEntity::new), 2.0F, true);
      registerLogicGate("nandgate", new SimpleEntityLogicGate(NAndLogicGateEntity::new), 2.0F, true);
      registerLogicGate("orgate", new SimpleEntityLogicGate(OrLogicGateEntity::new), 2.0F, true);
      registerLogicGate("norgate", new SimpleEntityLogicGate(NOrLogicGateEntity::new), 2.0F, true);
      registerLogicGate("xorgate", new SimpleEntityLogicGate(XOrLogicGateEntity::new), 2.0F, true);
      registerLogicGate("tflipflopgate", new SimpleEntityLogicGate(TFlipFlopLogicGateEntity::new), 2.0F, true);
      registerLogicGate("srlatchgate", new SimpleEntityLogicGate(SRLatchLogicGateEntity::new), 2.0F, true);
      registerLogicGate("countergate", new SimpleEntityLogicGate(CounterLogicGateEntity::new), 10.0F, true);
      registerLogicGate("timergate", new SimpleEntityLogicGate(TimerLogicGateEntity::new), 10.0F, true);
      registerLogicGate("buffergate", new SimpleEntityLogicGate(BufferLogicGateEntity::new), 10.0F, true);
      registerLogicGate("delaygate", new SimpleEntityLogicGate(DelayLogicGateEntity::new), 10.0F, true);
      registerLogicGate("sensorgate", new SimpleEntityLogicGate(SensorLogicGateEntity::new), 10.0F, true);
      registerLogicGate("soundgate", new SimpleEntityLogicGate(SoundLogicGateEntity::new), 10.0F, true);
      registerLogicGate("countdowngate", new SimpleEntityLogicGate(CountdownLogicGateEntity::new), 10.0F, false);
      registerLogicGate("countdownrelay", new SimpleEntityLogicGate(CountdownRelayLogicGateEntity::new), 2.0F, false);
   }

   protected void onRegister(LogicGateRegistryElement var1, int var2, String var3, boolean var4) {
      Item var5 = var1.gate.generateNewItem();
      if (var5 != null) {
         ItemRegistry.registerItem(var1.gate.getStringID(), var5, var1.itemBrokerValue, var1.itemObtainable);
      }

   }

   protected void onRegistryClose() {
      this.streamElements().map((var0) -> {
         return var0.gate;
      }).forEach(GameLogicGate::updateLocalDisplayName);
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         LogicGateRegistryElement var2 = (LogicGateRegistryElement)var1.next();
         var2.gate.onLogicGateRegistryClosed();
      }

      stringIDs = (String[])instance.streamElements().map((var0) -> {
         return var0.gate.getStringID();
      }).toArray((var0) -> {
         return new String[var0];
      });
   }

   public static int registerLogicGate(String var0, GameLogicGate var1, float var2, boolean var3) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register logic gates");
      } else {
         return instance.register(var0, new LogicGateRegistryElement(var1, var2, var3));
      }
   }

   public static GameLogicGate getLogicGate(int var0) {
      LogicGateRegistryElement var1 = (LogicGateRegistryElement)instance.getElement(var0);
      return var1 == null ? null : var1.gate;
   }

   public static int getLogicGateID(String var0) {
      return instance.getElementID(var0);
   }

   public static String getLogicGateStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   public static Iterable<GameLogicGate> getLogicGates() {
      return (Iterable)instance.streamElements().map((var0) -> {
         return var0.gate;
      }).collect(Collectors.toList());
   }

   public static String[] getGateStringIDs() {
      if (stringIDs == null) {
         throw new IllegalStateException("LogicGateRegistry not yet closed");
      } else {
         return stringIDs;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((LogicGateRegistryElement)var1, var2, var3, var4);
   }

   protected static class LogicGateRegistryElement implements IDDataContainer {
      public final GameLogicGate gate;
      public final float itemBrokerValue;
      public final boolean itemObtainable;

      public LogicGateRegistryElement(GameLogicGate var1, float var2, boolean var3) {
         this.gate = var1;
         this.itemBrokerValue = var2;
         this.itemObtainable = var3;
      }

      public IDData getIDData() {
         return this.gate.idData;
      }
   }
}
