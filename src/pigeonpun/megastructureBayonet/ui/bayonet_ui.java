package pigeonpun.megastructureBayonet.ui;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class bayonet_ui implements CustomUIPanelPlugin {
    protected CustomVisualDialogDelegate.DialogCallbacks callbacks;
    protected InteractionDialogAPI dialog;
    protected HashMap<String, bayonet_ui_component> componentMap = new HashMap<>();
    protected List<ButtonAPI> buttons = new ArrayList<>();
    protected HashMap<ButtonAPI, String> buttonMap = new HashMap<>();
    protected CustomPanelAPI containerPanel; //Created panel from ba_deligate.java
    protected TooltipMakerAPI mainTooltip;
    protected void init(CustomPanelAPI panel, CustomVisualDialogDelegate.DialogCallbacks callbacks, InteractionDialogAPI dialog) {
        this.callbacks = callbacks;
        this.containerPanel = panel;
        this.dialog = dialog;
        mainTooltip = this.containerPanel.createUIElement(this.containerPanel.getPosition().getWidth(), this.containerPanel.getPosition().getHeight(), false);
        mainTooltip.setForceProcessInput(true);
        containerPanel.addUIElement(mainTooltip).inTL(0,0);
        refresh();
    }
    protected void refresh() {
        //reset button for checking press
        for (ButtonAPI b : buttons) {
            if (b.isChecked()) {
                b.setChecked(false);
            }
        }
//        log.info("refreshing");
        buttons.clear();
        buttonMap.clear();
        componentMap.clear();
    }
    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
