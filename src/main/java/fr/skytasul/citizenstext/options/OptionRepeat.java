package fr.skytasul.citizenstext.options;

import fr.skytasul.citizenstext.texts.TextInstance;

public class OptionRepeat extends BooleanOption {
	
	protected OptionRepeat(TextInstance txt) {
		super(txt);
	}
	
	@Override
	public Boolean getDefault() {
		return true;
	}
	
}
