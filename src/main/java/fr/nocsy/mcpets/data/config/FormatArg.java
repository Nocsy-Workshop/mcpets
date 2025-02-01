package fr.nocsy.mcpets.data.config;

import org.jetbrains.annotations.NotNull;

public class FormatArg {
    private final String toReplace;
    private final String replaceWith;

    public FormatArg(String toReplace, String replaceWith) {
        this.toReplace = toReplace;
        this.replaceWith = replaceWith;
    }

    public FormatArg(String toReplace, @NotNull Integer replaceWith) {
        this.toReplace = toReplace;
        this.replaceWith = Integer.toString(replaceWith);
    }

    /**
     * Apply the format to the specified string
     */
    public String applyToString(String toApply) {
        return toApply.replace(toReplace, replaceWith);
    }
}
