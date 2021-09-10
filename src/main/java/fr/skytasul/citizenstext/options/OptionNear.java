package fr.skytasul.citizenstext.options;

import fr.skytasul.citizenstext.CitizensTextConfiguration;
import fr.skytasul.citizenstext.texts.TextInstance;

public class OptionNear extends BooleanOption {
	
	protected OptionNear(TextInstance txt) {
		super(txt);
	}
	
	@Override
	public Boolean getDefault() {
		return CitizensTextConfiguration.clickDisabled();
	}
	
}
