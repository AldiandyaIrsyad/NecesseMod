package necesse.gfx.forms.presets.containerComponent;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.BedContainer;
import necesse.inventory.container.events.SpawnUpdateContainerEvent;

public class SleepContainerForm extends ContainerFormSwitcher<BedContainer> {
   public final FormComponentList components = (FormComponentList)this.addComponent(new FormComponentList());
   public final FormLabel label;
   public final FormLabel clockLabel;
   public final FormLabel timeLabel;
   public final Form wakeUpButtonForm;
   public final Form spawnButtonForm;
   public final FormLocalTextButton spawnButton;
   private final TreeSet<SleepingCharacter> chars = new TreeSet(Comparator.comparingLong((var0) -> {
      return var0.startTime - (long)var0.lifeTime;
   }));

   public SleepContainerForm(Client var1, BedContainer var2) {
      super(var1, var2);
      this.makeCurrent(this.components);
      this.wakeUpButtonForm = (Form)this.components.addComponent(new Form(200, 32));
      FormLocalTextButton var3 = (FormLocalTextButton)this.wakeUpButtonForm.addComponent(new FormLocalTextButton("ui", "wakeupbutton", 0, 0, this.wakeUpButtonForm.getWidth(), FormInputSize.SIZE_32, ButtonColor.BASE));
      var3.onClicked((var1x) -> {
         var1.closeContainer(true);
      });
      this.spawnButtonForm = (Form)this.components.addComponent(new Form(300, 32));
      this.spawnButton = (FormLocalTextButton)this.spawnButtonForm.addComponent(new FormLocalTextButton("ui", var2.isCurrentSpawn ? "clearspawnbutton" : "setspawnbutton", 0, 0, this.spawnButtonForm.getWidth(), FormInputSize.SIZE_32, ButtonColor.BASE));
      this.spawnButton.onClicked((var2x) -> {
         if (!var2.isCurrentSpawn) {
            var2.setSpawn.runAndSend();
         } else {
            var2.clearSpawn.runAndSend();
         }

         this.spawnButton.startCooldown(250);
      });
      var2.onEvent(SpawnUpdateContainerEvent.class, (var1x) -> {
         this.spawnButton.setLocalization(new LocalMessage("ui", var1x.isCurrentSpawn ? "clearspawnbutton" : "setspawnbutton"));
      });
      this.label = (FormLabel)this.components.addComponent(new FormLabel("", (new FontOptions(20)).color(Color.WHITE).outline(), 0, 0, 0));
      this.clockLabel = (FormLabel)this.components.addComponent(new FormLabel("", (new FontOptions(20)).color(Color.WHITE).outline(), 0, 0, 0));
      this.timeLabel = (FormLabel)this.components.addComponent(new FormLabel("", (new FontOptions(20)).color(Color.WHITE).outline(), 0, 0, 0));
      this.onWindowResized();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if ((long)((BedContainer)this.container).sleepingPlayers < this.client.streamClients().count()) {
         this.label.setText(Localization.translate("ui", "waitingplayers", "count", ((BedContainer)this.container).sleepingPlayers, "total", this.client.streamClients().count()));
      } else {
         if (var1.isGameTick() && GameRandom.globalRandom.getChance(20)) {
            this.chars.add(new SleepingCharacter((float)Screen.getHudWidth() / 2.0F + (float)GameRandom.globalRandom.getIntBetween(-10, 10), (float)Screen.getHudHeight() / 2.0F - 20.0F + (float)GameRandom.globalRandom.getIntBetween(-5, 5), (float)GameRandom.globalRandom.getIntBetween(-10, 10), (float)GameRandom.globalRandom.getIntBetween(-20, -50), (float)(GameRandom.globalRandom.getIntBetween(5, 25) * (Integer)GameRandom.globalRandom.getOneOf((Object[])(1, -1))), GameRandom.globalRandom.getIntBetween(14, 20), GameRandom.globalRandom.getIntBetween(1000, 3000)));
         }

         while(!this.chars.isEmpty() && ((SleepingCharacter)this.chars.first()).shouldRemove()) {
            this.chars.pollFirst();
         }

         this.label.setText("");
      }

      if (var2 != null) {
         this.clockLabel.setText(var2.getLevel().getWorldEntity().getDayTimeReadable());
         this.timeLabel.setText(var2.getLevel().getWorldEntity().getTimeOfDay().displayName);
      }

      Iterator var4 = this.chars.iterator();

      while(var4.hasNext()) {
         SleepingCharacter var5 = (SleepingCharacter)var4.next();
         var5.draw();
      }

      super.draw(var1, var2, var3);
   }

   public boolean shouldOpenInventory() {
      return false;
   }

   public boolean shouldShowInventory() {
      return false;
   }

   public boolean shouldShowToolbar() {
      return false;
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.wakeUpButtonForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2 + 50);
      this.spawnButtonForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2 + 50 + this.wakeUpButtonForm.getHeight() + Settings.UI.formSpacing);
      this.label.setPosition(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2 - 140);
      this.clockLabel.setPosition(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2 - 120);
      this.timeLabel.setPosition(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2 - 100);
   }

   private static class SleepingCharacter {
      private float x;
      private float y;
      private float dx;
      private float dy;
      private float sway;
      private int fontSize;
      private long startTime;
      private int lifeTime;

      public SleepingCharacter(float var1, float var2, float var3, float var4, float var5, int var6, int var7) {
         this.x = var1;
         this.y = var2;
         this.dx = var3;
         this.dy = var4;
         this.sway = var5;
         this.fontSize = var6;
         this.lifeTime = var7;
         this.startTime = System.currentTimeMillis();
      }

      public void draw() {
         if (!this.shouldRemove()) {
            long var1 = System.currentTimeMillis() - this.startTime;
            int var3 = this.fontSize;
            double var4;
            if (var1 < 500L) {
               var4 = (double)var1 / 500.0;
               var3 = (int)(var4 * (double)var3);
            } else if (var1 > (long)(this.lifeTime - 100)) {
               var4 = Math.abs((double)(var1 + 100L - (long)this.lifeTime) / 100.0 - 1.0);
               var3 = (int)(var4 * (double)var3);
            }

            var4 = (double)var1 / (double)this.lifeTime;
            double var6 = (double)this.x + Math.sin((double)var1 / 500.0) * (double)this.sway + (double)this.dx * var4;
            double var8 = (double)this.y + (double)this.dy * var4;
            FontOptions var10 = (new FontOptions(var3)).outline();
            float var11 = FontManager.bit.getWidth('Z', var10);
            float var12 = FontManager.bit.getHeight('Z', var10);
            FontManager.bit.drawChar((float)var6 - var11 / 2.0F, (float)var8 - var12 / 2.0F, 'Z', var10);
         }
      }

      public boolean shouldRemove() {
         return this.startTime + (long)this.lifeTime < System.currentTimeMillis();
      }
   }
}
