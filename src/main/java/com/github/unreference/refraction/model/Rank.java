package com.github.unreference.refraction.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.text.format.NamedTextColor;

public enum Rank {
  DEFAULT("default"),

  // A first step into the fantastical stories of the mist\n
  // that just might turn out to be true.\n\n
  // The first purchasable rank in the Minexplex shop!
  ULTRA("ultra", "Ultra", NamedTextColor.AQUA, DEFAULT, true),

  // There are many stories of a person who was brave enough\n
  // to tame the most fearsome dragon in the land.\n\n
  // The second purchasable rank in the Mineplex shop!
  HERO("hero", "Hero", NamedTextColor.LIGHT_PURPLE, ULTRA, true),

  // For years, many have scoffed at the existence of\n
  // these beings, only for them to become true.\n\n
  // The third purchasable rank at the Mineplex shop!
  LEGEND("legend", "Legend", NamedTextColor.GREEN, HERO, true),

  // Ancient myths have spoken of these gigantic\n
  // beings with unfathomable power.\n\n
  // The fourth purchasable rank in the Mineplex shop!
  TITAN("titan", "Titan", NamedTextColor.RED, LEGEND, true),

  // Fantastic and magical, no one except the time lords\n
  // truly understand the power of these individuals.\n\n
  // The fifth purchasable rank in the Mineplex shop!
  ETERNAL("eternal", "Eternal", NamedTextColor.DARK_AQUA, TITAN, true),

  // Everlasting beings that are said to have\n
  // witnessed the birth of the universe.\n
  // It's said they can control time itself.\n\n
  // A subscription-based rank purchasable in the Mineplex shop!
  IMMORTAL("immortal", "Immortal", NamedTextColor.YELLOW, ETERNAL, true),

  // A streamer who often features Mineplex\n
  // on their Twitch, Beam, or YouTube stream.
  STREAM("stream", "Stream", NamedTextColor.DARK_PURPLE, IMMORTAL, true),

  // A YouTuber who creates content for\nor related to Mineplex.\n\n
  // They have fewer subscribers than full YouTubers.
  YT("yt", "YT", NamedTextColor.DARK_PURPLE, STREAM, true),

  // A YouTuber who creates content for\n
  // or related to Mineplex.
  YOUTUBE("youtube", "YouTube", NamedTextColor.RED, YT, true),

  // These creative staff members help\n
  // build maps for your favorite games!
  BUILDER("builder", "Builder", NamedTextColor.BLUE, ETERNAL, true),

  // Build Leads are leaders of the Mineplex Build Team.\n
  // They oversee the creation of new maps and manage Builders.
  BUILD_LEAD("build_lead", "BuildLead", NamedTextColor.BLUE, DEFAULT, true),

  // Trainees are Moderators in training.\n
  // Their duties include enforcing the rules\n
  // and providing help to anyone\n
  // with questions or concerns.\n\n
  // For assistance, contact them using &e/a <message>.
  TRAINEE("trainee", "Trainee", NamedTextColor.GOLD, BUILD_LEAD, true),

  // Moderators enforce rules and provide help to\n
  // anyone with questions or concerns.\n\n
  // For assistance, contact them using &e/a <message>.
  MOD("mod", "Mod", NamedTextColor.GOLD, TRAINEE, true),

  // Senior Moderators are members of a special staff team\n
  // whose duties include fulfilling specific tasks such as\n
  // community or staff management.\n
  // Just like Moderators, you can always ask them for help!\n\n
  // For assistance, contact them using &e/a <message>.
  SR_MOD("sr_mod", "Sr.Mod", NamedTextColor.GOLD, MOD, true),

  // Support members handle tickets and\n
  // provide customer service.
  SUPPORT("support", "Support", NamedTextColor.BLUE, SR_MOD, true),

  // Administrators are leaders of their respective\n
  // Senior Moderator teams.
  ADMIN("admin", "Admin", NamedTextColor.DARK_RED, SUPPORT, true),

  // Developers work behind the scenes to create new\n
  // games and features.\n
  // They also fix bugs to provide you\n
  // with the best experience!
  DEV("dev", "Dev", NamedTextColor.DARK_RED, ADMIN, true),

  // Leaders manage the operation of their respective\n
  // team or projects within the staff, development, or\n
  // management team.
  LEADER("leader", "Leader", NamedTextColor.DARK_RED, DEV, true),

  // Owners are the core managers of Mineplex.\n
  // Each Owner manages a different aspect of the\n
  // server and ensures its efficient operation.
  OWNER("owner", "Owner", NamedTextColor.DARK_RED, LEADER, true),

  RC("rc", false),
  CMA("cma", false),
  CM("cm", CMA, false),
  STMA("stma", false),
  STM("stm", STMA, false),
  QAT("qat", false),
  QA("qa", QAT, false),
  QAL("qal", QA, false);

  private final String id;
  private final String prefix;
  private final NamedTextColor prefixColor;
  private final Rank parent;
  private final boolean isPrimary;

  private final Map<String, RankPermission> grantedPermissions;
  private final Set<String> revokedPermissions;

  Rank(String id) {
    this.id = id;
    this.prefix = null;
    this.prefixColor = null;
    this.parent = null;
    this.isPrimary = true;
    this.grantedPermissions = new HashMap<>();
    this.revokedPermissions = new HashSet<>();
  }

  Rank(String id, boolean isPrimary) {
    this.id = id;
    this.prefix = null;
    this.prefixColor = null;
    this.parent = null;
    this.isPrimary = isPrimary;
    this.grantedPermissions = new HashMap<>();
    this.revokedPermissions = new HashSet<>();
  }

  Rank(String id, Rank parent, boolean isPrimary) {
    this.id = id;
    this.prefix = null;
    this.prefixColor = null;
    this.parent = parent;
    this.isPrimary = isPrimary;
    this.grantedPermissions = new HashMap<>();
    this.revokedPermissions = new HashSet<>();
  }

  Rank(String id, String prefix, NamedTextColor prefixColor, Rank parent, boolean isPrimary) {
    this.id = id;
    this.prefix = prefix;
    this.prefixColor = prefixColor;
    this.parent = parent;
    this.isPrimary = isPrimary;
    this.grantedPermissions = new HashMap<>();
    this.revokedPermissions = new HashSet<>();
  }

  public static String getId(Rank rank) {
    return rank.getId();
  }

  public static Rank getRankFromId(String id) {
    for (Rank rank : Rank.values()) {
      if (rank.getId().equalsIgnoreCase(id)) {
        return rank;
      }
    }

    return null;
  }

  public String getId() {
    return id;
  }

  public void grantPermission(String permission, boolean isInheritable) {
    grantedPermissions.put(permission, new RankPermission(isInheritable));
  }

  public void revokePermission(String permission) {
    grantedPermissions.remove(permission);
    revokedPermissions.add(permission);
  }

  public boolean isPermitted(String permission) {
    if (revokedPermissions.contains(permission)) {
      return false; // Explicitly revoked permission
    }

    RankPermission perm = grantedPermissions.get(permission);
    if (perm != null) {
      return true; // Explicitly granted permission
    }

    if (parent != null && parent.isPermitted(permission)) {
      RankPermission parentPermission = parent.grantedPermissions.get(permission);

      if (parentPermission != null && parentPermission.isInheritable()) {
        grantedPermissions.put(permission, parentPermission);
        return true; // Inherits granted permission
      }
    }

    return false;
  }

  public String getPrefix() {
    return prefix;
  }

  public NamedTextColor getPrefixColor() {
    return prefixColor;
  }

  public boolean isPrimary() {
    return isPrimary;
  }
}
