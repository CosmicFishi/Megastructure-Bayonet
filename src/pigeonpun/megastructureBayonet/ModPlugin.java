package pigeonpun.megastructureBayonet;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.thoughtworks.xstream.XStream;
import org.magiclib.bounty.MagicBountyCampaignPlugin;
import pigeonpun.megastructureBayonet.structure.bayonetManager;
import pigeonpun.megastructureBayonet.structure.bayonetStorageFee;

public class ModPlugin extends BaseModPlugin {
    //- Ability to transfer cargo (player cargo <-> Bayonet cargo) (done)
    //- Emergency repair of fleet ^ - Nah
    //- Customize station if possible (done)
    //          Fix a issue where the station can be stored into storage. (done)
    //- monthly fee applying to player (done)
    //- custom hullmod to set CR of station (DONE)
    //- Intel Repairing/building (DONE)
    //- Station destroyed by enemy -> unrecoverable -> you have to repair it (DONE)
    //- When destroyed, the station can not be summon anymore (DONE).
    //- Storage fee now apply with maintenance fee. (DONE)
    //todo - A send home button. "Emergency home jump"
    //todo - Officer replacing seems to remove the AI core completely ? (May be ignorable if its a fleet commander)
    //  Not a problem if there were no AI core to begin with. Station when first built wont have any commander
    //todo - Megastructure integration (WIP) - Release v0.1.0
    //todo - Call in Bayonet in hyperspace and still get it to function ?
    //todo - animation - credit SirHarley
    //todo - Optional difficulty: Some ships get "damaged"
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
        bayonetManager.createBayonetStorageFee();
    }

    @Override
    public void configureXStream(XStream x) {
        super.configureXStream(x);
        x.alias("Megastructure_bayonetCampaignPlugin", bayonetBaseCampaignPlugin.class);
        x.alias("Megastructure_bayonetStorageFee", bayonetStorageFee.class);
    }
    // You can add more methods from ModPlugin here. Press Control-O in IntelliJ to see options.
}
