package necesse.engine.network.client;

import java.util.ArrayList;
import java.util.UUID;
import necesse.engine.GameCache;
import necesse.engine.GlobalData;
import necesse.engine.network.client.tutorialPhases.ChopTreePhase;
import necesse.engine.network.client.tutorialPhases.CraftPickaxePhase;
import necesse.engine.network.client.tutorialPhases.CraftTorchPhase;
import necesse.engine.network.client.tutorialPhases.EndTutorialPhase;
import necesse.engine.network.client.tutorialPhases.InteractElderPhase;
import necesse.engine.network.client.tutorialPhases.InteractSettlementFlagPhase;
import necesse.engine.network.client.tutorialPhases.IslandTravelTipPhase;
import necesse.engine.network.client.tutorialPhases.MineOrePhase;
import necesse.engine.network.client.tutorialPhases.OreTipPhase;
import necesse.engine.network.client.tutorialPhases.TutorialPhase;
import necesse.engine.network.client.tutorialPhases.UseLadderPhase;
import necesse.engine.network.client.tutorialPhases.UseWorkstationPhase;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;

public class ClientTutorial {
   private final long worldUniqueID;
   private Client client;
   private boolean isActive;
   private ArrayList<TutorialPhase> phases;
   private int currentPhase;
   private InteractElderPhase interactElderPhase;
   private UseWorkstationPhase useWorkstationPhase;

   public ClientTutorial(Client var1, long var2) {
      this.client = var1;
      this.worldUniqueID = var2;
      this.setupPhases();
      this.isActive = true;
      this.loadProgress();
   }

   private void setupPhases() {
      this.phases = new ArrayList();
      this.phases.add(new ChopTreePhase(this, this.client));
      this.phases.add(new CraftTorchPhase(this, this.client));
      this.phases.add(this.interactElderPhase = new InteractElderPhase(this, this.client));
      this.phases.add(this.useWorkstationPhase = new UseWorkstationPhase(this, this.client));
      this.phases.add(new CraftPickaxePhase(this, this.client));
      this.phases.add(new UseLadderPhase(this, this.client));
      this.phases.add(new MineOrePhase(this, this.client));
      this.phases.add(new OreTipPhase(this, this.client));
      this.phases.add(new IslandTravelTipPhase(this, this.client));
      this.phases.add(new InteractSettlementFlagPhase(this, this.client));
      this.phases.add(new EndTutorialPhase(this, this.client));
   }

   private String getCachePath() {
      byte[] var1 = (this.worldUniqueID + "Tutorial").getBytes();
      return "/client/" + UUID.nameUUIDFromBytes(var1);
   }

   private void saveProgress() {
      SaveData var1 = new SaveData("Tutorial");
      var1.addBoolean("isActive", this.isActive);
      var1.addInt("currentPhase", this.currentPhase);
      GameCache.cacheSave(var1, this.getCachePath());
   }

   private void loadProgress() {
      LoadData var1 = GameCache.getSave(this.getCachePath());
      if (var1 != null) {
         this.isActive = var1.getBoolean("isActive", this.isActive);
         this.currentPhase = var1.getInt("currentPhase", 0);
      }
   }

   public void updateObjective(MainGame var1) {
      if (this.isActive) {
         this.getCurrentPhase().updateObjective(var1);
      }
   }

   public void start() {
      if (this.isActive) {
         this.setPhase(this.currentPhase);
      }
   }

   public void reset() {
      if (this.isActive) {
         this.getCurrentPhase().end();
      }

      this.isActive = true;
      this.setupPhases();
      this.setPhase(0);
      this.saveProgress();
   }

   public void endTutorial() {
      if (this.isActive) {
         this.getCurrentPhase().end();
      }

      this.isActive = false;
      if (GlobalData.getCurrentState() instanceof MainGame) {
         ((MainGame)GlobalData.getCurrentState()).formManager.clearTutorial();
      }

      this.saveProgress();
   }

   public void tick() {
      if (this.isActive) {
         TutorialPhase var1 = this.getCurrentPhase();
         if (!var1.isOver()) {
            this.getCurrentPhase().tick();
         } else if (!this.nextPhase()) {
            this.endTutorial();
         } else {
            this.tick();
            this.saveProgress();
         }

      }
   }

   public void drawOverForms(PlayerMob var1) {
      if (this.isActive) {
         this.getCurrentPhase().drawOverForm(var1);
      }
   }

   private TutorialPhase getCurrentPhase() {
      return (TutorialPhase)this.phases.get(this.currentPhase);
   }

   private boolean setPhase(int var1) {
      if (var1 < 0 && var1 >= this.phases.size()) {
         return false;
      } else {
         this.getCurrentPhase().end();
         this.currentPhase = var1;
         this.getCurrentPhase().start();
         if (GlobalData.getCurrentState() instanceof MainGame) {
            this.getCurrentPhase().updateObjective((MainGame)GlobalData.getCurrentState());
         }

         return true;
      }
   }

   public boolean nextPhase() {
      if (this.currentPhase + 1 < this.phases.size()) {
         this.setPhase(this.currentPhase + 1);
         return true;
      } else {
         return false;
      }
   }

   public boolean previousPhase() {
      if (this.currentPhase - 1 > 0) {
         this.setPhase(this.currentPhase - 1);
         return true;
      } else {
         return false;
      }
   }

   public void elderInteracted() {
      if (this.isActive) {
         if (this.getCurrentPhase() == this.interactElderPhase) {
            this.interactElderPhase.elderInteracted();
         }

      }
   }

   public void usedWorkstation() {
      if (this.isActive) {
         if (this.getCurrentPhase() == this.useWorkstationPhase) {
            this.useWorkstationPhase.usedWorkstation();
         }

      }
   }
}
