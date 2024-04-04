package necesse.gfx.forms.presets.containerComponent;

import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketAdventurePartyCompressInventory;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;

public class PartyConfigForm extends Form {
   protected Client client;
   protected Container container;
   protected int slotStartIndex;
   protected int slotEndIndex;
   protected Runnable sizeUpdated;
   public FormLocalLabel partySizeLabel;
   public FormDropdownSelectionButton<AdventureParty.BuffPotionPolicy> buffPolicyDropdown;
   public FormContentBox inventoryContent;
   public FormContainerSlot[] slots;
   public FormLocalTextButton compressInventoryButton;
   public FormLocalTextButton backButton;
   protected int lastInventorySize;
   protected int lastPartySize;

   public PartyConfigForm(Client var1, Container var2, int var3, int var4, int var5, Runnable var6, Runnable var7, Runnable var8) {
      super((String)"partyConfig", var5, 90);
      this.client = var1;
      this.container = var2;
      this.slotStartIndex = var3;
      this.slotEndIndex = var4;
      FormFlow var9 = new FormFlow(5);
      this.addComponent((FormLocalLabel)var9.nextY(new FormLocalLabel("ui", "adventureparty", new FontOptions(20), -1, 4, 0), 5));
      this.lastPartySize = var1.adventureParty.getSize();
      this.partySizeLabel = (FormLocalLabel)this.addComponent((FormLocalLabel)var9.nextY(new FormLocalLabel(new LocalMessage("ui", "adventurepartysize", new Object[]{"size", this.lastPartySize}), new FontOptions(12), -1, 4, 0), 5));
      this.addComponent((FormLocalLabel)var9.nextY(new FormLocalLabel("ui", "adventurepartytip", new FontOptions(12), -1, 4, 0, this.getWidth() - 8), 10));
      int var10;
      if (var6 != null) {
         var10 = Math.min(var5 - 8, 350);
         ((FormLocalTextButton)this.addComponent((FormLocalTextButton)var9.nextY(new FormLocalTextButton("ui", "adventurepartycommand", this.getWidth() / 2 - var10 / 2, 4, var10, FormInputSize.SIZE_24, ButtonColor.BASE), 10))).onClicked((var1x) -> {
            var6.run();
         });
      }

      this.addComponent((FormLocalLabel)var9.nextY(new FormLocalLabel("ui", "buffpotionpolicy", new FontOptions(20), -1, 4, 0), 5));
      var10 = Math.min(var5 - 8, 350);
      this.buffPolicyDropdown = (FormDropdownSelectionButton)this.addComponent((FormDropdownSelectionButton)var9.nextY(new FormDropdownSelectionButton(this.getWidth() / 2 - var10 / 2, 4, FormInputSize.SIZE_24, ButtonColor.BASE, var10), 10));
      AdventureParty.BuffPotionPolicy[] var11 = AdventureParty.BuffPotionPolicy.values();
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         AdventureParty.BuffPotionPolicy var14 = var11[var13];
         this.buffPolicyDropdown.options.add(var14, var14.displayName);
      }

      this.buffPolicyDropdown.setSelected(var1.adventureParty.getBuffPotionPolicy(), var1.adventureParty.getBuffPotionPolicy().displayName);
      this.buffPolicyDropdown.onSelected((var1x) -> {
         var1.adventureParty.setBuffPotionPolicy((AdventureParty.BuffPotionPolicy)var1x.value, true);
      });
      if (var3 != -1) {
         this.addComponent((FormLocalLabel)var9.nextY(new FormLocalLabel("ui", "adventurepartyinventory", new FontOptions(20), -1, 4, 0, this.getWidth() - 8), 2));
         this.addComponent((FormLocalLabel)var9.nextY(new FormLocalLabel("ui", "adventurepartyinventorytip", new FontOptions(12), -1, 4, 0, this.getWidth() - 8), 4));
         this.inventoryContent = (FormContentBox)this.addComponent(new FormContentBox(0, var9.next(40), this.getWidth(), 40));
      }

      int var15 = Math.min(var5 - 8, 350);
      this.compressInventoryButton = (FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "adventurepartyinventorycompress", this.getWidth() / 2 - var15 / 2, 4, var15, FormInputSize.SIZE_24, ButtonColor.BASE));
      this.compressInventoryButton.onClicked((var1x) -> {
         var1.network.sendPacket(new PacketAdventurePartyCompressInventory());
      });
      if (var7 != null) {
         this.backButton = (FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "backbutton", this.getWidth() / 2 - 75, 4, 150, FormInputSize.SIZE_24, ButtonColor.BASE));
         this.backButton.onClicked((var1x) -> {
            var7.run();
         });
      }

      this.updateHeight();
      this.sizeUpdated = var8;
   }

   public void updateHeight() {
      FormFlow var1 = new FormFlow();
      if (this.inventoryContent != null) {
         var1.next(this.inventoryContent.getY());
         this.slots = new FormContainerSlot[this.slotEndIndex - this.slotStartIndex + 1];
         short var2 = 140;
         byte var3 = 40;
         int var4 = this.getWidth() - 8;
         int var5 = Math.max(var4 / var3, 1);
         int var6 = Math.min(this.container.client.playerMob.getInv().party.getSize(), this.slots.length);
         int var7 = (int)Math.ceil((double)((float)var6 / (float)var5));
         this.inventoryContent.clearComponents();

         for(int var8 = 0; var8 < var6; ++var8) {
            int var9 = var8 + this.slotStartIndex;
            int var10 = var8 % var5;
            int var11 = var8 / var5;
            FormContainerSlot var12 = new FormContainerSlot(this.client, this.container, var9, 4 + var10 * 40, var11 * 40);
            var12.setDecal(var8 % 2 == 0 ? Settings.UI.inventoryslot_icon_food : Settings.UI.inventoryslot_icon_potion);
            this.slots[var8] = (FormContainerSlot)this.inventoryContent.addComponent(var12);
            if (var8 == 3) {
               this.buffPolicyDropdown.controllerDownFocus = this.slots[var8];
            }
         }

         this.inventoryContent.setContentBox(new Rectangle(0, 0, this.getWidth(), var7 * var3));
         this.inventoryContent.setHeight(Math.min(var2, var7 * var3));
         var1.next(this.inventoryContent.getHeight() + 5);
      }

      if (this.compressInventoryButton != null) {
         this.compressInventoryButton.setY(var1.next(28));
      }

      if (this.backButton != null) {
         this.backButton.setY(var1.next(28));
      }

      this.setHeight(var1.next());
      if (this.sizeUpdated != null) {
         this.sizeUpdated.run();
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.buffPolicyDropdown != null && this.buffPolicyDropdown.getSelected() != this.client.adventureParty.getBuffPotionPolicy()) {
         this.buffPolicyDropdown.setSelected(this.client.adventureParty.getBuffPotionPolicy(), this.client.adventureParty.getBuffPotionPolicy().displayName);
      }

      if (this.lastPartySize != this.client.adventureParty.getSize()) {
         this.lastPartySize = this.client.adventureParty.getSize();
         this.partySizeLabel.setLocalization(new LocalMessage("ui", "adventurepartysize", new Object[]{"size", this.lastPartySize}));
      }

      if (this.inventoryContent != null && this.lastInventorySize != this.container.client.playerMob.getInv().party.getSize()) {
         this.updateHeight();
         this.lastInventorySize = this.container.client.playerMob.getInv().party.getSize();
      }

      super.draw(var1, var2, var3);
   }
}
