package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

public class bayonetFleetEventListener implements FleetEventListener, EveryFrameScript {
    CampaignFleetAPI fleet;
    public bayonetFleetEventListener(CampaignFleetAPI fleet) {
        this.fleet = fleet;
    }
    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        //todo: check if this works
        if(reason.equals(CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE)) {
            fleet.setAbortDespawn(true);
            //todo: custom bayonet member variant
            FleetMemberAPI member = bayonetManager.createBayonetStationMember();
            fleet.getFleetData().addFleetMember(member);
            bayonetManager.changeBayonetStationStatus(bayonetManager.BAYONET_STATION_STATUS.REPAIRING, fleet);
            fleet.setContainingLocation(Global.getSector().getCurrentLocation());
            fleet.setLocation(Global.getSector().getPlayerFleet().getLocation().x, Global.getSector().getPlayerFleet().getLocation().y);
        }
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {

    }
}
