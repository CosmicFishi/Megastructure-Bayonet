package pigeonpun.megastructureBayonet;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.thoughtworks.xstream.XStream;
import org.magiclib.bounty.MagicBountyCampaignPlugin;

public class ModPlugin extends BaseModPlugin {
    public static final String BAYONET_STORAGE_STATS_KEY = "bayonet_storage";
    public static final String BAYONET_SHIP_STORAGE_STATS_KEY = "bayonet_ship_storage";
    public static final String BAYONET_SHIP_STORAGE_NO_STORE = "mega_bayonet_ship_storage_no_store";
    //todo: - Ability to transfer cargo (player cargo <-> Bayonet cargo)
    //todo - Emergency repair of fleet ^
    //todo - Customize station if possible (WIP)
    //          Fix a issue where the station can be stored into storage.
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
