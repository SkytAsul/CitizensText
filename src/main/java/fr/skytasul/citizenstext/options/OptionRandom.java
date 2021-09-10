package fr.skytasul.citizenstext.options;

import fr.skytasul.citizenstext.texts.TextInstance;

public class OptionRandom extends BooleanOption {
	
	public OptionRandom(TextInstance txt) {
		super(txt);
	}
	
	@Override
	public Boolean getDefault() {
		return false;
	}
	
}
