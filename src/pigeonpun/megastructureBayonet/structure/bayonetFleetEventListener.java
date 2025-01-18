package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import org.apache.log4j.Logger;

import java.awt.*;

public class bayonetFleetEventListener implements FleetEventListener {
    public static final Logger log = Global.getLogger(bayonetFleetEventListener.class);
    CampaignFleetAPI bayonetFleet;

    public bayonetFleetEventListener(CampaignFleetAPI fleet) {
        this.bayonetFleet = fleet;
    }
    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        if(reason.equals(CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE)) {
            fleet.setAbortDespawn(true);
            //todo: custom bayonet member variant
            FleetMemberAPI member = bayonetManager.createBayonetStationMember();
            fleet.getFleetData().addFleetMember(member);
            bayonetManager.changeBayonetStationStatus(bayonetManager.BAYONET_STATION_STATUS.REPAIRING, fleet);
        }
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }
}
