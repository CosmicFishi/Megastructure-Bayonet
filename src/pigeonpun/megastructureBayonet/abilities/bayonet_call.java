package pigeonpun.megastructureBayonet.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
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
            //todo - Ability to transfer cargo (player cargo <-> Bayonet cargo)
            //todo - Emergency repair of fleet
            //todo - Customize station if possible
            //todo - Call in Bayonet in hyperspace and still get it to function ?
            //todo - Megastructure integration ?
//            station.getMemoryWithoutUpdate().set("$abandonedStation", true);
//            MarketAPI market = Global.getFactory().createMarket(marketId, station.getName(), 0);
//            market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
//            market.setPrimaryEntity(station);
//            market.setFactionId(Factions.NEUTRAL);
////            market.addCondition(Conditions.ABANDONED_STATION);
//            market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
//            market.setPlanetConditionMarketOnly(false);
//            ((StoragePlugin)market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getPlugin()).setPlayerPaidToUnlock(true);
//            station.setMarket(market);
//            station.getMemoryWithoutUpdate().unset("$tradeMode");
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
