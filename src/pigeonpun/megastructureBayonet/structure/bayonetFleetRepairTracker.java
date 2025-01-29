package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import org.apache.log4j.Logger;

public class bayonetFleetRepairTracker implements EveryFrameScript {
    CampaignFleetAPI bayonetFleet;
    public static final Logger log = Global.getLogger(bayonetFleetRepairTracker.class);
    public bayonetFleetRepairTracker(CampaignFleetAPI fleet) {
        this.bayonetFleet = fleet;
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
        bayonetManager.bayonetStatusData data = bayonetManager.getCurrentBayonetStatus(bayonetFleet);
        //todo: add building to this too
        if (data.status.equals(bayonetManager.BAYONET_STATION_STATUS.DAMAGED)) {
            float days = Global.getSector().getClock().convertToDays(amount);

            data.dayLeftBeforeFunctional -= days;
            if (data.dayLeftBeforeFunctional <= 0) {
                data.dayLeftBeforeFunctional = 0;
            }
            bayonetFleet.getCustomData().put(bayonetManager.BAYONET_ENTITY_STATUS_DATA, data);
            //display current repair CR in campaign
            float maxDays = bayonetManager.getTotalRepairDay(bayonetFleet);
            for(FleetMemberAPI member: bayonetFleet.getFleetData().getMembersListCopy()) {
                float currentRepairPercentage = (1 - (data.dayLeftBeforeFunctional / maxDays));
                float setCR = member.getRepairTracker().getMaxCR() * currentRepairPercentage;
                member.getRepairTracker().setCR(setCR);
            }

            if(data.dayLeftBeforeFunctional == 0) {
                bayonetManager.changeBayonetStationStatus(bayonetManager.BAYONET_STATION_STATUS.FUNCTIONAL, bayonetFleet);
            }
        }
    }

}
