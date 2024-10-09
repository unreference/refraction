package com.github.unreference.refraction.domain.model;

import com.github.unreference.refraction.util.FormatUtil;
import java.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum Rank {
  PLAYER("player", true),

  ULTRA(
      "ultra",
      "&b&lUltra",
      PLAYER,
      true,
      "The first step into the fantastical stores of the mist that just might turn out to be true.\n\n"
          + "The first purchasable rank in the shop!"),
  HERO(
      "hero",
      "&d&lHero",
      ULTRA,
      true,
      "There are many stories of a person who was brave enough to tame the most fearsome dragon in the"
          + "land.\n\nThe second purchasable rank in the shop!"),
  LEGEND(
      "legend",
      "&a&lLegend",
      HERO,
      true,
      "For years, many have scoffed at the existence of these begins, only for it to turn out to be "
          + "true.\n\nThe third purchasable rank at the shop!"),
  TITAN(
      "titan",
      "&c&lTitan",
      LEGEND,
      true,
      "Ancient myths have spoken of these gigantic beings with unfathomable power.\n\n"
          + "The fourth purchasable rank in the shop!"),
  ETERNAL(
      "eternal",
      "&3Eternal",
      TITAN,
      true,
      "Fantastic and magical, no one except the Time Lords themselves truly understand the power"
          + "of these individuals.\n\nThe fifth purchasable rank in the shop!"),

  IMMORTAL(
      "immortal",
      "&e&lImmortal",
      ETERNAL,
      true,
      "Everlasting beings that are said to have witnessed the birth of the universe. It's said that these "
          + "individuals can control time itself.\n\nA subscription-based rank purchasable in the shop!"),

  STREAM(
      "stream",
      "&5&lStream",
      IMMORTAL,
      true,
      "A streamer who often features the server on their stream."),
  YT(
      "yt",
      "&5&lYT",
      STREAM,
      true,
      "A YouTuber who creates content for or related to the server. They have fewer subscribers than"
          + "full YouTubers."),
  YOUTUBE(
      "youtube",
      "&c&lYouTube",
      YT,
      true,
      "A YouTuber who creates content for or related to the server."),

  BUILDER(
      "builder",
      "&9&lBuilder",
      ETERNAL,
      true,
      "These creative staff members help build maps for your favorite games!"),
  BUILD_LEAD(
      "build_lead",
      "&9&lBuildLead",
      BUILDER,
      true,
      "Build Leads are leaders of the Build Team. They oversee the creation of new maps and manage"
          + "Builders."),

  TRAINEE(
      "trainee",
      "&6&lTrainee",
      BUILD_LEAD,
      true,
      "Trainees are Moderators in training. Their duties include enforcing the rules and providing help"
          + "to anybody with questions or concerns.\n\nFor assistance, you can contact them using &e/a <message>&7."),
  MOD(
      "mod",
      "&6&lMod",
      TRAINEE,
      true,
      "Moderators enforce rules and provide assistance to anybody with questions or concerns.\n\n"
          + "For assistance, contact them using &e/a <message>&7."),
  SR_MOD(
      "sr_mod",
      "&6&lSr.Mod",
      MOD,
      true,
      "Senior Moderators are members of a special staff team whose duties include fulfilling "
          + "specific tasks such as community or staff management. Just like a Moderator, you can always ask them "
          + "for help!\n\nFor assistance, contact them using &e/a <message>&7."),

  SUPPORT(
      "support",
      "&9&lSupport",
      SR_MOD,
      true,
      "Support members handle tickets and provide customer service."),
  ADMIN(
      "admin",
      "&4&lAdmin",
      SUPPORT,
      true,
      "Administrators are leaders of their respective Senior Moderator teams."),
  DEV(
      "dev",
      "&4&lDev",
      ADMIN,
      true,
      "Developers work behind the scenes to create new games and features. "
          + "They also fix bugs to provide you with the best experience!"),
  LEADER(
      "leader",
      "&4&lLeader",
      DEV,
      true,
      "Leaders manage the operation of their respective team or projects within the staff, development, or "
          + "management team."),

  OWNER(
      "owner",
      "&4&lOwner",
      LEADER,
      true,
      "Owners are the core managers of Mineplex. Each owner manages a different aspect of the server to "
          + "ensure its efficient operation."),

  RC("rc", false),
  STMA("stma", false),
  STM("stm", STMA, false),
  QAT("qat", false),
  QA("qa", QAT, false),
  QAL("qal", QA, false);

  private final String id;
  private final String prefix;
  private final Rank parent;
  private final boolean isPrimary;
  private final String description;

  private final Map<String, RankPermission> grantedPermissions;
  private final Set<String> revokedPermissions;

  Rank(String id, boolean isPrimary) {
    this.id = id;
    this.prefix = null;
    this.parent = null;
    this.isPrimary = isPrimary;
    this.description = null;
    this.grantedPermissions = new HashMap<>();
    this.revokedPermissions = new HashSet<>();
  }

  Rank(String id, Rank parent, boolean isPrimary) {
    this.id = id;
    this.prefix = null;
    this.parent = parent;
    this.isPrimary = isPrimary;
    this.description = null;
    this.grantedPermissions = new HashMap<>();
    this.revokedPermissions = new HashSet<>();
  }

  Rank(String id, String prefix, Rank parent, boolean isPrimary, String description) {
    this.id = id;
    this.prefix = prefix;
    this.parent = parent;
    this.isPrimary = isPrimary;
    this.description = description;
    this.grantedPermissions = new HashMap<>();
    this.revokedPermissions = new HashSet<>();
  }

  Rank(String id, String prefix, Rank parent, boolean isPrimary) {
    this.id = id;
    this.prefix = prefix;
    this.parent = parent;
    this.isPrimary = isPrimary;
    this.description = null;
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

  public static Component getFormattedList(List<Rank> ranks) {
    Component ranksComponent = Component.empty().colorIfAbsent(NamedTextColor.GRAY);

    for (int i = 0; i < ranks.size(); i++) {
      Rank rank = ranks.get(i);

      ranksComponent = ranksComponent.append(Component.text(rank.getId(), NamedTextColor.YELLOW));

      if (i < ranks.size() - 1) {
        ranksComponent = ranksComponent.append(Component.text(", "));
      }
    }

    return ranksComponent;
  }

  public String getId() {
    return id;
  }

  public void grantPermission(String permission, boolean isInheritable) {
    revokedPermissions.remove(permission);
    grantedPermissions.put(permission, new RankPermission(isInheritable));
  }

  public void revokePermission(String permission) {
    grantedPermissions.remove(permission);
    revokedPermissions.add(permission);
  }

  public boolean isPermitted(String permission) {
    // 1. Check if the permission is explicitly revoked for this rank
    if (revokedPermissions.contains(permission)) {
      return false; // Explicitly revoked permission
    }

    // 2. Check if the permission is explicitly granted for this rank
    RankPermission perm = grantedPermissions.get(permission);
    if (perm != null) {
      return true; // Explicitly granted permission
    }

    // 3. Check if permission is inherited from parent (recursively)
    Rank currentRank = this;
    while (currentRank.parent != null) {
      currentRank = currentRank.parent;
      if (currentRank.revokedPermissions.contains(permission)) {
        return false; // Permission explicitly revoked in parent
      }

      RankPermission parentPerm = currentRank.grantedPermissions.get(permission);
      if (parentPerm != null && parentPerm.isInheritable()) {
        return true; // Inherited permission
      }
    }

    // 4. If not explicitly granted, revoked, or inherited, return false
    return false;
  }

  public String getPrefix() {
    return prefix;
  }

  public Component getFormattedPrefix() {
    if (prefix != null) {
      return FormatUtil.getFormattedComponent(prefix);
    }

    return Component.empty();
  }

  public boolean isPrimary() {
    return isPrimary;
  }

  public String getDescription() {
    return description;
  }
}
