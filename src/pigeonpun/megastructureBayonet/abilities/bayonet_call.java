package pigeonpun.megastructureBayonet.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import org.apache.log4j.Logger;

public class bayonet_call extends BaseDurationAbility {
    public static final String bayonet_memory_ID = "$megastructure_bayonet";
    public static final Logger log = Global.getLogger(bayonet_call.class);
    @Override
    protected void activateImpl() {

    }

    @Override
    protected void applyEffect(float amount, float level) {
        SectorEntityToken station;
        if(Global.getSector().getMemoryWithoutUpdate().get(bayonet_memory_ID) == null) {
            station = Global.getSector().getCurrentLocation().addCustomEntity(
                    "mega_bayonet",
                    "The Bayonet", "megastructure-bayonet",
                    Factions.NEUTRAL
            );
            Global.getSector().getMemoryWithoutUpdate().set(bayonet_memory_ID, station);
        } else {
            station = (SectorEntityToken) Global.getSector().getMemoryWithoutUpdate().get(bayonet_memory_ID);
        }
        if(station != null) {
            station.setContainingLocation(Global.getSector().getCurrentLocation());
            station.setLocation(Global.getSector().getPlayerFleet().getLocation().x, Global.getSector().getPlayerFleet().getLocation().y);
            log.info("aaaaaaaaaaaa");
        }
    }

    @Override
    protected void deactivateImpl() {

    }

    @Override
    protected void cleanupImpl() {

    }
}
