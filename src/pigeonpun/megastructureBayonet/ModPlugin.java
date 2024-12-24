package pigeonpun.megastructureBayonet;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.thoughtworks.xstream.XStream;
import org.magiclib.bounty.MagicBountyCampaignPlugin;

public class ModPlugin extends BaseModPlugin {
    public static final String BAYONET_STORAGE_STATS_KEY = "bayonet_storage";
    public static final String BAYONET_SHIP_STORAGE_STATS_KEY = "bayonet_ship_storage";
    public static final String BAYONET_SHIP_STORAGE_NO_STORE = "mega_bayonet_ship_storage_no_store";
    //- Ability to transfer cargo (player cargo <-> Bayonet cargo) (done)
    //- Emergency repair of fleet ^ - Nah
    //- Customize station if possible (done)
    //          Fix a issue where the station can be stored into storage. (done)
    //todo - monthly fee applying to player
    //todo - Station destroyed by enemy -> unrecoverable -> you have to build a brand new one
    //todo - Call in Bayonet in hyperspace and still get it to function ?
    //todo - Megastructure integration (WIP) - Release v0.1.0
    //todo - animation - credit SirHarley
    //todo - Have station "docked" in the planet selected - Release v0.2.0
    //todo - Upgradable Station thru Megastructure - Release v1.0.0
    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();
    }

    @Override
    public void onNewGame() {
        super.onNewGame();
        // Add your code here, or delete this method (it does nothing unless you add code)
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().registerPlugin(new bayonetBaseCampaignPlugin());
    }

    @Override
    public void configureXStream(XStream x) {
        super.configureXStream(x);
        x.alias("Megastructure_bayonetCampaignPlugin", bayonetBaseCampaignPlugin.class);
    }
    // You can add more methods from ModPlugin here. Press Control-O in IntelliJ to see options.
}
