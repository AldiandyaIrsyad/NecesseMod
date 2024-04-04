package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.Zoning;
import necesse.engine.util.ZoningChange;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormHorizontalToggle;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDefendZoneAutoExpandEvent;
import necesse.inventory.container.settlement.events.SettlementDefendZoneChangedEvent;
import necesse.level.maps.hudManager.HudDrawElement;

public class SettlementDefendZoneForm<T extends SettlementContainer> extends Form implements SettlementSubForm {
   public final Client client;
   public final T container;
   public final SettlementContainerForm<T> containerForm;
   public Zoning zone;
   public FormHorizontalToggle toggle;
   public HudDrawElement hudDrawElement;
   public int defendZoneSubscription = -1;

   public SettlementDefendZoneForm(Client var1, T var2, SettlementContainerForm<T> var3) {
      super(400, 200);
      this.client = var1;
      this.container = var2;
      this.containerForm = var3;
      FormFlow var4 = new FormFlow(10);
      this.addComponent((FormLocalLabel)var4.nextY(new FormLocalLabel("ui", "settlementdefendzonehelp", new FontOptions(16), 0, this.getWidth() / 2, 10, this.getWidth() - 20), 10));
      this.toggle = (FormHorizontalToggle)this.addComponent(new FormHorizontalToggle(10, var4.next()));
      FormLocalLabel var5 = (FormLocalLabel)this.addComponent((FormLocalLabel)var4.nextY(new FormLocalLabel("ui", "settlementautoexpand", new FontOptions(16), -1, 50, 10, this.getWidth() - 20 - 40), 10));
      Rectangle var6 = var5.getBoundingBox();
      int var7 = this.getWidth() - var6.width - 40;
      var5.setPosition(var7 / 2 + 40, var5.getY());
      this.toggle.setPosition(var7 / 2, var5.getY() + var5.getHeight() / 2 - 8);
      this.toggle.onToggled((var1x) -> {
         var2.changeDefendZoneAutoExpandAction.runAndSend(((FormButtonToggle)var1x.from).isToggled());
      });
      this.setHeight(var4.next());
      this.onWindowResized();
   }

   protected void init() {
      super.init();
      this.container.onEvent(SettlementDefendZoneAutoExpandEvent.class, (var1) -> {
         this.toggle.setToggled(var1.active);
      });
      this.container.onEvent(SettlementDefendZoneChangedEvent.class, (var1) -> {
         if (this.zone == null) {
            this.zone = new Zoning(new Rectangle(this.client.getLevel().width, this.client.getLevel().height));
         }

         synchronized(this.zone) {
            var1.change.applyTo(this.zone);
         }
      });
   }

   public void clearHudElement() {
      if (this.hudDrawElement != null) {
         this.hudDrawElement.remove();
         this.hudDrawElement = null;
      }

   }

   public void setupHudElement() {
      this.clearHudElement();
      this.hudDrawElement = this.client.getLevel().hudManager.addElement(new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            if (SettlementDefendZoneForm.this.zone != null) {
               synchronized(SettlementDefendZoneForm.this.zone) {
                  final SharedTextureDrawOptions var5 = SettlementDefendZoneForm.this.zone.getDrawOptions(new Color(50, 50, 125), new Color(50, 50, 255, 75), var2);
                  if (var5 != null) {
                     var1.add(new SortedDrawable() {
                        public int getPriority() {
                           return -100000;
                        }

                        public void draw(TickManager var1) {
                           var5.draw();
                        }
                     });
                  }
               }
            }

         }
      });
   }

   public void setupGameTool() {
      Screen.clearGameTools(this);
      Screen.setGameTool(new CreateOrExpandGlobalZoneGameTool(this.client.getLevel()) {
         public void onExpandedZone(Rectangle var1) {
            SettlementDefendZoneForm.this.container.changeDefendZoneAction.runAndSend(ZoningChange.expand(var1));
         }

         public void onShrankZone(Rectangle var1) {
            SettlementDefendZoneForm.this.container.changeDefendZoneAction.runAndSend(ZoningChange.shrink(var1));
         }

         public boolean canCancel() {
            return false;
         }

         public boolean forceControllerCursor() {
            return true;
         }
      }, this);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosInventory(this);
   }

   public void dispose() {
      super.dispose();
      Screen.clearGameTools(this);
      this.clearHudElement();
   }

   public void onSetCurrent(boolean var1) {
      if (var1) {
         if (this.defendZoneSubscription == -1) {
            this.defendZoneSubscription = this.container.subscribeDefendZoneAction.subscribe();
         }

         this.setupHudElement();
         this.setupGameTool();
      } else {
         this.zone = null;
         Screen.clearGameTools(this);
         this.clearHudElement();
         if (this.defendZoneSubscription != -1) {
            this.container.subscribeDefendZoneAction.unsubscribe(this.defendZoneSubscription);
            this.defendZoneSubscription = -1;
         }
      }

   }

   public GameMessage getMenuButtonName() {
      return new LocalMessage("ui", "settlementdefendzone");
   }

   public String getTypeString() {
      return "defendzone";
   }
}
