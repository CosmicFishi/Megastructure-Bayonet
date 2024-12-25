package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SubmarketPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class bayonetStorageFee implements EconomyTickListener, TooltipMakerAPI.TooltipCreator {
    public static final String BAYONET_STORAGE_FEE_KEY = "$bayonet_storage_fee";
    MarketAPI market;
    bayonetStorageFee() {
        market = bayonetManager.getBayonetMarket();
        Global.getSector().getListenerManager().addListener(this);
        Global.getSector().getMemoryWithoutUpdate().set(BAYONET_STORAGE_FEE_KEY, this);
    }
    public static bayonetStorageFee get() {
        return (bayonetStorageFee) Global.getSector().getMemoryWithoutUpdate().get(BAYONET_STORAGE_FEE_KEY);
    }

    @Override
    public void reportEconomyTick(int iterIndex) {

        int cargoCost = bayonetManager.getStorageCargoTotalFee(market);
        int shipCost = bayonetManager.getStorageShipTotalFee(market);
        int totalCost = (cargoCost + shipCost);
        MonthlyReport report = SharedData.getData().getCurrentReport();
        MonthlyReport.FDNode storageNode = report.getNode(MonthlyReport.STORAGE);
        if(storageNode.name == null) {
            storageNode.name = "Storage";
            storageNode.custom = MonthlyReport.STORAGE;
            storageNode.tooltipCreator = report.getMonthlyReportTooltip();
        }

        //Get a node -> set that node as the origin, create your own node. Any nested node must take the custom node as its origin.
        //If parent node has upkeep/income -> it would add/subtract to the nested node upkeep/income
        //Getting the first node is the most important part, make sure to check if the node NAME is null to create the node
        //remember to put custom property as well so it will now which root's node it belongs to
        //refer to MontlyReport.java / CoreScript.java
        MonthlyReport.FDNode bayonetNode = report.getNode(storageNode, "megastructure_bayonet");
//        bayonetNode.upkeep = totalCost;
        bayonetNode.name = "Bayonet Storage Maintenance Fee";
        bayonetNode.icon = Global.getSettings().getSpriteName("income_report", "bayonet_stockpiling");
        bayonetNode.custom = market;
        bayonetNode.tooltipCreator = this;

        MonthlyReport.FDNode bayonetCargoNode = report.getNode(bayonetNode, "megastructure_bayonet_cargo");
        bayonetCargoNode.upkeep = cargoCost;
        bayonetCargoNode.name = "Cargo Storage Fee";
        bayonetCargoNode.tooltipCreator = this;

        MonthlyReport.FDNode bayonetShipNode = report.getNode(bayonetNode, "megastructure_bayonet_ship");
        bayonetShipNode.upkeep = shipCost;
        bayonetShipNode.name = "Ship Storage Fee";
        bayonetShipNode.tooltipCreator = this;
    }

    @Override
    public void reportEconomyMonthEnd() {

    }

    @Override
    public boolean isTooltipExpandable(Object tooltipParam) {
        return false;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 450;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        int cargoCost = bayonetManager.getStorageCargoTotalFee(bayonetManager.getBayonetMarket());
        int shipCost = bayonetManager.getStorageShipTotalFee(bayonetManager.getBayonetMarket());
        int totalCost = -(cargoCost + shipCost);
        float pad = 3f;
        float opad = 10f;

        Color h = Misc.getHighlightColor();

        FactionAPI faction = Global.getSector().getPlayerFaction();
        Color color = faction.getBaseUIColor();
        Color dark = faction.getDarkUIColor();
        Color grid = faction.getGridUIColor();
        Color bright = faction.getBrightUIColor();

        SubmarketPlugin storage = bayonetManager.getBayonetStorage();

        CargoAPI cargo = Global.getFactory().createCargo(true);
        List<FleetMemberAPI> ships = new ArrayList<FleetMemberAPI>();

        tooltip.addPara(market.getName(), pad, bright);
        if (storage != null) {
            cargo.addAll(storage.getCargo());
            ships.addAll(storage.getCargo().getMothballedShips().getMembersListCopy());
        }

        tooltip.addSectionHeading("Cargo in storage", color, dark, Alignment.MID, opad);
        opad = 10f;
        tooltip.showCargo(cargo, 10, true, opad);
        tooltip.addSectionHeading("Ships in storage", color, dark, Alignment.MID, opad);
        opad = 10f;
        tooltip.showShips(ships, 10, true, opad);

        int cost = totalCost;
        if (cost > 0) {
            tooltip.addPara("Monthly storage fee: %s", opad, h, Misc.getDGSCredits(cost));
        }
    }
}
