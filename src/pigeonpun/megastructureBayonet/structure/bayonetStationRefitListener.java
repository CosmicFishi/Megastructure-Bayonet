package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import org.apache.log4j.Logger;

public class bayonetStationRefitListener implements EveryFrameScript {
    CampaignFleetAPI bayonetFleet;
    public static final Logger log = Global.getLogger(bayonetStationRefitListener.class);
    public bayonetStationRefitListener(CampaignFleetAPI fleet) {
        this.bayonetFleet = fleet;
    }
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        //Adding bayonet station to refit UI shenanigans
        if(Global.getSector().getMemoryWithoutUpdate().get("$bayonet_isFleetEncounter") != null &&
                Global.getSector().getMemoryWithoutUpdate().get("$bayonet_isFleetEncounter") == true) {
            if(Global.getSector().getCampaignUI().getCurrentCoreTab() != null) {
                FleetMemberAPI bayonetStation = bayonetFleet.getFleetData().getMembersListCopy().get(0);
                if(Global.getSector().getCampaignUI().getCurrentCoreTab().equals(CoreUITabId.REFIT) && !isBayonetAdded()) {
                    //adding bayonet station to player fleet
                    Global.getSector().getPlayerFleet().getFleetData().addFleetMember(bayonetStation);
                    //Core UI doesn't sync up completely with the member adding, force opening the UI tab again seems to do the trick
                    //if it works, it workS
                    Global.getSector().getCampaignUI().showCoreUITab(Global.getSector().getCampaignUI().getCurrentCoreTab());
                } else {
                    if(!Global.getSector().getCampaignUI().getCurrentCoreTab().equals(CoreUITabId.REFIT) && isBayonetAdded()) {
                        //removing the station if it's not refit UI
                        Global.getSector().getPlayerFleet().getFleetData().removeFleetMember(bayonetStation);
                        //if it works, it workS
                        Global.getSector().getCampaignUI().showCoreUITab(Global.getSector().getCampaignUI().getCurrentCoreTab());
                    }
                }
            } else {
                //When player close refit
                if(isBayonetAdded()) {
                    FleetMemberAPI bayonetStation = bayonetFleet.getFleetData().getMembersListCopy().get(0);
                    Global.getSector().getPlayerFleet().getFleetData().removeFleetMember(bayonetStation);
                }
            }
        }
    }
    public boolean isBayonetAdded() {
        FleetDataAPI playerFleetData = Global.getSector().getPlayerFleet().getFleetData();
        for(FleetMemberAPI member: playerFleetData.getMembersListCopy()) {
            if(member.getId().equals(bayonetFleet.getFleetData().getMembersListCopy().get(0).getId())) {
                return true;
            }
        }
        return false;
    }
}
