package fr.nocsy.mcpets.modeler.bone;

import kr.toxicity.model.api.nms.ModelNametag;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class BetterModelNameTag implements AbstractNameTag {

    private final ModelNametag delegate;

    public BetterModelNameTag(ModelNametag delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setString(String string) {
        delegate.component(LegacyComponentSerializer.legacySection().deserialize(string));
    }

    @Override
    public void setVisible(boolean visible) {
        delegate.alwaysVisible(visible);
    }
}
