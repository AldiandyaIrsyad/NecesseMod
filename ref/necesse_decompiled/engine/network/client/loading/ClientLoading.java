package necesse.engine.network.client.loading;

import java.util.ArrayList;
import java.util.UUID;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketClientStats;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.state.MainGame;
import necesse.engine.state.MainMenu;
import necesse.engine.tickManager.PerformanceTimerUtils;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.FormResizeWrapper;
import necesse.gfx.forms.MainMenuFormManager;

public class ClientLoading {
   public final Client client;
   private ArrayList<ClientLoadingPhase> phases = new ArrayList();
   private int currentPhase;
   private boolean currentPhaseStarted;
   private boolean firstLoad = true;
   private long loadStartTime;
   private FormResizeWrapper unusedFormWrapper;
   public final ClientLoadingConnecting connectingPhase;
   public final ClientLoadingWorld worldPhase;
   public final ClientLoadingPlayerStats statsPhase;
   public final ClientLoadingLevelData levelDataPhase;
   public final ClientLoadingSelectCharacter createCharPhase;
   public final ClientLoadingPlayers playersPhase;
   public final ClientLoadingClient clientPhase;
   public final ClientLoadingQuests questsPhase;
   public final ClientLoadingLevelPreload levelPreloadPhase;
   public final ClientObjectEntityLoading objectEntities;

   public ClientLoading(Client var1) {
      this.client = var1;
      this.phases.add(this.connectingPhase = new ClientLoadingConnecting(this));
      this.phases.add(this.worldPhase = new ClientLoadingWorld(this));
      this.phases.add(this.statsPhase = new ClientLoadingPlayerStats(this));
      this.phases.add(this.levelDataPhase = new ClientLoadingLevelData(this));
      this.phases.add(this.createCharPhase = new ClientLoadingSelectCharacter(this));
      this.phases.add(this.playersPhase = new ClientLoadingPlayers(this));
      this.phases.add(this.clientPhase = new ClientLoadingClient(this));
      this.phases.add(this.questsPhase = new ClientLoadingQuests(this));
      this.phases.add(this.levelPreloadPhase = new ClientLoadingLevelPreload(this));
      this.currentPhase = 0;
      this.objectEntities = new ClientObjectEntityLoading(this);
   }

   public void init() {
      this.startCurrentPhase();
   }

   private void startCurrentPhase() {
      ClientLoadingPhase var1 = (ClientLoadingPhase)this.phases.get(this.currentPhase);
      this.currentPhaseStarted = false;
      if (!var1.isDone()) {
         FormResizeWrapper var2 = var1.start();
         this.currentPhaseStarted = true;
         if (!var1.isDone()) {
            FormManager var3 = GlobalData.getCurrentState().getFormManager();
            if (var3 instanceof MainMenuFormManager) {
               ((MainMenuFormManager)var3).setConnectingComponent(var2);
            } else {
               this.unusedFormWrapper = var2;
            }

         }
      }
   }

   public FormResizeWrapper getUnusedFormWrapper() {
      FormResizeWrapper var1 = this.unusedFormWrapper;
      this.unusedFormWrapper = null;
      return var1;
   }

   public GameMessage getLoadingMessage() {
      return this.isDone() ? null : ((ClientLoadingPhase)this.phases.get(this.currentPhase)).getLoadingMessage();
   }

   public void tick() {
      if (this.isDone()) {
         if (this.client.levelManager.loading().isLoadingDone()) {
            if (this.client.getLevel().debugLoadingPerformance != null) {
               PerformanceTimerUtils.printPerformanceTimer(this.client.getLevel().debugLoadingPerformance.getCurrentRootPerformanceTimer());
            }

            this.client.getLevel().debugLoadingPerformance = null;
         }

         this.objectEntities.tick();
      } else {
         while(true) {
            if (this.currentPhaseStarted) {
               ((ClientLoadingPhase)this.phases.get(this.currentPhase)).tick();
            }

            if (this.nextPhase()) {
               if (!this.isDone()) {
                  continue;
               }

               this.finishUp();
            }

            if (!this.isDone() && this.loadStartTime != 0L) {
               long var1 = System.currentTimeMillis() - this.loadStartTime;
               if (var1 >= 10000L) {
                  if (GlobalData.getCurrentState() instanceof MainGame) {
                     GlobalData.setCurrentState(new MainMenu(this.client));
                  }

                  this.loadStartTime = 0L;
               }
            }
            break;
         }
      }

   }

   private boolean nextPhase() {
      if (this.isDone()) {
         return false;
      } else {
         boolean var1 = false;

         while(((ClientLoadingPhase)this.phases.get(this.currentPhase)).isDone()) {
            var1 = true;
            if (this.currentPhaseStarted) {
               ((ClientLoadingPhase)this.phases.get(this.currentPhase)).end();
            }

            ++this.currentPhase;
            if (this.isDone()) {
               break;
            }

            this.startCurrentPhase();
         }

         return var1;
      }
   }

   private void finishUp() {
      this.client.resetPositionPointUpdate();
      this.client.tutorial.start();
      if (Settings.alwaysSkipTutorial) {
         this.client.tutorial.endTutorial();
      }

      this.client.updateSteamRichPresence();
      if (this.firstLoad) {
         this.client.network.sendPacket(new PacketClientStats(GlobalData.stats(), GlobalData.achievements()));
      }

      this.firstLoad = false;
   }

   public void reset() {
      this.loadStartTime = System.currentTimeMillis();
      this.currentPhase = 0;
      this.phases.stream().filter((var0) -> {
         return var0.resetOnLevelChange;
      }).forEach(ClientLoadingPhase::reset);
      this.startCurrentPhase();
      this.objectEntities.reset();
   }

   public void submitApprovedPacket(PacketConnectApproved var1) {
      this.connectingPhase.submitApprovedPacket(var1);
      this.createCharPhase.submitConnectAccepted(var1);
   }

   public boolean isDone() {
      return this.currentPhase >= this.phases.size();
   }

   public boolean hasLocalServer() {
      return this.client.getLocalServer() != null;
   }

   public String getClientCachePath(long var1, String var3) {
      byte[] var4 = (var1 + var3).getBytes();
      return "/client/" + UUID.nameUUIDFromBytes(var4);
   }

   public String getClientCachePath(String var1) {
      return this.getClientCachePath(this.client.getWorldUniqueID(), var1);
   }
}
