package com.github.unreference.refraction.domain.model;

import com.github.unreference.refraction.util.FormatUtil;
import com.github.unreference.refraction.util.MessageUtil;
import java.util.*;
import java.util.regex.Matcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum Rank {
  PLAYER("player", true),

  CELESTIAL(
      "celestial",
      "#FFFC66Celestial",
      PLAYER,
      true,
      "The embodiment of light and radiance, those who transcend the ordinary. The Celestial shine above all "
          + "others with unmatched brilliance.\n\nThe first purchasable rank in the shop!"),
  VANGUARD(
      "vanguard",
      "#00E2FCVanguard",
      CELESTIAL,
      true,
      "The first line of defense, blazing trails for others to follow. The Vanguard stand tall as protectors "
          + "and innovators in the face of adversity.\n\nThe second purchasable rank in the shop!"),
  MYTHIC(
      "mythic",
      "#FC6EFCMythic",
      VANGUARD,
      true,
      "Stories are told of these rare few whose deeds reshape history itself. The Mythic rise above legends "
          + "as those who leave an everlasting mark.\n\nThe third purchasable rank in the shop!"),
  COLOSSUS(
      "colossus",
      "#FC7657Colossus",
      MYTHIC,
      true,
      "Unyielding, colossal figures of power. The Colossus stand as immovable giants, representing unmatched "
          + "strength and resilience.\n\nThe fourth purchasable rank in the shop!"),
  ETHEREAL(
      "ethereal",
      "#3CFFB5Ethereal",
      COLOSSUS,
      true,
      "Transcending the mortal plane, the Ethereal live forever as luminous beings, guiding the future "
          + "with their eternal contributions.\n\nThe fifth purchasable rank in the shop!"),
  ASCENDANT(
      "ascendant",
      "#73FF60Ascendant",
      ETHEREAL,
      true,
      "The pinnacle of existence, having ascended beyond the stars themselves. The Ascendant are the "
          + "ultimate beings, forever etched in the fabric of time and space.\n\nA subscription-based rank, "
          + "purchasable in the shop!"),

  TWITCH(
      "twitch",
      "#7E45FBTwitch",
      ASCENDANT,
      true,
      "Twitch streamers broadcast live content, engaging the community and showcasing server events to "
          + "enhance player interaction."),
  TIKTOK(
      "tiktok",
      "#E84FA3TikTok",
      TWITCH,
      true,
      "TikTok creators produce and share engaging short-form videos, promoting the server and its "
          + "community to attract new players."),
  YOUTUBE(
      "youtube",
      "#FB0000YouTube",
      TIKTOK,
      true,
      "YouTube creators produce video content related to the server, increasing visibility and helping "
          + "to grow the player base."),

  BUILDER(
      "builder",
      "#FBA300Builder",
      ETHEREAL,
      true,
      "Builders design and construct maps and environments within the server, enhancing gameplay and "
          + "player experiences."),
  BUILD_LEAD(
      "build_lead",
      "#FBA300BuildLead",
      BUILDER,
      true,
      "Build Leads coordinate map creation efforts, ensuring high-quality designs and effective "
          + "collaboration among builders."),

  TRAINEE(
      "trainee",
      "#0EA7FCTrainee",
      BUILD_LEAD,
      true,
      "Trainees learn server moderation and community engagement practices, providing assistance under the "
          + "guidance of experienced staff.\n\nFor assistance, you can contact them using\n&e/a <message>&7."),
  MOD(
      "mod",
      "#0EA7FCMod",
      TRAINEE,
      true,
      "Moderators maintain order and enforce rules within the server, fostering a positive atmosphere and "
          + "ensuring player safety.\n\nFor assistance, contact them using\n&e/a <message>&7."),
  SR_MOD(
      "sr_mod",
      "#0EA7FCSr.Mod",
      MOD,
      true,
      "Senior Moderators are members of a specialized team and contribute to its efforts, enhancing server "
          + "management and community interaction through additional responsibilities.\n\nFor assistance, contact them "
          + "using\n&e/a <message>&7."),

  SUPPORT(
      "support",
      "#576BF0Support",
      SR_MOD,
      true,
      "Support members handle tickets and resolve issues related to purchases, ensuring a positive "
          + "shopping experience for players."),

  ADMIN(
      "admin",
      "#FB2057Admin",
      SUPPORT,
      true,
      "Administrators lead specialized Senior Moderator teams and manage operations to ensure efficient "
          + "server functionality and effective collaboration."),
  DEV(
      "dev",
      "#FB2057Dev",
      ADMIN,
      true,
      "Developers create and maintain server features, implementing technical solutions to enhance "
          + "gameplay and player experience."),
  LEADER(
      "leader",
      "#FB2057Leader",
      DEV,
      true,
      "Leaders oversee specific aspects of the server, ensuring effective communication and coordination "
          + "among team members."),
  OWNER(
      "owner",
      "#FB2057Owner",
      LEADER,
      true,
      "Owners hold ultimate authority over server management, guiding the overall direction and ensuring "
          + "a thriving community."),

  RC("rc", false),
  STMA("stma", false),
  STM("stm", STMA, false),
  QAT("qat", false),
  QA("qa", ASCENDANT, false),
  QAL("qal", QA, false);

  private final String id;
  private final Rank parent;
  private final boolean isPrimary;
  private final String description;
  private final Map<String, RankPermission> grantedPermissions;
  private final Set<String> revokedPermissions;
  private String prefix;

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

  public void setPrefix(String prefix) {
    this.prefix = prefix;
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

  public Component getRankWithHover() {
    if (prefix == null) {
      return Component.empty();
    }

    if (description == null) {
      return FormatUtil.toUpperCase(getFormattedPrefix()).decorate(TextDecoration.BOLD);
    }

    Component hoverContent =
        Component.text()
            .append(FormatUtil.toUpperCase(getFormattedPrefix()).decorate(TextDecoration.BOLD))
            .append(Component.newline())
            .append(MessageUtil.getMessage(description))
            .build();

    return FormatUtil.toUpperCase(getFormattedPrefix())
        .decorate(TextDecoration.BOLD)
        .hoverEvent(hoverContent);
  }

  public TextColor getRankColor() {
    Matcher hexMatcher = FormatUtil.hexColorPattern.matcher(this.prefix);
    if (hexMatcher.find()) {
      String hexColor = hexMatcher.group();
      return TextColor.fromHexString(hexColor);
    }

    Matcher legacyMatcher = FormatUtil.legacyColorPattern.matcher(this.prefix);
    if (legacyMatcher.find()) {
      String legacyCode = legacyMatcher.group();
      char legacyChar = legacyCode.charAt(1);
      NamedTextColor namedColor = FormatUtil.getColorFromLegacy(legacyChar);
      if (namedColor != null) {
        return namedColor;
      }
    }

    return NamedTextColor.WHITE;
  }
}
