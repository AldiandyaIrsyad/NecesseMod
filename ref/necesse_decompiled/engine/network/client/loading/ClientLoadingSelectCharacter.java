package necesse.engine.network.client.loading;

import java.io.File;
import necesse.engine.Screen;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.network.packet.PacketDownloadCharacter;
import necesse.engine.network.packet.PacketDownloadCharacterResponse;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.packet.PacketSelectedCharacter;
import necesse.engine.save.CharacterSave;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.HumanLook;
import necesse.gfx.forms.FormResizeWrapper;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.presets.CharacterSelectForm;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.NewCharacterForm;

public class ClientLoadingSelectCharacter extends ClientLoadingPhase {
   private FormSwitcher switcher;
   private ConfirmationForm lookErrorForm;
   private NewCharacterForm createCharacterForm;
   private CharacterSelectForm characterSelectForm;
   private boolean needAppearance;
   private boolean allowCharacterSelect;
   private int serverCharacterUniqueID;
   private PlayerMob serverCharacterPlayer;

   public ClientLoadingSelectCharacter(ClientLoading var1) {
      super(var1, false);
   }

   public void submitPlayerAppearancePacket(PacketPlayerAppearance var1) {
      if (var1.slot == this.client.getSlot()) {
         this.markDone();
      }

   }

   public void submitConnectAccepted(PacketConnectApproved var1) {
      this.needAppearance = var1.needAppearance;
      this.allowCharacterSelect = var1.characterSelect;
      this.serverCharacterUniqueID = var1.serverCharacterUniqueID;
      if (var1.serverCharacterAppearance != null) {
         this.serverCharacterPlayer = new PlayerMob((long)this.serverCharacterUniqueID, (NetworkClient)null);
         this.serverCharacterPlayer.look = var1.serverCharacterAppearance;
         this.serverCharacterPlayer.getInv().applyLookContentPacket(new PacketReader(var1.serverCharacterLookContent));
         this.serverCharacterPlayer.playerName = var1.serverCharacterName;
      }

      if (!this.needAppearance && !this.allowCharacterSelect) {
         this.markDone();
      }

   }

   public void submitDownloadedCharacter(PacketDownloadCharacterResponse var1) {
      if (this.switcher != null && !this.switcher.isDisposed()) {
         CharacterSave var2 = new CharacterSave(var1.characterUniqueID, var1.networkData);
         if (this.client.playingOnDisplayName != null) {
            var2.lastUsed = new LocalMessage("ui", "characterlastworld", "world", this.client.playingOnDisplayName);
         }

         File var3 = CharacterSave.saveCharacter(var2, (File)null, false);
         this.characterSelectForm.onCharacterLoaded((FormFlow)null, var3, var2);
      }

   }

   public void submitError(HumanLook var1, GameMessage var2) {
      if (this.switcher != null && !this.switcher.isDisposed()) {
         if (var1 != null) {
            this.createCharacterForm.setLook(var1);
         }

         if (var2 != null) {
            this.client.characterFilePath = null;
            this.lookErrorForm.setupConfirmation((GameMessage)var2, new LocalMessage("ui", "continuebutton"), new LocalMessage("ui", "connectcancel"), () -> {
               this.switcher.makeCurrent((FormComponent)(this.allowCharacterSelect ? this.characterSelectForm : this.createCharacterForm));
            }, this::cancelConnection);
            this.switcher.makeCurrent(this.lookErrorForm);
            this.lookErrorForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
         }
      }

   }

   public FormResizeWrapper start() {
      if (!this.needAppearance && !this.allowCharacterSelect) {
         this.markDone();
         return null;
      } else {
         this.switcher = new FormSwitcher();
         this.characterSelectForm = (CharacterSelectForm)this.switcher.addComponent(new CharacterSelectForm(new LocalMessage("ui", "connectcancel"), this.client.worldSettings.allowCheats, this.client.getPermissionLevel().getLevel() >= PermissionLevel.OWNER.getLevel()) {
            public void onSelected(File var1, CharacterSave var2) {
               if (!ClientLoadingSelectCharacter.this.client.hasDisconnected()) {
                  ClientLoadingSelectCharacter.this.client.characterFilePath = var1;
                  ClientLoadingSelectCharacter.this.client.network.sendPacket(new PacketSelectedCharacter(var2.characterUniqueID, var1 == null ? null : var2));
               }
            }

            public void onBackPressed() {
               ClientLoadingSelectCharacter.this.cancelConnection();
            }

            public void onDownloadPressed(int var1) {
               if (!ClientLoadingSelectCharacter.this.client.hasDisconnected()) {
                  ClientLoadingSelectCharacter.this.client.network.sendPacket(new PacketDownloadCharacter(var1));
               }
            }
         });
         this.createCharacterForm = (NewCharacterForm)this.switcher.addComponent(new NewCharacterForm("newCharacter") {
            public void onCreatePressed(PlayerMob var1) {
               if (!ClientLoadingSelectCharacter.this.client.hasDisconnected()) {
                  ClientLoadingSelectCharacter.this.client.network.sendPacket(new PacketPlayerAppearance(ClientLoadingSelectCharacter.this.client.getSlot(), CharacterSave.getNewUniqueCharacterID((var1x) -> {
                     return !ClientLoadingSelectCharacter.this.characterSelectForm.isCharacterUniqueIDOccupied(var1x);
                  }), var1));
               }
            }

            public void onCancelPressed() {
               ClientLoadingSelectCharacter.this.cancelConnection();
            }
         });
         if (this.serverCharacterPlayer != null) {
            this.createCharacterForm.setLook(this.serverCharacterPlayer.look);
         }

         this.lookErrorForm = (ConfirmationForm)this.switcher.addComponent(new ConfirmationForm("lookError", 400, 120));
         if (this.allowCharacterSelect) {
            if (this.serverCharacterPlayer != null) {
               this.characterSelectForm.addExtraCharacter(this.serverCharacterUniqueID, this.serverCharacterPlayer);
            }

            this.characterSelectForm.loadCharacters();
            this.switcher.makeCurrent(this.characterSelectForm);
         } else {
            this.switcher.makeCurrent(this.createCharacterForm);
         }

         return new FormResizeWrapper(this.switcher, () -> {
            this.characterSelectForm.onWindowResized();
            this.createCharacterForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
            this.lookErrorForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
         });
      }
   }

   public GameMessage getLoadingMessage() {
      return new StaticMessage("CREATING_CHARACTER");
   }

   public void tick() {
   }

   public void end() {
   }
}
