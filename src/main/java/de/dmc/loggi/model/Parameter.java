package de.dmc.loggi.model;

/**
 *
 * @author CptSpaetzle
 */
 public enum Parameter {
    TEMPLATE("t", "template", "path to template.json file", true, false),
    SOURCE("s", "source", "Source log file", true, false),
    HELP("h", "help", "Display help and usage info", false, false);
    
    String shortName;
    String longName;
    String description;
    boolean hasArgument;
    boolean required;

    private Parameter(String shortName, String longName, String description, boolean hasArgument, boolean required) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.hasArgument = hasArgument;
        this.required = required;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasArgument() {
        return hasArgument;
    }

    public void setHasArgument(boolean hasArgument) {
        this.hasArgument = hasArgument;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    
}