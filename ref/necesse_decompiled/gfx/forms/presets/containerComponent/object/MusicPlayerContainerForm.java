package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.MusicOptions;
import necesse.engine.MusicOptionsOffset;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalSlider;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.object.MusicPlayerContainer;

public class MusicPlayerContainerForm extends OEInventoryContainerForm<MusicPlayerContainer> {
   protected GameMusic lastPlaying;
   protected FormContentIconButton pauseButton;
   protected FormLocalSlider playingSlider;

   public MusicPlayerContainerForm(Client var1, MusicPlayerContainer var2) {
      super(var1, var2);
   }

   protected void addSlots(FormFlow var1) {
      this.inventoryForm.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "insertplaylist", new FontOptions(16), -1, 5, 0), 4));
      super.addSlots(var1);
      FormContainerSlot[] var2 = this.slots;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         FormContainerSlot var5 = var2[var4];
         var5.setDecal(Settings.UI.inventoryslot_icon_vinyl);
      }

      var1.next(10);
      this.playingSlider = (FormLocalSlider)this.inventoryForm.addComponent((<undefinedtype>)var1.nextY(new FormLocalSlider(new StaticMessage(""), 5, 0, 0, 0, 60, this.inventoryForm.getWidth() - 10) {
         public String getValueText() {
            int var1 = this.getValue();
            int var2 = var1 / 60;
            var1 -= var2 * 60;
            String var3 = var1 < 10 ? "0" + var1 : "" + var1;
            int var4 = this.getMaxValue();
            int var5 = var4 / 60;
            var4 -= var5 * 60;
            String var6 = var4 < 10 ? "0" + var4 : "" + var4;
            return var2 + ":" + var3 + " / " + var5 + ":" + var6;
         }
      }, 5));
      this.playingSlider.allowScroll = false;
      this.playingSlider.onGrab((var1x) -> {
         if (!var1x.grabbed) {
            int var2 = ((FormSlider)var1x.from).getValue() * 1000;
            MusicOptionsOffset var3 = ((MusicPlayerContainer)this.container).objectEntity.getCurrentMusic();
            if (var3 != null) {
               long var4 = var3.offset - (long)var2;
               ((MusicPlayerContainer)this.container).forwardMilliseconds.runAndSend(var4);
            }
         }

      });
      int var6 = var1.next(32);
      this.pauseButton = (FormContentIconButton)this.inventoryForm.addComponent(new FormContentIconButton(this.inventoryForm.getWidth() / 2 - 16, var6, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.world_icon, new GameMessage[0]));
      this.pauseButton.onClicked((var1x) -> {
         ((MusicPlayerContainer)this.container).setPaused.runAndSend(!((MusicPlayerContainer)this.container).objectEntity.isPaused());
      });
      ((FormContentIconButton)this.inventoryForm.addComponent(new FormContentIconButton(this.inventoryForm.getWidth() / 2 - 16 - 4 - 32, var6, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.prev_song, new GameMessage[0]))).onClicked((var1x) -> {
         MusicOptionsOffset var2 = ((MusicPlayerContainer)this.container).objectEntity.getCurrentMusic();
         if (var2 != null) {
            long var3 = var2.offset;
            if (var2.offset < 3000L) {
               MusicOptions var5 = ((MusicPlayerContainer)this.container).objectEntity.getPreviousMusic();
               if (var5 != null) {
                  var3 += var5.music.sound.getLengthInMillis() - (long)var5.getFadeOutTime();
               }
            }

            ((MusicPlayerContainer)this.container).forwardMilliseconds.runAndSend(var3);
         }

      });
      ((FormContentIconButton)this.inventoryForm.addComponent(new FormContentIconButton(this.inventoryForm.getWidth() / 2 + 16 + 4, var6, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.next_song, new GameMessage[0]))).onClicked((var1x) -> {
         MusicOptionsOffset var2 = ((MusicPlayerContainer)this.container).objectEntity.getCurrentMusic();
         if (var2 != null) {
            long var3 = var2.options.music.sound.getLengthInMillis() - (long)var2.options.getFadeOutTime();
            long var5 = var2.offset - var3;
            ((MusicPlayerContainer)this.container).forwardMilliseconds.runAndSend(var5);
         }

      });
   }

   public void updatePlayingComponents() {
      MusicOptionsOffset var1 = ((MusicPlayerContainer)this.container).objectEntity.getCurrentMusic();
      GameMusic var2 = var1 == null ? null : var1.options.music;
      if (this.lastPlaying != var2) {
         if (var2 != null) {
            this.playingSlider.setLocalization(new LocalMessage("ui", "musicplaying", "name", var2.trackName.translate()));
         } else {
            this.playingSlider.setLocalization(new StaticMessage(""));
         }

         this.lastPlaying = var2;
      }

      if (var1 != null) {
         int var3 = (int)((double)var1.options.music.sound.getLengthInMillis() / 1000.0);
         this.playingSlider.setRange(0, var3);
         if (!this.playingSlider.isGrabbed()) {
            int var4 = (int)((double)var1.offset / 1000.0);
            this.playingSlider.setValue(var4);
         }
      } else {
         this.playingSlider.setRange(0, 0);
         this.playingSlider.setValue(0);
      }

      this.pauseButton.setIcon(((MusicPlayerContainer)this.container).objectEntity.isPaused() ? Settings.UI.play_song : Settings.UI.pause_song);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.updatePlayingComponents();
      super.draw(var1, var2, var3);
   }
}
