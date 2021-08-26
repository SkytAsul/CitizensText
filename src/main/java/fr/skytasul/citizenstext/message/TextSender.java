package fr.skytasul.citizenstext.message;

import fr.skytasul.citizenstext.CitizensText;
import fr.skytasul.citizenstext.CitizensTextConfiguration;

public interface TextSender {
	
	public static final NPCSender NPC_SENDER = new NPCSender();
	public static final PlayerSender PLAYER_SENDER = new PlayerSender();
	public static final NoSender NO_SENDER = new NoSender();
	
	@Override
	public abstract String toString();
	
	public static class NPCSender implements TextSender {
		
		private NPCSender() {}
		
		public String format(String message, String name, int id, int size) {
			return CitizensText.formatMessage(CitizensTextConfiguration.getNPCFormat(), message, name, id, size);
		}
		
		@Override
		public String toString() {
			return "npc";
		}
		
	}
	
	public static class PlayerSender implements TextSender {
		
		private PlayerSender() {}
		
		public String format(String message, String name, int id, int size) {
			return CitizensText.formatMessage(CitizensTextConfiguration.getPlayerFormat(), message, name, id, size);
		}
		
		@Override
		public String toString() {
			return "player";
		}
		
	}
	
	public static class NoSender implements TextSender {
		
		private NoSender() {}
		
		@Override
		public String toString() {
			return "noSender";
		}
		
	}
	
	public interface CustomizedSender extends TextSender {
		
		public abstract String format(String message, int id, int size);
		
	}
	
	public static class FixedNPCSender implements CustomizedSender {
		
		public static final String PREFIX = "otherNPC:";
		
		private String npcName;
		
		public FixedNPCSender(String npcName) {
			this.npcName = npcName;
		}
		
		@Override
		public String format(String message, int id, int size) {
			return CitizensText.formatMessage(CitizensTextConfiguration.getNPCFormat(), message, npcName, id, size);
		}
		
		@Override
		public String toString() {
			return PREFIX + npcName;
		}
		
	}
	
	public static class FixedFormatSender implements CustomizedSender {
		
		public static final String PREFIX = "otherFormat:";
		
		private String format;
		
		public FixedFormatSender(String format) {
			this.format = format;
		}
		
		@Override
		public String format(String message, int id, int size) {
			return CitizensText.formatMessage(format, message, "invalid", id, size);
		}
		
		@Override
		public String toString() {
			return PREFIX + format;
		}
		
	}
	
	public static TextSender fromString(String string) {
		if (string.equals("npc")) return NPC_SENDER;
		if (string.equals("player")) return PLAYER_SENDER;
		if (string.equals("noSender")) return NO_SENDER;
		if (string.startsWith(FixedNPCSender.PREFIX)) return new FixedNPCSender(string.substring(FixedNPCSender.PREFIX.length()));
		if (string.startsWith(FixedFormatSender.PREFIX)) return new FixedFormatSender(string.substring(FixedFormatSender.PREFIX.length()));
		throw new IllegalArgumentException("Unknown sender: " + string);
	}
		
}
