package necesse.gfx.forms.presets.debug;

import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketMobHealth;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.HumanLook;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.componentPresets.FormNewPlayerPreset;
import necesse.gfx.gameFont.FontOptions;

public class DebugPlayerForm extends Form {
   public FormNewPlayerPreset newPlayer;
   public FormSlider healthSlider;
   public final DebugForm parent;

   public DebugPlayerForm(String var1, DebugForm var2) {
      super((String)var1, 400, 10);
      this.parent = var2;
      Client var3 = var2.client;
      PlayerMob var4 = var3.getPlayer();
      FormFlow var5 = new FormFlow(10);
      this.addComponent(new FormLabel("Player", new FontOptions(20), 0, this.getWidth() / 2, var5.next(25)));
      this.newPlayer = (FormNewPlayerPreset)this.addComponent((FormNewPlayerPreset)var5.nextY(new FormNewPlayerPreset(0, 35, this.getWidth()), 10));
      this.healthSlider = (FormSlider)this.addComponent((FormSlider)var5.nextY(new FormSlider("Health", 8, 305, var4.getHealth(), 0, var4.getMaxHealth(), this.getWidth() - 16, new FontOptions(12)), 5));
      this.healthSlider.onGrab((var1x) -> {
         if (!var1x.grabbed) {
            int var2 = ((FormSlider)var1x.from).getValue();
            ClientClient var3x = var3.getClient();
            var3x.playerMob.setHealthHidden(var2, 0.0F, 0.0F, (Attacker)null, true);
            var3.network.sendPacket(new PacketMobHealth(var3x.playerMob, true));
         }

      });
      this.healthSlider.drawValueInPercent = false;
      int var6 = var5.next(40);
      ((FormTextButton)this.addComponent(new FormTextButton("Save", 4, var6, this.getWidth() / 2 - 6))).onClicked((var2x) -> {
         PlayerMob var3x = this.newPlayer.getNewPlayer();
         ClientClient var4 = var3.getClient();
         var3x.playerName = var4.getName();
         var4.playerMob.look = new HumanLook(var3x.look);
         var3.network.sendPacket(new PacketPlayerAppearance(var3.getSlot(), var3.getCharacterUniqueID(), var3x));
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Back", this.getWidth() / 2 + 2, var6, this.getWidth() / 2 - 6))).onClicked((var1x) -> {
         var2.makeCurrent(var2.mainMenu);
      });
      this.setHeight(var5.next());
   }

   public void refreshPlayer() {
      PlayerMob var1 = this.parent.client.getPlayer();
      this.newPlayer.setLook(new HumanLook(var1.look));
      this.healthSlider.setRange(0, this.parent.client.getPlayer().getMaxHealth());
      this.healthSlider.setValue(this.parent.client.getPlayer().getHealth());
   }
}
