package pigeonpun.megastructureBayonet;

import com.fs.starfarer.api.BaseModPlugin;

public class ModPlugin extends BaseModPlugin {
    public static final String BAYONET_STORAGE_STATS_KEY = "bayonet_storage";
    public static final String BAYONET_SHIP_STORAGE_STATS_KEY = "bayonet_ship_storage";
    public static final String BAYONET_STORAGE_SUBMARKET = "megastructure_bayonet_storage";
    //- Ability to transfer cargo (player cargo <-> Bayonet cargo)
    //      Right now, only transferring between ship is good, commodities however, only buying
    //todo - Emergency repair of fleet
    //todo - Customize station if possible (WIP)
    //todo - Call in Bayonet in hyperspace and still get it to function ?
    //todo - Megastructure integration ?
    //todo - animation - credit SirHarley
    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();
    }

    @Override
    public void onNewGame() {
        super.onNewGame();
        // Add your code here, or delete this method (it does nothing unless you add code)
    }

    // You can add more methods from ModPlugin here. Press Control-O in IntelliJ to see options.
}
