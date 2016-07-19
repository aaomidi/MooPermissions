package com.aaomidi.moopermissions.commands.othercommands;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.model.commands.MOtherCommand;
import com.aaomidi.moopermissions.model.perms.player.MPlayer;
import com.aaomidi.moopermissions.utils.StringManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amir on 2015-12-26.
 */
public class ImportCommand extends MOtherCommand {
	private final Pattern groupAddPattern = Pattern.compile("[\\w ]+group (\\w+) add (\\w+)\\/(\\w+)");
	private final Pattern permAddPattern = Pattern.compile("\\w+ player (\\w+)\\/(\\w+) set ([\\w.]+) (\\w+)");
	private final Pattern uuidPattern = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

	public ImportCommand(String name, String description, String... aliases) {
		super(name, description, aliases);
	}

	@Override
	public boolean execute(MooPermissions instance, String[] args, CommandSender commandSender) {
		if (args.length == 0) return false;

		String fileName = args[0];

		File file = new File(instance.getDataFolder(), fileName);

		if (!file.exists() || !file.isFile()) {
			StringManager.sendMessage(commandSender, "&cThat file was not recognized.");
		}

		try {
			List<String> fileContent = Files.readAllLines(file.toPath());

			for (String s : fileContent) {
				if (s.startsWith("#")) continue;
				if (!s.contains("set") || !s.contains("add")) continue;

				Matcher groupAddMatcher = groupAddPattern.matcher(s);
				Matcher permAddMatcher = permAddPattern.matcher(s);

				String variable;
				Boolean helperVariable = null;
				String uuidString;
				String playerName;

				if (groupAddMatcher.matches()) {
					variable = groupAddMatcher.group(1);
					uuidString = groupAddMatcher.group(2);
					playerName = groupAddMatcher.group(3);
				} else if (permAddMatcher.matches()) {
					variable = permAddMatcher.group(3);
					uuidString = permAddMatcher.group(1);
					playerName = permAddMatcher.group(2);
					String booleanValue = permAddMatcher.group(4);
					helperVariable = Boolean.valueOf(booleanValue);
				} else {
					continue;
				}

				Matcher uuidMatcher = uuidPattern.matcher(uuidString);
				if (!uuidMatcher.matches()) {
					StringManager.log(Level.WARNING, "Did not match 2.");
					StringManager.log(Level.WARNING, uuidString);
					continue;
				}
				String correctUUID = uuidMatcher.replaceAll("$1-$2-$3-$4-$5");
				UUID uuid = UUID.fromString(correctUUID);
				MPlayer mplayer = instance.getMySQL().initPlayer(new Player() {
					@Override
					public AttributeInstance getAttribute(Attribute attribute) {
						return null;
					}

					@Override
					public void sendRawMessage(String s) {

					}

					@Override
					public void kickPlayer(String s) {

					}

					@Override
					public void chat(String s) {

					}

					@Override
					public boolean performCommand(String s) {
						return false;
					}

					@Override
					public void saveData() {

					}

					@Override
					public void loadData() {

					}

					@Override
					public void playNote(Location location, byte b, byte b1) {

					}

					@Override
					public void playNote(Location location, Instrument instrument, Note note) {

					}

					@Override
					public void playSound(Location location, Sound sound, float v, float v1) {

					}

					@Override
					public void playSound(Location location, String s, float v, float v1) {

					}

					@Override
					public void stopSound(Sound sound) {

					}

					@Override
					public void stopSound(String s) {

					}

					@Override
					public void playEffect(Location location, Effect effect, int i) {

					}

					@Override
					public <T> void playEffect(Location location, Effect effect, T t) {

					}

					@Override
					public void sendBlockChange(Location location, Material material, byte b) {

					}

					@Override
					public boolean sendChunkChange(Location location, int i, int i1, int i2, byte[] bytes) {
						return false;
					}

					@Override
					public void sendBlockChange(Location location, int i, byte b) {

					}

					@Override
					public void sendSignChange(Location location, String[] strings) throws IllegalArgumentException {

					}

					@Override
					public void sendMap(MapView mapView) {

					}

					@Override
					public void updateInventory() {

					}

					@Override
					public void awardAchievement(Achievement achievement) {

					}

					@Override
					public void removeAchievement(Achievement achievement) {

					}

					@Override
					public boolean hasAchievement(Achievement achievement) {
						return false;
					}

					@Override
					public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {

					}

					@Override
					public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {

					}

					@Override
					public void incrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {

					}

					@Override
					public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {

					}

					@Override
					public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException {

					}

					@Override
					public int getStatistic(Statistic statistic) throws IllegalArgumentException {
						return 0;
					}

					@Override
					public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {

					}

					@Override
					public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {

					}

					@Override
					public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
						return 0;
					}

					@Override
					public void incrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {

					}

					@Override
					public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {

					}

					@Override
					public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {

					}

					@Override
					public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {

					}

					@Override
					public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {

					}

					@Override
					public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
						return 0;
					}

					@Override
					public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException {

					}

					@Override
					public void decrementStatistic(Statistic statistic, EntityType entityType, int i) {

					}

					@Override
					public void setStatistic(Statistic statistic, EntityType entityType, int i) {

					}

					@Override
					public void setPlayerTime(long l, boolean b) {

					}

					@Override
					public void resetPlayerTime() {

					}

					@Override
					public void resetPlayerWeather() {

					}

					@Override
					public void giveExp(int i) {

					}

					@Override
					public void giveExpLevels(int i) {

					}

					@Override
					public void setBedSpawnLocation(Location location, boolean b) {

					}

					@Override
					public void hidePlayer(Player player) {

					}

					@Override
					public void showPlayer(Player player) {

					}

					@Override
					public boolean canSee(Player player) {
						return false;
					}

					@Override
					public void sendTitle(String s, String s1) {

					}

					@Override
					public void resetTitle() {

					}

					@Override
					public void spawnParticle(Particle particle, Location location, int i) {

					}

					@Override
					public void spawnParticle(Particle particle, double v, double v1, double v2, int i) {

					}

					@Override
					public <T> void spawnParticle(Particle particle, Location location, int i, T t) {

					}

					@Override
					public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, T t) {

					}

					@Override
					public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2) {

					}

					@Override
					public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5) {

					}

					@Override
					public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, T t) {

					}

					@Override
					public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, T t) {

					}

					@Override
					public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3) {

					}

					@Override
					public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6) {

					}

					@Override
					public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3, T t) {

					}

					@Override
					public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, T t) {

					}

					@Override
					public Spigot spigot() {
						return null;
					}

					@Override
					public Map<String, Object> serialize() {
						return null;
					}

					@Override
					public void acceptConversationInput(String s) {

					}

					@Override
					public boolean beginConversation(Conversation conversation) {
						return false;
					}

					@Override
					public void abandonConversation(Conversation conversation) {

					}

					@Override
					public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {

					}

					@Override
					public boolean setWindowProperty(InventoryView.Property property, int i) {
						return false;
					}

					@Override
					public InventoryView openInventory(Inventory inventory) {
						return null;
					}

					@Override
					public InventoryView openWorkbench(Location location, boolean b) {
						return null;
					}

					@Override
					public InventoryView openEnchanting(Location location, boolean b) {
						return null;
					}

					@Override
					public void openInventory(InventoryView inventoryView) {

					}

					@Override
					public InventoryView openMerchant(Villager villager, boolean b) {
						return null;
					}

					@Override
					public void closeInventory() {

					}

					@Override
					public double getEyeHeight(boolean b) {
						return 0;
					}

					@Override
					public List<Block> getLineOfSight(HashSet<Byte> hashSet, int i) {
						return null;
					}

					@Override
					public List<Block> getLineOfSight(Set<Material> set, int i) {
						return null;
					}

					@Override
					public Block getTargetBlock(HashSet<Byte> hashSet, int i) {
						return null;
					}

					@Override
					public Block getTargetBlock(Set<Material> set, int i) {
						return null;
					}

					@Override
					public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hashSet, int i) {
						return null;
					}

					@Override
					public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
						return null;
					}

					@Override
					public int _INVALID_getLastDamage() {
						return 0;
					}

					@Override
					public void _INVALID_setLastDamage(int i) {

					}

					@Override
					public boolean addPotionEffect(PotionEffect potionEffect) {
						return false;
					}

					@Override
					public boolean addPotionEffect(PotionEffect potionEffect, boolean b) {
						return false;
					}

					@Override
					public boolean addPotionEffects(Collection<PotionEffect> collection) {
						return false;
					}

					@Override
					public boolean hasPotionEffect(PotionEffectType potionEffectType) {
						return false;
					}

					@Override
					public void removePotionEffect(PotionEffectType potionEffectType) {

					}

					@Override
					public boolean hasLineOfSight(Entity entity) {
						return false;
					}

					@Override
					public boolean setLeashHolder(Entity entity) {
						return false;
					}

					@Override
					public boolean hasAI() {
						return false;
					}

					@Override
					public void damage(double v) {

					}

					@Override
					public void _INVALID_damage(int i) {

					}

					@Override
					public void damage(double v, Entity entity) {

					}

					@Override
					public void _INVALID_damage(int i, Entity entity) {

					}

					@Override
					public int _INVALID_getHealth() {
						return 0;
					}

					@Override
					public void _INVALID_setHealth(int i) {

					}

					@Override
					public int _INVALID_getMaxHealth() {
						return 0;
					}

					@Override
					public void _INVALID_setMaxHealth(int i) {

					}

					@Override
					public void resetMaxHealth() {

					}

					@Override
					public Location getLocation(Location location) {
						return null;
					}

					@Override
					public boolean teleport(Location location) {
						return false;
					}

					@Override
					public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
						return false;
					}

					@Override
					public boolean teleport(Entity entity) {
						return false;
					}

					@Override
					public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause) {
						return false;
					}

					@Override
					public List<Entity> getNearbyEntities(double v, double v1, double v2) {
						return null;
					}

					@Override
					public void remove() {

					}

					@Override
					public boolean setPassenger(Entity entity) {
						return false;
					}

					@Override
					public boolean eject() {
						return false;
					}

					@Override
					public void playEffect(EntityEffect entityEffect) {

					}

					@Override
					public boolean leaveVehicle() {
						return false;
					}

					@Override
					public boolean hasGravity() {
						return false;
					}

					@Override
					public void sendMessage(String s) {

					}

					@Override
					public void sendMessage(String[] strings) {

					}

					@Override
					public void setMetadata(String s, MetadataValue metadataValue) {

					}

					@Override
					public List<MetadataValue> getMetadata(String s) {
						return null;
					}

					@Override
					public boolean hasMetadata(String s) {
						return false;
					}

					@Override
					public void removeMetadata(String s, Plugin plugin) {

					}

					@Override
					public boolean hasPlayedBefore() {
						return false;
					}

					@Override
					public boolean isPermissionSet(String s) {
						return false;
					}

					@Override
					public boolean isPermissionSet(Permission permission) {
						return false;
					}

					@Override
					public boolean hasPermission(String s) {
						return false;
					}

					@Override
					public boolean hasPermission(Permission permission) {
						return false;
					}

					@Override
					public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
						return null;
					}

					@Override
					public PermissionAttachment addAttachment(Plugin plugin) {
						return null;
					}

					@Override
					public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
						return null;
					}

					@Override
					public PermissionAttachment addAttachment(Plugin plugin, int i) {
						return null;
					}

					@Override
					public void removeAttachment(PermissionAttachment permissionAttachment) {

					}

					@Override
					public void recalculatePermissions() {

					}

					@Override
					public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) {

					}

					@Override
					public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
						return null;
					}

					@Override
					public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
						return null;
					}

					@Override
					public void setTexturePack(String s) {

					}

					@Override
					public void setResourcePack(String s) {

					}

					@Override
					public void setAI(boolean b) {

					}

					@Override
					public void setGravity(boolean b) {

					}

					@Override
					public String getDisplayName() {
						return null;
					}

					@Override
					public void setDisplayName(String s) {

					}

					@Override
					public String getPlayerListName() {
						return null;
					}

					@Override
					public void setPlayerListName(String s) {

					}

					@Override
					public Location getCompassTarget() {
						return null;
					}

					@Override
					public void setCompassTarget(Location location) {

					}

					@Override
					public InetSocketAddress getAddress() {
						return null;
					}

					@Override
					public boolean isSneaking() {
						return false;
					}

					@Override
					public void setSneaking(boolean b) {

					}

					@Override
					public boolean isSprinting() {
						return false;
					}

					@Override
					public void setSprinting(boolean b) {

					}

					@Override
					public boolean isSleepingIgnored() {
						return false;
					}

					@Override
					public void setSleepingIgnored(boolean b) {

					}

					@Override
					public long getPlayerTime() {
						return 0;
					}

					@Override
					public long getPlayerTimeOffset() {
						return 0;
					}

					@Override
					public boolean isPlayerTimeRelative() {
						return false;
					}

					@Override
					public WeatherType getPlayerWeather() {
						return null;
					}

					@Override
					public void setPlayerWeather(WeatherType weatherType) {

					}

					@Override
					public float getExp() {
						return 0;
					}

					@Override
					public void setExp(float v) {

					}

					@Override
					public int getLevel() {
						return 0;
					}

					@Override
					public void setLevel(int i) {

					}

					@Override
					public int getTotalExperience() {
						return 0;
					}

					@Override
					public void setTotalExperience(int i) {

					}

					@Override
					public float getExhaustion() {
						return 0;
					}

					@Override
					public void setExhaustion(float v) {

					}

					@Override
					public float getSaturation() {
						return 0;
					}

					@Override
					public void setSaturation(float v) {

					}

					@Override
					public int getFoodLevel() {
						return 0;
					}

					@Override
					public void setFoodLevel(int i) {

					}

					@Override
					public Location getBedSpawnLocation() {
						return null;
					}

					@Override
					public void setBedSpawnLocation(Location location) {

					}

					@Override
					public boolean getAllowFlight() {
						return false;
					}

					@Override
					public void setAllowFlight(boolean b) {

					}

					@Override
					public boolean isOnGround() {
						return false;
					}

					@Override
					public boolean isFlying() {
						return false;
					}

					@Override
					public void setFlying(boolean b) {

					}

					@Override
					public float getFlySpeed() {
						return 0;
					}

					@Override
					public void setFlySpeed(float v) throws IllegalArgumentException {

					}

					@Override
					public float getWalkSpeed() {
						return 0;
					}

					@Override
					public void setWalkSpeed(float v) throws IllegalArgumentException {

					}

					@Override
					public Scoreboard getScoreboard() {
						return null;
					}

					@Override
					public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {

					}

					@Override
					public boolean isHealthScaled() {
						return false;
					}

					@Override
					public void setHealthScaled(boolean b) {

					}

					@Override
					public double getHealthScale() {
						return 0;
					}

					@Override
					public void setHealthScale(double v) throws IllegalArgumentException {

					}

					@Override
					public Entity getSpectatorTarget() {
						return null;
					}

					@Override
					public void setSpectatorTarget(Entity entity) {

					}

					@Override
					public boolean isConversing() {
						return false;
					}

					@Override
					public String getName() {
						return playerName;
					}

					@Override
					public PlayerInventory getInventory() {
						return null;
					}

					@Override
					public Inventory getEnderChest() {
						return null;
					}

					@Override
					public MainHand getMainHand() {
						return null;
					}

					@Override
					public InventoryView getOpenInventory() {
						return null;
					}

					@Override
					public ItemStack getItemInHand() {
						return null;
					}

					@Override
					public void setItemInHand(ItemStack itemStack) {

					}

					@Override
					public ItemStack getItemOnCursor() {
						return null;
					}

					@Override
					public void setItemOnCursor(ItemStack itemStack) {

					}

					@Override
					public boolean isSleeping() {
						return false;
					}

					@Override
					public int getSleepTicks() {
						return 0;
					}

					@Override
					public GameMode getGameMode() {
						return null;
					}

					@Override
					public void setGameMode(GameMode gameMode) {

					}

					@Override
					public boolean isBlocking() {
						return false;
					}

					@Override
					public int getExpToLevel() {
						return 0;
					}

					@Override
					public double getEyeHeight() {
						return 0;
					}

					@Override
					public Location getEyeLocation() {
						return null;
					}

					@Override
					public int getRemainingAir() {
						return 0;
					}

					@Override
					public void setRemainingAir(int i) {

					}

					@Override
					public int getMaximumAir() {
						return 0;
					}

					@Override
					public void setMaximumAir(int i) {

					}

					@Override
					public int getMaximumNoDamageTicks() {
						return 0;
					}

					@Override
					public void setMaximumNoDamageTicks(int i) {

					}

					@Override
					public double getLastDamage() {
						return 0;
					}

					@Override
					public void setLastDamage(double v) {

					}

					@Override
					public int getNoDamageTicks() {
						return 0;
					}

					@Override
					public void setNoDamageTicks(int i) {

					}

					@Override
					public Player getKiller() {
						return null;
					}

					@Override
					public Collection<PotionEffect> getActivePotionEffects() {
						return null;
					}

					@Override
					public boolean getRemoveWhenFarAway() {
						return false;
					}

					@Override
					public void setRemoveWhenFarAway(boolean b) {

					}

					@Override
					public EntityEquipment getEquipment() {
						return null;
					}

					@Override
					public boolean getCanPickupItems() {
						return false;
					}

					@Override
					public void setCanPickupItems(boolean b) {

					}

					@Override
					public boolean isLeashed() {
						return false;
					}

					@Override
					public Entity getLeashHolder() throws IllegalStateException {
						return null;
					}

					@Override
					public boolean isGliding() {
						return false;
					}

					@Override
					public void setGliding(boolean b) {

					}

					@Override
					public boolean isCollidable() {
						return false;
					}

					@Override
					public void setCollidable(boolean b) {

					}

					@Override
					public double getHealth() {
						return 0;
					}

					@Override
					public void setHealth(double v) {

					}

					@Override
					public double getMaxHealth() {
						return 0;
					}

					@Override
					public void setMaxHealth(double v) {

					}

					@Override
					public Location getLocation() {
						return null;
					}

					@Override
					public Vector getVelocity() {
						return null;
					}

					@Override
					public void setVelocity(Vector vector) {

					}

					@Override
					public World getWorld() {
						return null;
					}

					@Override
					public int getEntityId() {
						return 0;
					}

					@Override
					public int getFireTicks() {
						return 0;
					}

					@Override
					public void setFireTicks(int i) {

					}

					@Override
					public int getMaxFireTicks() {
						return 0;
					}

					@Override
					public boolean isDead() {
						return false;
					}

					@Override
					public boolean isValid() {
						return false;
					}

					@Override
					public Server getServer() {
						return null;
					}

					@Override
					public Entity getPassenger() {
						return null;
					}

					@Override
					public boolean isEmpty() {
						return false;
					}

					@Override
					public float getFallDistance() {
						return 0;
					}

					@Override
					public void setFallDistance(float v) {

					}

					@Override
					public EntityDamageEvent getLastDamageCause() {
						return null;
					}

					@Override
					public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {

					}

					@Override
					public UUID getUniqueId() {
						return uuid;
					}

					@Override
					public int getTicksLived() {
						return 0;
					}

					@Override
					public void setTicksLived(int i) {

					}

					@Override
					public EntityType getType() {
						return null;
					}

					@Override
					public boolean isInsideVehicle() {
						return false;
					}

					@Override
					public Entity getVehicle() {
						return null;
					}

					@Override
					public String getCustomName() {
						return null;
					}

					@Override
					public void setCustomName(String s) {

					}

					@Override
					public boolean isCustomNameVisible() {
						return false;
					}

					@Override
					public void setCustomNameVisible(boolean b) {

					}

					@Override
					public boolean isGlowing() {
						return false;
					}

					@Override
					public void setGlowing(boolean b) {

					}

					@Override
					public boolean isInvulnerable() {
						return false;
					}

					@Override
					public void setInvulnerable(boolean b) {

					}

					@Override
					public boolean isSilent() {
						return false;
					}

					@Override
					public void setSilent(boolean b) {

					}

					@Override
					public boolean isOnline() {
						return false;
					}

					@Override
					public boolean isBanned() {
						return false;
					}

					@Override
					public void setBanned(boolean b) {

					}

					@Override
					public boolean isWhitelisted() {
						return false;
					}

					@Override
					public void setWhitelisted(boolean b) {

					}

					@Override
					public Player getPlayer() {
						return null;
					}

					@Override
					public long getFirstPlayed() {
						return 0;
					}

					@Override
					public long getLastPlayed() {
						return 0;
					}

					@Override
					public Set<PermissionAttachmentInfo> getEffectivePermissions() {
						return null;
					}

					@Override
					public Set<String> getListeningPluginChannels() {
						return null;
					}

					@Override
					public boolean isOp() {
						return false;
					}

					@Override
					public void setOp(boolean b) {

					}
				});

				if (mplayer == null) {
					StringManager.log(Level.WARNING, "Player was null");
					continue;
				}
				if (helperVariable == null) {
					mplayer.addToGroup(variable, 0);
				} else {
					mplayer.addPermissionToPlayer(variable, helperVariable, 0);
				}
			}
			StringManager.sendMessage(commandSender, "&bSuccessfully imported permissions!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}
}
