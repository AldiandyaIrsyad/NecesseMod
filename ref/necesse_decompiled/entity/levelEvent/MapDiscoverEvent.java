package necesse.entity.levelEvent;

public class MapDiscoverEvent extends LevelEvent {
   public MapDiscoverEvent() {
   }

   public void init() {
      super.init();
      if (this.isClient()) {
         this.level.getClient().levelManager.map().discoverEntireMap();
      }

      this.over();
   }
}
