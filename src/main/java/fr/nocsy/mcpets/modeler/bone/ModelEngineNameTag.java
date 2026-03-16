package fr.nocsy.mcpets.modeler.bone;

import com.ticxo.modelengine.api.model.bone.type.NameTag;

public class ModelEngineNameTag implements AbstractNameTag {

    private final NameTag delegate;

    public ModelEngineNameTag(NameTag delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setString(String string) {
        delegate.setString(string);
    }

    @Override
    public void setVisible(boolean visible) {
        delegate.setVisible(visible);
    }
}
