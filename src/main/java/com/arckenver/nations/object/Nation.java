package com.arckenver.nations.object;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import com.arckenver.nations.DataHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.channel.NationMessageChannel;
import com.flowpowered.math.vector.Vector2i;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Nation
{
	public static final String TYPE_OUTSIDER = "outsider";
	public static final String TYPE_CITIZEN = "citizen";
	public static final String TYPE_COOWNER = "coowner";
	
	public static final String PERM_BUILD = "build";
	public static final String PERM_INTERACT = "interact";
	
	private UUID uuid;
	private String name;
	private String tag;
	private boolean isAdmin;
	private Hashtable<String, Location<World>> spawns;
	private Region region;
	private UUID president;
	private ArrayList<UUID> ministers;
	private ArrayList<UUID> citizens;
	private Hashtable<String, Hashtable<String, Boolean>> perms;
	private Hashtable<String, Boolean> flags;
	private Hashtable<UUID, Zone> zones;
	private int extras;
	private int extraspawns;
	private double taxes;
<<<<<<< Updated upstream
	
=======
	private int rentInterval;// hours
	private LocalDateTime lastRentCollectTime;
	private LocalDateTime lastWarTime = LocalDateTime.of(1999,01,5,1,1,0);
	private War currentWar = null;
	public boolean inWar = false;

>>>>>>> Stashed changes
	private NationMessageChannel channel = new NationMessageChannel();

	public Nation(UUID uuid, String name)
	{
		this(uuid, name, false);
	}
	
	@SuppressWarnings("serial")
	public Nation(UUID uuid, String name, boolean isAdmin)
	{
		this.uuid = uuid;
		this.name = name;
		this.tag = null;
		this.isAdmin = isAdmin;
		this.spawns = new Hashtable<String, Location<World>>();
		this.region = new Region();
		this.president = null;
<<<<<<< Updated upstream
		this.ministers = new ArrayList<UUID>();
		this.citizens = new ArrayList<UUID>();
		this.flags = new Hashtable<String, Boolean>();
		for (Entry<Object, ? extends CommentedConfigurationNode> e : ConfigHandler.getNode("nations", "flags").getChildrenMap().entrySet())
		{
=======
		this.ministers = new ArrayList<>();
		this.citizens = new ArrayList<>();
		this.flags = new Hashtable<>();
		this.rentInterval = ConfigHandler.getNode("nations", "defaultRentInterval").getInt();
		this.lastRentCollectTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), 0)); //just hours
		this.lastWarTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), 0)); //just hours
		this.currentWar = null;
		this.inWar = false;

		for (Entry<Object, ? extends CommentedConfigurationNode> e : ConfigHandler.getNode("nations", "flags").getChildrenMap().entrySet()) {
>>>>>>> Stashed changes
			flags.put(e.getKey().toString(), e.getValue().getBoolean());
		}
		this.perms = new Hashtable<String, Hashtable<String, Boolean>>()
		{{
			put(TYPE_OUTSIDER, new Hashtable<String, Boolean>()
			{{
				put(PERM_BUILD, ConfigHandler.getNode("nations", "perms").getNode(TYPE_OUTSIDER).getNode(PERM_BUILD).getBoolean());
				put(PERM_INTERACT, ConfigHandler.getNode("nations", "perms").getNode(TYPE_OUTSIDER).getNode(PERM_INTERACT).getBoolean());
			}});
			put(TYPE_CITIZEN, new Hashtable<String, Boolean>()
			{{
				put(PERM_BUILD, ConfigHandler.getNode("nations", "perms").getNode(TYPE_CITIZEN).getNode(PERM_BUILD).getBoolean());
				put(PERM_INTERACT, ConfigHandler.getNode("nations", "perms").getNode(TYPE_CITIZEN).getNode(PERM_INTERACT).getBoolean());
			}});
		}};
		this.zones = new Hashtable<UUID, Zone>();
		this.extras = 0;
		this.extraspawns = 0;
		this.taxes = ConfigHandler.getNode("nations", "defaultTaxes").getDouble();
	}

<<<<<<< Updated upstream
	public UUID getUUID()
	{
=======

	public boolean isInWar() {
		return inWar;
	}

	public void setInWar(boolean inWar) {
		this.inWar = inWar;
	}

	public UUID getUUID() {
>>>>>>> Stashed changes
		return uuid;
	}

	public void setUuid(UUID uuid)
	{
		this.uuid = uuid;
	}

	public String getName()
	{
		return name.replace("_", " ");
	}

	public String getRealName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public boolean hasTag()
	{
		return tag != null;
	}

	public String getTag()
	{
		if (tag == null)
			return getName();
		return tag;
	}
	
	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public boolean isAdmin()
	{
		return isAdmin;
	}

	public double getTaxes()
	{
		return taxes;
	}

	public void setTaxes(double taxes)
	{
		this.taxes = taxes;
	}

	public double getUpkeep()
	{
		return ConfigHandler.getNode("prices", "upkeepPerCitizen").getDouble() * citizens.size();
	}

	public Location<World> getSpawn(String name)
	{
		return spawns.get(name);
	}

	public void addSpawn(String name, Location<World> spawn)
	{
		this.spawns.put(name, spawn);
	}
	
	public void removeSpawn(String name)
	{
		this.spawns.remove(name);
	}

	public Hashtable<String, Location<World>> getSpawns()
	{
		return spawns;
	}

	public int getNumSpawns()
	{
		return spawns.size();
	}
	
	public int getMaxSpawns()
	{
		return ConfigHandler.getNode("others", "maxNationSpawns").getInt() + extraspawns;
	}

	public int getExtraSpawns() {
		return extraspawns;
	}
	
	public void setExtraSpawns(int extraspawns)
	{
		this.extraspawns = extraspawns;
		if (this.extraspawns < 0)
			this.extraspawns = 0;
	}

	public void addExtraSpawns(int extraspawns)
	{
		this.extraspawns += extraspawns;
	}

	public void removeExtraSpawns(int extraspawns)
	{
		this.extraspawns -= extraspawns;
		if (this.extraspawns < 0)
			this.extraspawns = 0;
	}
	
	public Region getRegion()
	{
		return region;
	}

	public void setRegion(Region region)
	{
		this.region = region;
	}

	public UUID getPresident()
	{
		return president;
	}

	public void setPresident(UUID president)
	{
		this.president = president;
	}

	public boolean isPresident(UUID uuid)
	{
		return uuid.equals(president);
	}
	
	public ArrayList<UUID> getMinisters()
	{
		return ministers;
	}
	
	public void addMinister(UUID uuid)
	{
		ministers.add(uuid);
	}

	public void removeMinister(UUID uuid)
	{
		ministers.remove(uuid);
	}
	
	public boolean isMinister(UUID uuid)
	{
		return ministers.contains(uuid);
	}

	public ArrayList<UUID> getStaff()
	{
		ArrayList<UUID> staff = new ArrayList<UUID>();
		staff.add(president);
		staff.addAll(ministers);
		return staff;
	}
	
	public boolean isStaff(UUID uuid)
	{
		if (isPresident(uuid) || isMinister(uuid))
			return true;
		if (!isAdmin())
			return false;
		Optional<Player> player = Sponge.getServer().getPlayer(uuid);
		if (player.isPresent() && player.get().hasPermission("nations.admin.zone.staff"))
			return true;
		return false;
	}
	
	public ArrayList<UUID> getCitizens()
	{
		return citizens;
	}
	
	public void addCitizen(UUID uuid)
	{
		citizens.add(uuid);
		Optional<Player> player = Sponge.getServer().getPlayer(uuid);
		if (player.isPresent())
			channel.addMember(player.get());
	}

	public boolean isCitizen(UUID uuid)
	{
		return citizens.contains(uuid);
	}

	public int getNumCitizens()
	{
		return citizens.size();
	}

	public void removeCitizen(UUID uuid)
	{
		zones.values().stream()
				.filter(zone -> uuid.equals(zone.getOwner()))
				.forEach(zone -> zone.setOwner(null));
		ministers.remove(uuid);
		citizens.remove(uuid);
		Optional<Player> player = Sponge.getServer().getPlayer(uuid);
		if (player.isPresent())
		{
			channel.removeMember(player.get());
			player.get().setMessageChannel(MessageChannel.TO_ALL);
		}
	}
	
	public Hashtable<String, Boolean> getFlags()
	{
		return flags;
	}
	
	public void setFlag(String flag, boolean b)
	{
		flags.put(flag, b);
	}
	
	public boolean getFlag(String flag)
	{
		return flags.get(flag);
	}

	public boolean getFlag(String flag, Location<World> loc)
	{
		Zone zone = getZone(loc);
		if (zone == null || !zone.hasFlag(flag))
		{
			return getFlag(flag);
		}
		return zone.getFlag(flag);
	}

	public boolean getPerm(String type, String perm)
	{
		return perms.get(type).get(perm);
	}

	public Hashtable<String, Hashtable<String, Boolean>> getPerms()
	{
		return perms;
	}

	public void setPerm(String type, String perm, boolean bool)
	{
		perms.get(type).put(perm, bool);
	}

	public Hashtable<UUID, Zone> getZones()
	{
		return zones;
	}
	
	public Zone getZone(Location<World> loc)
	{
		Vector2i p = new Vector2i(loc.getBlockX(), loc.getBlockZ());
		for (Zone zone : zones.values())
		{
			if (zone.getRect().isInside(p))
			{
				return zone;
			}
		}
		return null;
	}

	public void addZone(Zone zone)
	{
		zones.put(zone.getUUID(), zone);
	}
	
	public void removeZone(UUID uuid)
	{
		zones.remove(uuid);
	}

	public int getExtras()
	{
		return extras;
	}

	public void setExtras(int extras)
	{
		this.extras = extras;
		if (this.extras < 0)
			this.extras = 0;
	}

	public void addExtras(int extras)
	{
		this.extras += extras;
	}

	public void removeExtras(int extras)
	{
		this.extras -= extras;
		if (this.extras < 0)
			this.extras = 0;
	}

	public int maxBlockSize()
	{
		return extras + citizens.size() * ConfigHandler.getNode("others", "blocksPerCitizen").getInt();
	}
<<<<<<< Updated upstream
	
	public NationMessageChannel getMessageChannel()
	{
=======

	public int getRentInterval() {
		return rentInterval;
	}

	public void setRentInterval(int rentInterval) {
		this.rentInterval = rentInterval;
	}

	public LocalDateTime getLastRentCollectTime() {
		return lastRentCollectTime;
	}

	public void setLastRentCollectTime(LocalDateTime lastRentCollectTime) {
		this.lastRentCollectTime = lastRentCollectTime;
	}

	public LocalDateTime getLastWarTime() {
		return lastWarTime;
	}

	public void setLastWarTime(LocalDateTime lastWarTime) {
		this.lastWarTime = lastWarTime;
	}

	public NationMessageChannel getChannel() {
		return channel;
	}

	public void setChannel(NationMessageChannel channel) {
		this.channel = channel;
	}

	public NationMessageChannel getMessageChannel() {
>>>>>>> Stashed changes
		return channel;
	}

	public War getCurrentWar() {
		return currentWar;
	}

	public void setCurrentWar(War currentWar) {
		this.currentWar = currentWar;
	}
}
