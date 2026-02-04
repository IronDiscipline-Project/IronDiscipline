package com.irondiscipline.manager;

import com.irondiscipline.IronDiscipline;
import com.irondiscipline.model.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.util.UUID;

/**
 * Discord Bot マネージャー
 */
public class DiscordManager extends ListenerAdapter {

    private final IronDiscipline plugin;
    private JDA jda;
    private String notificationChannelId;
    private String guildId;
    private String unverifiedRoleId;
    private String verifiedRoleId;
    private String adminRoleId;
    private boolean enabled = false;

    // 寄付システム
    private int donationGoal = 5000; // 月間目標（円）
    private int donationCurrent = 0; // 現在の寄付額

    public DiscordManager(IronDiscipline plugin) {
        this.plugin = plugin;
    }

    /**
     * Botを起動
     */
    public boolean start(String botToken, String channelId, String guildId, String unverifiedRoleId,
            String verifiedRoleId, String adminRoleId) {
        if (botToken == null || botToken.isEmpty()) {
            plugin.getLogger().warning("Discord Bot Token が設定されていません");
            return false;
        }

        this.notificationChannelId = channelId;
        this.guildId = guildId;
        this.unverifiedRoleId = unverifiedRoleId;
        this.verifiedRoleId = verifiedRoleId;
        this.adminRoleId = adminRoleId;

        try {
            jda = JDABuilder.createDefault(botToken)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.playing("鉄の規律"))
                    .addEventListeners(this)
                    .build();

            // コマンド登録は onGuildReady で行う
            plugin.getLogger().info("Discord Bot ログイン完了");

            return true;

        } catch (Exception e) {
            plugin.getLogger().severe("Discord Bot 起動失敗: " + e.getMessage());
            return false;
        }
    }

    private boolean isAdmin(Member member) {
        if (member == null) return false;
        if (member.hasPermission(Permission.ADMINISTRATOR)) return true;
        if (adminRoleId != null && !adminRoleId.isEmpty()) {
            Role adminRole = member.getGuild().getRoleById(adminRoleId);
            return adminRole != null && member.getRoles().contains(adminRole);
        }
        return false;
    }

    @Override
    public void onGuildReady(net.dv8tion.jda.api.events.guild.GuildReadyEvent event) {
        String configGuildId = this.guildId;
        if (configGuildId != null && !configGuildId.isEmpty() && !event.getGuild().getId().equals(configGuildId)) {
            return; // 指定されたサーバー以外は無視
        }

        plugin.getLogger()
                .info("Guild Commands を登録中: " + event.getGuild().getName() + " (" + event.getGuild().getId() + ")");

        event.getGuild().updateCommands().addCommands(
                Commands.slash("link", "Minecraftアカウントと連携"),
                Commands.slash("unlink", "連携を解除"),
                Commands.slash("status", "サーバー状態を表示"),
                Commands.slash("players", "オンラインプレイヤー一覧"),
                Commands.slash("playtime", "勤務時間を確認"),
                Commands.slash("rank", "自分の階級を確認"),
                Commands.slash("warn", "プレイヤーに警告")
                        .addOption(OptionType.USER, "user", "対象ユーザー", true)
                        .addOption(OptionType.STRING, "reason", "理由", true),
                Commands.slash("announce", "ゲーム内アナウンス")
                        .addOption(OptionType.STRING, "message", "メッセージ", true),
                Commands.slash("donate", "サーバー運営費の寄付情報"),
                Commands.slash("setgoal", "寄付目標を設定（管理者）")
                        .addOption(OptionType.INTEGER, "goal", "月間目標金額（円）", true)
                        .addOption(OptionType.INTEGER, "current", "現在の寄付額（円）", true),

                // === New Commands ===
                Commands.slash("settings", "Bot設定の変更（管理者）")
                        .addOption(OptionType.STRING, "action", "操作 (set/get/role)", true)
                        .addOption(OptionType.STRING, "key", "設定キー or 階級名", false)
                        .addOption(OptionType.STRING, "value", "設定値 or ロールID", false),

                Commands.slash("panel", "機能パネルの設置（管理者）")
                        .addOption(OptionType.STRING, "type", "パネル種類 (auth/roles)", true),

                Commands.slash("division", "部隊管理（管理者）")
                        .addOption(OptionType.STRING, "action", "操作 (create/add/remove/list)", true)
                        .addOption(OptionType.STRING, "arg1", "引数1 (部隊名/ユーザー)", false)
                        .addOption(OptionType.STRING, "arg2", "引数2 (部隊名)", false),

                Commands.slash("promote", "昇進（管理者）")
                        .addOption(OptionType.USER, "user", "対象ユーザー", true),

                Commands.slash("demote", "降格（管理者）")
                        .addOption(OptionType.USER, "user", "対象ユーザー", true),

                Commands.slash("setrank", "階級指定（管理者）")
                        .addOption(OptionType.USER, "user", "対象ユーザー", true)
                        .addOption(OptionType.STRING, "rank", "階級ID", true),

                Commands.slash("kick", "キック（管理者）")
                        .addOption(OptionType.USER, "user", "対象ユーザー", true)
                        .addOption(OptionType.STRING, "reason", "理由", true),

                Commands.slash("ban", "BAN（管理者）")
                        .addOption(OptionType.USER, "user", "対象ユーザー", true)
                        .addOption(OptionType.STRING, "reason", "理由", true))
                .queue(
                        success -> plugin.getLogger().info("コマンド登録成功！ (" + success.size() + "個)"),
                        error -> plugin.getLogger().severe("コマンド登録失敗: " + error.getMessage()));
    }

    /**
     * Botを停止
     */
    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
            plugin.getLogger().info("Discord Bot 停止");
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String cmd = event.getName();

        switch (cmd) {
            case "link" -> handleLink(event);
            case "unlink" -> handleUnlink(event);
            case "status" -> handleStatus(event);
            case "players" -> handlePlayers(event);
            case "playtime" -> handlePlaytime(event);
            case "rank" -> handleRank(event);
            case "warn" -> handleWarn(event);
            case "announce" -> handleAnnounce(event);
            case "donate" -> handleDonate(event);
            case "setgoal" -> handleSetGoal(event);

            // New Handlers
            case "settings" -> handleSettings(event);
            case "panel" -> handlePanel(event);
            case "division" -> handleDivision(event);
            case "promote" -> handleAdminRank(event, true);
            case "demote" -> handleAdminRank(event, false);
            case "setrank" -> handleSetRank(event);
            case "kick" -> handlePunish(event, "kick");
            case "ban" -> handlePunish(event, "ban");
        }
    }

    private void handleLink(SlashCommandInteractionEvent event) {
        long discordId = event.getUser().getIdLong();

        if (plugin.getLinkManager().isLinked(discordId)) {
            event.reply("既に連携済みです。解除するには `/unlink` を使用してください。").setEphemeral(true).queue();
            return;
        }

        String code = plugin.getLinkManager().generateLinkCode(discordId);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("🔗 アカウント連携")
                .setDescription("Minecraft内で以下のコマンドを実行してください：")
                .addField("コマンド", "`/link " + code + "`", false)
                .addField("有効期限", "5分", false)
                .setColor(Color.BLUE)
                .setFooter("鉄の規律");

        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    private void handleUnlink(SlashCommandInteractionEvent event) {
        long discordId = event.getUser().getIdLong();

        if (plugin.getLinkManager().unlinkByDiscord(discordId)) {
            event.reply("✅ 連携を解除しました。").setEphemeral(true).queue();
        } else {
            event.reply("連携されていません。").setEphemeral(true).queue();
        }
    }

    private void handleStatus(SlashCommandInteractionEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            int online = Bukkit.getOnlinePlayers().size();
            int max = Bukkit.getMaxPlayers();
            int linked = plugin.getLinkManager().getLinkCount();

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("📊 サーバー状態")
                    .addField("オンライン", online + " / " + max, true)
                    .addField("連携済み", linked + "人", true)
                    .setColor(Color.GREEN)
                    .setFooter("鉄の規律");

            event.replyEmbeds(eb.build()).queue();
        });
    }

    private void handlePlayers(SlashCommandInteractionEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            StringBuilder sb = new StringBuilder();

            for (Player p : Bukkit.getOnlinePlayers()) {
                Rank rank = plugin.getRankManager().getRank(p);
                String div = plugin.getDivisionManager().getDivision(p.getUniqueId());
                String divDisplay = div != null ? plugin.getDivisionManager().getDivisionDisplayName(div) : "";

                sb.append("**").append(p.getName()).append("** - ")
                        .append(rank.getId()).append(" ").append(divDisplay).append("\n");
            }

            if (sb.length() == 0) {
                sb.append("オンラインプレイヤーなし");
            }

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("👥 オンラインプレイヤー")
                    .setDescription(sb.toString())
                    .setColor(Color.CYAN)
                    .setFooter("鉄の規律");

            event.replyEmbeds(eb.build()).queue();
        });
    }

    private void handlePlaytime(SlashCommandInteractionEvent event) {
        long discordId = event.getUser().getIdLong();
        UUID minecraftId = plugin.getLinkManager().getMinecraftId(discordId);

        if (minecraftId == null) {
            event.reply("アカウントが連携されていません。`/link` で連携してください。").setEphemeral(true).queue();
            return;
        }

        // Defer reply to prevent timeout and indicate processing
        event.deferReply(true).queue();

        Bukkit.getScheduler().runTask(plugin, () -> {
            String playtime = plugin.getPlaytimeManager().getFormattedPlaytime(minecraftId);
            String playerName = Bukkit.getOfflinePlayer(minecraftId).getName();

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("⏱️ 勤務時間")
                    .addField(playerName != null ? playerName : "Unknown", playtime, false)
                    .setColor(Color.ORANGE)
                    .setFooter("鉄の規律");

            event.getHook().editOriginalEmbeds(eb.build()).queue();
        });
    }

    private void handleRank(SlashCommandInteractionEvent event) {
        long discordId = event.getUser().getIdLong();
        UUID minecraftId = plugin.getLinkManager().getMinecraftId(discordId);

        if (minecraftId == null) {
            event.reply("アカウントが連携されていません。").setEphemeral(true).queue();
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(minecraftId);
            Rank rank = player != null ? plugin.getRankManager().getRank(player) : Rank.PRIVATE;
            String div = plugin.getDivisionManager().getDivision(minecraftId);

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("🎖️ 階級情報")
                    .addField("階級", rank.getId(), true)
                    .addField("部隊", div != null ? div : "なし", true)
                    .setColor(Color.YELLOW)
                    .setFooter("鉄の規律");

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
        });
    }

    private void handleWarn(SlashCommandInteractionEvent event) {
        var targetOption = event.getOption("user");
        var reasonOption = event.getOption("reason");

        if (targetOption == null || reasonOption == null) {
            event.reply("パラメータが不足しています。").setEphemeral(true).queue();
            return;
        }

        long targetDiscordId = targetOption.getAsUser().getIdLong();
        String reason = reasonOption.getAsString();

        UUID targetMinecraft = plugin.getLinkManager().getMinecraftId(targetDiscordId);
        if (targetMinecraft == null) {
            event.reply("対象ユーザーはMinecraftと連携されていません。").setEphemeral(true).queue();
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            Player target = Bukkit.getPlayer(targetMinecraft);
            if (target == null || !target.isOnline()) {
                event.reply("対象プレイヤーはオフラインです。").setEphemeral(true).queue();
                return;
            }

            // 警告実行
            plugin.getWarningManager().addWarning(targetMinecraft, target.getName(), reason, null).thenAccept(count -> {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    target.sendMessage("§c§l【警告】§r§c " + reason + " §7(警告" + count + "回目)");
                });
            });

            event.reply("✅ " + target.getName() + " に警告を与えました。理由: " + reason).queue();
        });
    }

    private void handleAnnounce(SlashCommandInteractionEvent event) {
        var msgOption = event.getOption("message");
        if (msgOption == null)
            return;

        String message = msgOption.getAsString();

        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle("§6§l【通達】", "§f" + message, 10, 100, 20);
                p.sendMessage("§6§l【Discord通達】§r §f" + message);
            }
        });

        event.reply("✅ アナウンスを送信しました: " + message).queue();
    }

    private void handleDonate(SlashCommandInteractionEvent event) {
        int percent = donationGoal > 0 ? (donationCurrent * 100 / donationGoal) : 0;
        if (percent > 100)
            percent = 100;

        // プログレスバー生成
        int bars = 20;
        int filled = (percent * bars) / 100;
        StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < bars; i++) {
            progressBar.append(i < filled ? "█" : "░");
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("💰 サーバー運営費")
                .setDescription("サーバー維持のためのご支援をお願いします！")
                .addField("月間目標", "¥" + String.format("%,d", donationGoal), true)
                .addField("現在の達成額", "¥" + String.format("%,d", donationCurrent), true)
                .addField("達成率", percent + "%", true)
                .addField("進捗", "`" + progressBar.toString() + "` " + percent + "%", false)
                .setColor(percent >= 100 ? Color.GREEN : (percent >= 50 ? Color.YELLOW : Color.RED))
                .setFooter("ご支援ありがとうございます！");

        // 寄付先情報があれば追加
        String info = plugin.getConfigManager().getDonationInfo();
        if (info != null && !info.isEmpty()) {
            eb.addField("寄付方法", info, false);
        }

        event.replyEmbeds(eb.build()).queue();
    }

    private void handleSetGoal(SlashCommandInteractionEvent event) {
        // 管理者権限チェック
        if (!isAdmin(event.getMember())) {
            event.reply("❌ このコマンドは管理者のみ使用可能です。").setEphemeral(true).queue();
            return;
        }

        var goalOption = event.getOption("goal");
        var currentOption = event.getOption("current");

        if (goalOption == null || currentOption == null) {
            event.reply("パラメータが不足しています。").setEphemeral(true).queue();
            return;
        }

        donationGoal = goalOption.getAsInt();
        donationCurrent = currentOption.getAsInt();

        int percent = donationGoal > 0 ? (donationCurrent * 100 / donationGoal) : 0;

        event.reply("✅ 寄付目標を更新しました！\n目標: ¥" + String.format("%,d", donationGoal) +
                " / 現在: ¥" + String.format("%,d", donationCurrent) + " (" + percent + "%)").queue();
    }

    // ===== 通知機能 =====

    /**
     * 通知チャンネルにメッセージ送信
     */
    public void sendNotification(String title, String message, Color color) {
        if (!enabled || jda == null || notificationChannelId == null || notificationChannelId.isEmpty()) {
            return;
        }

        TextChannel channel = jda.getTextChannelById(notificationChannelId);
        if (channel == null)
            return;

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(title)
                .setDescription(message)
                .setColor(color)
                .setTimestamp(java.time.Instant.now())
                .setFooter("鉄の規律");

        channel.sendMessageEmbeds(eb.build()).queue();
    }

    public void notifyJoin(Player player) {
        sendNotification("📥 参加", "**" + player.getName() + "** がサーバーに参加しました", Color.GREEN);
    }

    public void notifyQuit(Player player) {
        sendNotification("📤 退出", "**" + player.getName() + "** がサーバーから退出しました", Color.GRAY);
    }

    public void notifyWarning(String playerName, String reason, int count) {
        sendNotification("⚠️ 警告", "**" + playerName + "** に警告 (" + count + "回目)\n理由: " + reason, Color.ORANGE);
    }

    public void notifyJail(String playerName, String reason) {
        sendNotification("🔒 隔離", "**" + playerName + "** が隔離されました\n理由: " + reason, Color.RED);
    }

    public void notifyUnjail(String playerName) {
        sendNotification("🔓 釈放", "**" + playerName + "** が釈放されました", Color.GREEN);
    }

    public boolean isEnabled() {
        return enabled;
    }

    // ===== ロール管理 =====

    /**
     * Discordサーバーに参加した時に未認証ロールを付与
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (unverifiedRoleId == null || unverifiedRoleId.isEmpty())
            return;

        Role unverifiedRole = event.getGuild().getRoleById(unverifiedRoleId);
        if (unverifiedRole != null) {
            event.getGuild().addRoleToMember(event.getMember(), unverifiedRole).queue();
            plugin.getLogger().info("Discord: " + event.getUser().getName() + " に未認証ロールを付与");
        }
    }

    /**
     * 連携完了時に認証済みロールを付与し、ニックネームを変更
     */
    public void onLinkComplete(long discordId, String minecraftName, Rank rank) {
        if (!enabled || jda == null || guildId == null || guildId.isEmpty())
            return;

        Guild guild = jda.getGuildById(guildId);
        if (guild == null)
            return;

        guild.retrieveMemberById(discordId).queue(member -> {
            if (member == null)
                return;

            // 未認証ロールを削除
            if (unverifiedRoleId != null && !unverifiedRoleId.isEmpty()) {
                Role unverifiedRole = guild.getRoleById(unverifiedRoleId);
                if (unverifiedRole != null) {
                    guild.removeRoleFromMember(member, unverifiedRole).queue();
                }
            }

            // 認証済みロールを付与
            if (verifiedRoleId != null && !verifiedRoleId.isEmpty()) {
                Role verifiedRole = guild.getRoleById(verifiedRoleId);
                if (verifiedRole != null) {
                    guild.addRoleToMember(member, verifiedRole).queue();
                }
            }

            // ニックネーム変更 [階級]MinecraftName
            String nickname = "[" + rank.getId() + "]" + minecraftName;
            if (nickname.length() > 32) {
                nickname = nickname.substring(0, 32);
            }
            member.modifyNickname(nickname).queue(
                    success -> plugin.getLogger().info("Discord: " + minecraftName + " のニックネームを変更"),
                    error -> plugin.getLogger().warning("Discord: ニックネーム変更失敗: " + error.getMessage()));

        }, error -> {
        });
    }

    /**
     * 階級変更時にニックネームを更新
     */
    public void updateNickname(long discordId, String minecraftName, Rank rank) {
        if (!enabled || jda == null || guildId == null || guildId.isEmpty())
            return;

        Guild guild = jda.getGuildById(guildId);
        if (guild == null)
            return;

        guild.retrieveMemberById(discordId).queue(member -> {
            if (member == null)
                return;

            String nickname = "[" + rank.getId() + "]" + minecraftName;
            if (nickname.length() > 32) {
                nickname = nickname.substring(0, 32);
            }
            member.modifyNickname(nickname).queue();
        }, error -> {
        });
    }

    /**
     * 連携解除時にロールとニックネームをリセット
     */
    private void handleSettings(SlashCommandInteractionEvent event) {
        // 管理者権限チェック
        if (!isAdmin(event.getMember())) {
            event.reply("❌ このコマンドは管理者のみ使用可能です。").setEphemeral(true).queue();
            return;
        }

        String action = event.getOption("action").getAsString();
        String key = event.getOption("key") != null ? event.getOption("key").getAsString() : null;
        String value = event.getOption("value") != null ? event.getOption("value").getAsString() : null;

        if (action.equalsIgnoreCase("get")) {
            // 現在の設定を表示
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("⚙️ 現在の設定")
                    .setColor(Color.GRAY)
                    .addField("通知チャンネル", plugin.getConfigManager().getDiscordNotificationChannel(), false)
                    .addField("未認証ロール", plugin.getConfigManager().getDiscordUnverifiedRoleId(), true)
                    .addField("認証済みロール", plugin.getConfigManager().getDiscordVerifiedRoleId(), true)
                    .addField("通知ロール", plugin.getConfigManager().getDiscordNotificationRoleId(), true)
                    .addField("コンソールロール", plugin.getConfigManager().getDiscordConsoleRoleId(), true);

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        } else if (action.equalsIgnoreCase("set")) {
            if (key == null || value == null) {
                event.reply("❌ setにはkeyとvalueが必要です。\n例: `/settings action set key notification value 123456789`")
                        .setEphemeral(true).queue();
                return;
            }

            switch (key.toLowerCase()) {
                case "channel", "notification_channel" -> {
                    plugin.getConfigManager().setDiscordSetting("notification_channel_id", value);
                    event.reply("✅ 通知チャンネルIDを更新しました: " + value).setEphemeral(true).queue();
                }
                case "role_unverified", "unverified" -> {
                    plugin.getConfigManager().setDiscordSetting("unverified_role_id", value);
                    event.reply("✅ 未認証ロールIDを更新しました: " + value).setEphemeral(true).queue();
                }
                case "role_verified", "verified" -> {
                    plugin.getConfigManager().setDiscordSetting("verified_role_id", value);
                    event.reply("✅ 認証済みロールIDを更新しました: " + value).setEphemeral(true).queue();
                }
                case "role_notification", "notification" -> {
                    plugin.getConfigManager().setDiscordSetting("notification_role_id", value);
                    event.reply("✅ 通知ロールIDを更新しました: " + value).setEphemeral(true).queue();
                }
                case "role_console", "console" -> {
                    plugin.getConfigManager().setDiscordSetting("console_role_id", value);
                    event.reply("✅ コンソールロールIDを更新しました: " + value).setEphemeral(true).queue();
                }
                default -> event.reply("❌ 不明なキーです: " + key).setEphemeral(true).queue();
            }

            // 設定再読み込み
            plugin.getConfigManager().reload();

        } else if (action.equalsIgnoreCase("role")) {
            if (key == null || value == null) {
                event.reply("❌ role設定には階級IDとロールIDが必要です。\n例: `/settings action role key PRIVATE value 123456789`")
                        .setEphemeral(true).queue();
                return;
            }
            // 階級ロール設定
            plugin.getConfigManager().setDiscordRankRole(key, value);
            event.reply("✅ 階級 `" + key.toUpperCase() + "` にロールID `" + value + "` を紐付けました。").setEphemeral(true).queue();
            plugin.getConfigManager().reload();

        } else {
            event.reply("❌ 不明なアクションです: " + action).setEphemeral(true).queue();
        }
    }

    private void handlePanel(SlashCommandInteractionEvent event) {
        if (!isAdmin(event.getMember())) {
            event.reply("❌ このコマンドは管理者のみ使用可能です。").setEphemeral(true).queue();
            return;
        }

        String type = event.getOption("type").getAsString();

        if (type.equalsIgnoreCase("auth")) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("🔗 アカウント連携")
                    .setDescription("下のボタンを押して、Minecraftアカウントとの連携を開始してください。")
                    .setColor(Color.BLUE)
                    .setFooter("鉄の規律 - アカウント連携");

            event.getChannel().sendMessageEmbeds(eb.build())
                    .setActionRow(Button.primary("auth_start", "🔗 連携を開始する"))
                    .queue();

            event.reply("✅ 認証パネルを設置しました。").setEphemeral(true).queue();

        } else if (type.equalsIgnoreCase("roles")) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("🔘 ロール管理パネル")
                    .setDescription("以下のボタンでロールの同期や設定ができます。")
                    .setColor(Color.CYAN)
                    .addField("🔄 ロール・階級同期", "Minecraftの階級に合わせてDiscordロールを更新します。", false)
                    .addField("🔔 通知受け取り", "サーバーからの通知（参加/退出/警告など）を受け取るロールを切り替えます。", false);

            event.getChannel().sendMessageEmbeds(eb.build())
                    .setActionRow(
                            Button.success("role_sync", "🔄 階級・部隊を同期"),
                            Button.secondary("role_toggle_notify", "🔔 お知らせを受け取る"))
                    .queue();

            event.reply("✅ ロール管理パネルを設置しました。").setEphemeral(true).queue();
        } else if (type.equalsIgnoreCase("setup")) {
            sendSetupPanel(event);
            event.reply("✅ 設定パネルを開きました (自分のみ表示)").setEphemeral(true).queue();

        } else {
            event.reply("❌ 不明なパネルタイプです (auth/roles/setup)").setEphemeral(true).queue();
        }
    }

    // ===== Setup Panel Logic (Phase 8) =====

    /**
     * 設定パネル（メインメニュー）を送信
     */
    private void sendSetupPanel(SlashCommandInteractionEvent event) {
        StringSelectMenu menu = StringSelectMenu.create("setup_category")
                .setPlaceholder("設定カテゴリを選択してください")
                .addOption("⚙️ 基本設定", "basic", "通知チャンネル、権限ロールなどの基本設定")
                .addOption("🎖️ 階級ロール設定", "ranks", "Minecraftの階級とDiscordロールの紐付け")
                .build();

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("🛠️ IronDiscipline 設定パネル")
                .setDescription("設定したいカテゴリを下のメニューから選んでください。")
                .setColor(Color.LIGHT_GRAY);

        event.getChannel().sendMessageEmbeds(eb.build())
                .setActionRow(menu)
                .queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String id = event.getComponentId();

        // カテゴリ選択
        if (id.equals("setup_category")) {
            String selected = event.getValues().get(0);

            if (selected.equals("basic")) {
                // 基本設定メニュー
                EntitySelectMenu channelMenu = EntitySelectMenu
                        .create("setup_channel", EntitySelectMenu.SelectTarget.CHANNEL)
                        .setPlaceholder("通知チャンネルを選択")
                        .setMinValues(0) // 選択解除用
                        .setMaxValues(1)
                        .build();

                EntitySelectMenu notifyRoleMenu = EntitySelectMenu
                        .create("setup_role_notify", EntitySelectMenu.SelectTarget.ROLE)
                        .setPlaceholder("通知ロールを選択")
                        .setMinValues(0)
                        .setMaxValues(1)
                        .build();

                EntitySelectMenu verifiedRoleMenu = EntitySelectMenu
                        .create("setup_role_verified", EntitySelectMenu.SelectTarget.ROLE)
                        .setPlaceholder("認証済みロールを選択")
                        .setMinValues(0)
                        .setMaxValues(1)
                        .build();

                EntitySelectMenu unverifyRoleMenu = EntitySelectMenu
                        .create("setup_role_unverified", EntitySelectMenu.SelectTarget.ROLE)
                        .setPlaceholder("未認証ロールを選択")
                        .setMinValues(0)
                        .setMaxValues(1)
                        .build();

                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle("⚙️ 基本設定")
                        .setDescription("各項目に対応するチャンネルやロールを選択してください。")
                        .setColor(Color.BLUE);

                event.editMessageEmbeds(eb.build())
                        .setComponents(
                                event.getMessage().getActionRows().get(0), // カテゴリメニュー維持
                                net.dv8tion.jda.api.interactions.components.ActionRow.of(channelMenu),
                                net.dv8tion.jda.api.interactions.components.ActionRow.of(notifyRoleMenu),
                                net.dv8tion.jda.api.interactions.components.ActionRow.of(verifiedRoleMenu),
                                net.dv8tion.jda.api.interactions.components.ActionRow.of(unverifyRoleMenu))
                        .queue();

            } else if (selected.equals("ranks")) {
                // 階級選択メニュー
                StringSelectMenu rankMenu = StringSelectMenu.create("setup_rank_select")
                        .setPlaceholder("設定する階級を選択")
                        .addOption("二等兵 (PRIVATE)", "PRIVATE")
                        .addOption("上等兵 (PRIVATE_FIRST_CLASS)", "PRIVATE_FIRST_CLASS")
                        .addOption("伍長 (CORPORAL)", "CORPORAL")
                        .addOption("軍曹 (SERGEANT)", "SERGEANT")
                        .addOption("曹長 (SERGEANT_MAJOR)", "SERGEANT_MAJOR")
                        .addOption("准尉 (WARRANT_OFFICER)", "WARRANT_OFFICER")
                        .addOption("少尉 (LIEUTENANT)", "LIEUTENANT")
                        .addOption("中尉 (FIRST_LIEUTENANT)", "FIRST_LIEUTENANT")
                        .addOption("大尉 (CAPTAIN)", "CAPTAIN")
                        .addOption("少佐 (MAJOR)", "MAJOR")
                        .addOption("中佐 (LIEUTENANT_COLONEL)", "LIEUTENANT_COLONEL")
                        .addOption("大佐 (COLONEL)", "COLONEL")
                        .build();

                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle("🎖️ 階級ロール設定")
                        .setDescription("まず設定したい階級を選んでください。\nその後、ロール選択メニューが表示されます。")
                        .setColor(Color.YELLOW);

                event.editMessageEmbeds(eb.build())
                        .setComponents(
                                event.getMessage().getActionRows().get(0),
                                net.dv8tion.jda.api.interactions.components.ActionRow.of(rankMenu))
                        .queue();
            }

            // 階級選択後のロール選択表示
        } else if (id.equals("setup_rank_select")) {
            String rank = event.getValues().get(0);

            EntitySelectMenu roleMenu = EntitySelectMenu
                    .create("setup_rank_role_" + rank, EntitySelectMenu.SelectTarget.ROLE)
                    .setPlaceholder(rank + " に紐付けるロールを選択")
                    .setMinValues(0)
                    .setMaxValues(1)
                    .build();

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("🎖️ 設定: " + rank)
                    .setDescription("この階級に紐付けるDiscordロールを選択してください。")
                    .setColor(Color.ORANGE);

            event.editMessageEmbeds(eb.build())
                    .setComponents(
                            event.getMessage().getActionRows().get(0), // カテゴリ
                            event.getMessage().getActionRows().get(1), // 階級選択
                            net.dv8tion.jda.api.interactions.components.ActionRow.of(roleMenu))
                    .queue();
        }
    }

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        String id = event.getComponentId();
        String value = event.getValues().isEmpty() ? "" : event.getValues().get(0).getId();
        String name = event.getValues().isEmpty() ? "なし" : event.getValues().get(0).getAsMention(); // Channel or Role
                                                                                                    // mention

        if (id.equals("setup_channel")) {
            plugin.getConfigManager().setDiscordSetting("notification_channel_id", value);
            event.reply("✅ 通知チャンネルを更新しました: " + name).setEphemeral(true).queue();

        } else if (id.equals("setup_role_notify")) {
            plugin.getConfigManager().setDiscordSetting("notification_role_id", value);
            event.reply("✅ 通知ロールを更新しました: " + name).setEphemeral(true).queue();

        } else if (id.equals("setup_role_verified")) {
            plugin.getConfigManager().setDiscordSetting("verified_role_id", value);
            event.reply("✅ 認証済みロールを更新しました: " + name).setEphemeral(true).queue();

        } else if (id.equals("setup_role_unverified")) {
            plugin.getConfigManager().setDiscordSetting("unverified_role_id", value);
            event.reply("✅ 未認証ロールを更新しました: " + name).setEphemeral(true).queue();

        } else if (id.startsWith("setup_rank_role_")) {
            String rankId = id.replace("setup_rank_role_", "");
            plugin.getConfigManager().setDiscordRankRole(rankId, value);
            event.reply("✅ 階級 `" + rankId + "` のロールを更新しました: " + name).setEphemeral(true).queue();
        }

        // 設定を保存
        plugin.getConfigManager().reload();
    }

    public void onUnlink(long discordId) {
        if (!enabled || jda == null || guildId == null || guildId.isEmpty())
            return;

        Guild guild = jda.getGuildById(guildId);
        if (guild == null)
            return;

        guild.retrieveMemberById(discordId).queue(member -> {
            if (member == null)
                return;

            // 認証済みロールを削除
            if (verifiedRoleId != null && !verifiedRoleId.isEmpty()) {
                Role verifiedRole = guild.getRoleById(verifiedRoleId);
                if (verifiedRole != null) {
                    guild.removeRoleFromMember(member, verifiedRole).queue();
                }
            }

            // 未認証ロールを付与
            if (unverifiedRoleId != null && !unverifiedRoleId.isEmpty()) {
                Role unverifiedRole = guild.getRoleById(unverifiedRoleId);
                if (unverifiedRole != null) {
                    guild.addRoleToMember(member, unverifiedRole).queue();
                }
            }

            // ニックネームをリセット
            member.modifyNickname(null).queue();
        }, error -> {
        });
    }

    private void handleDivision(SlashCommandInteractionEvent event) {
        if (!isAdmin(event.getMember())) {
            event.reply("❌ このコマンドは管理者のみ使用可能です。").setEphemeral(true).queue();
            return;
        }

        String action = event.getOption("action").getAsString();
        String arg1 = event.getOption("arg1") != null ? event.getOption("arg1").getAsString() : null;
        String arg2 = event.getOption("arg2") != null ? event.getOption("arg2").getAsString() : null;

        if (action.equalsIgnoreCase("list")) {
            StringBuilder sb = new StringBuilder();
            for (String div : plugin.getDivisionManager().getAllDivisions()) {
                String display = plugin.getDivisionManager().getDivisionDisplayName(div);
                int count = plugin.getDivisionManager().getDivisionMembers(div).size();
                sb.append(display).append(": ").append(count).append("人\n");
            }
            event.reply("📜 **部隊一覧**\n" + sb.toString()).setEphemeral(true).queue();

        } else if (action.equalsIgnoreCase("create")) {
            if (arg1 == null) {
                event.reply("❌ 部隊名を指定してください。").setEphemeral(true).queue();
                return;
            }
            plugin.getDivisionManager().createDivision(arg1);
            event.reply("✅ 部隊 `" + arg1 + "` を作成しました。").setEphemeral(true).queue();

        } else if (action.equalsIgnoreCase("add")) {
            // arg1: ユーザー, arg2: 部隊
            if (arg1 == null || arg2 == null) {
                event.reply("❌ ユーザーと部隊名を指定してください。\n例: `/division action add arg1 @user arg2 infantry`")
                        .setEphemeral(true).queue();
                return;
            }
            // メンションからユーザーID抽出 (<@123456> -> 123456)
            long discordId = parseDiscordId(arg1);
            UUID uuid = plugin.getLinkManager().getMinecraftId(discordId);

            if (uuid == null) {
                event.reply("❌ そのユーザーはMinecraftと連携していません。").setEphemeral(true).queue();
                return;
            }

            if (!plugin.getDivisionManager().divisionExists(arg2)) {
                event.reply("❌ その部隊は存在しません。`/division list` で確認してください。").setEphemeral(true).queue();
                return;
            }

            plugin.getDivisionManager().setDivision(uuid, arg2);
            event.reply("✅ <@" + discordId + "> を `" + arg2 + "` に配属しました。").setEphemeral(true).queue();

            // 権限やロール更新のために即時反映処理があれば呼ぶ (今回はロール同期ボタン推奨)

        } else if (action.equalsIgnoreCase("remove")) {
            if (arg1 == null) {
                event.reply("❌ ユーザーを指定してください。").setEphemeral(true).queue();
                return;
            }
            long discordId = parseDiscordId(arg1);
            UUID uuid = plugin.getLinkManager().getMinecraftId(discordId);

            if (uuid == null) {
                event.reply("❌ そのユーザーはMinecraftと連携していません。").setEphemeral(true).queue();
                return;
            }

            plugin.getDivisionManager().removeDivision(uuid);
            event.reply("✅ <@" + discordId + "> を部隊から除隊させました。").setEphemeral(true).queue();

        } else {
            event.reply("❌ 不明なアクションです (create/add/remove/list)").setEphemeral(true).queue();
        }
    }

    private void handleAdminRank(SlashCommandInteractionEvent event, boolean promote) {
        if (!isAdmin(event.getMember())) {
            event.reply("❌ このコマンドは管理者のみ使用可能です。").setEphemeral(true).queue();
            return;
        }

        long targetDiscordId = event.getOption("user").getAsUser().getIdLong();
        UUID targetUUID = plugin.getLinkManager().getMinecraftId(targetDiscordId);

        if (targetUUID == null) {
            event.reply("❌ 対象ユーザーは連携していません。").setEphemeral(true).queue();
            return;
        }

        // RankManager requires Player object currently, need to fix if offline support
        // RankManager handles offline players via getRankAsync
        plugin.getRankManager().getRankAsync(targetUUID).thenAccept(current -> {
            Rank next = promote ? current.getNextRank() : current.getPreviousRank();

            if (next == null) {
                event.reply("❌ これ以上階級を変更できません (現在: " + current.getId() + ")").setEphemeral(true).queue();
                return;
            }

            plugin.getRankManager().setRankByUUID(targetUUID, next).thenAccept(success -> {
                if (success) {
                    event.getHook().sendMessage(
                            "✅ " + (promote ? "昇進" : "降格") + "させました: " + current.getId() + " -> " + next.getId())
                            .queue();
                    updateNickname(targetDiscordId, Bukkit.getOfflinePlayer(targetUUID).getName(), next);
                } else {
                    event.getHook().sendMessage("❌ 階級変更に失敗しました。").queue();
                }
            });
        });

        event.deferReply().queue();
    }

    private void handleSetRank(SlashCommandInteractionEvent event) {
        if (!isAdmin(event.getMember())) {
            event.reply("❌ このコマンドは管理者のみ使用可能です。").setEphemeral(true).queue();
            return;
        }

        long targetDiscordId = event.getOption("user").getAsUser().getIdLong();
        String rankId = event.getOption("rank").getAsString();
        UUID targetUUID = plugin.getLinkManager().getMinecraftId(targetDiscordId);

        if (targetUUID == null) {
            event.reply("❌ 対象ユーザーは連携していません。").setEphemeral(true).queue();
            return;
        }

        try {
            Rank rank = Rank.valueOf(rankId.toUpperCase());
            plugin.getRankManager().setRankByUUID(targetUUID, rank).thenAccept(success -> {
                if (success) {
                    event.getHook().sendMessage("✅ 階級を設定しました: " + rank.getId()).queue();
                    updateNickname(targetDiscordId, Bukkit.getOfflinePlayer(targetUUID).getName(), rank);
                } else {
                    event.getHook().sendMessage("❌ 階級設定に失敗しました。").queue();
                }
            });
            event.deferReply().queue();
        } catch (IllegalArgumentException e) {
            event.reply("❌ 無効な階級名です。").setEphemeral(true).queue();
        }
    }

    private void handlePunish(SlashCommandInteractionEvent event, String type) {
        if (!isAdmin(event.getMember()) && (event.getMember() == null || !event.getMember().hasPermission(Permission.KICK_MEMBERS))) {
            event.reply("❌ 権限がありません。").setEphemeral(true).queue();
            return;
        }

        long targetDiscordId = event.getOption("user").getAsUser().getIdLong();
        String reason = event.getOption("reason").getAsString();
        UUID targetUUID = plugin.getLinkManager().getMinecraftId(targetDiscordId);

        if (targetUUID == null) {
            event.reply("❌ 対象ユーザーは連携していません。").setEphemeral(true).queue();
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (type.equals("kick")) {
                Player target = Bukkit.getPlayer(targetUUID);
                if (target != null) {
                    target.kickPlayer(ChatColor.RED + "Kicked by Discord Admin\nReason: " + reason);
                }
            } else if (type.equals("ban")) {
                @SuppressWarnings("deprecation")
                org.bukkit.BanList<org.bukkit.BanEntry<String>> banList = (org.bukkit.BanList<org.bukkit.BanEntry<String>>) Bukkit.getBanList(org.bukkit.BanList.Type.NAME);
                if (banList != null) {
                    @SuppressWarnings("deprecation")
                    String targetName = Bukkit.getOfflinePlayer(targetUUID).getName();
                    banList.addBan(targetName, reason, null, "Console(Discord)");
                }

                Player target = Bukkit.getPlayer(targetUUID);
                if (target != null) {
                    target.kickPlayer(ChatColor.RED + "Banned by Discord Admin\nReason: " + reason);
                }
            }
        });

        event.reply("✅ 処罰を実行しました (" + type + "): " + reason).queue();
    }

    // Helper to parse <@12345> style mentions or raw IDs
    private long parseDiscordId(String input) {
        if (input.startsWith("<@") && input.endsWith(">")) {
            input = input.replaceAll("[^0-9]", "");
        }
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ===== Button Interactions =====

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getComponentId();

        if (id.equals("auth_start")) {
            long discordId = event.getUser().getIdLong();
            if (plugin.getLinkManager().isLinked(discordId)) {
                event.reply("✅ 既に連携済みです！").setEphemeral(true).queue();
                return;
            }

            String code = plugin.getLinkManager().generateLinkCode(discordId);
            event.reply("以下のコマンドをMinecraftサーバー内で入力してください：\n`/link " + code + "`\n(有効期限: 5分)")
                    .setEphemeral(true).queue();

        } else if (id.equals("role_sync")) {
            long discordId = event.getUser().getIdLong();
            UUID uuid = plugin.getLinkManager().getMinecraftId(discordId);

            if (uuid == null) {
                event.reply("❌ Minecraftアカウントと連携されていません。先に連携を行ってください。").setEphemeral(true).queue();
                return;
            }

            // Sync logic
            event.deferReply(true).queue();

            plugin.getRankManager().getRankAsync(uuid).thenAccept(rank -> {
                String rankRoleId = plugin.getConfigManager().getDiscordRankRoleId(rank.name());
                String verifiedRoleId = plugin.getConfigManager().getDiscordVerifiedRoleId();

                Guild guild = event.getGuild();
                Member member = event.getMember();

                if (guild != null && member != null) {
                    // 認証済みロールチェック
                    if (verifiedRoleId != null && !verifiedRoleId.isEmpty()) {
                        Role vRole = guild.getRoleById(verifiedRoleId);
                        if (vRole != null && !member.getRoles().contains(vRole)) {
                            guild.addRoleToMember(member, vRole).queue();
                        }
                    }

                    // 階級ロールチェック
                    if (rankRoleId != null && !rankRoleId.isEmpty()) {
                        Role rRole = guild.getRoleById(rankRoleId);
                        if (rRole != null && !member.getRoles().contains(rRole)) {
                            guild.addRoleToMember(member, rRole).queue();
                        }
                    }

                    // ニックネーム更新
                    updateNickname(discordId, Bukkit.getOfflinePlayer(uuid).getName(), rank);

                    event.getHook().sendMessage("✅ ロールと階級情報を同期しました！").queue();
                } else {
                    event.getHook().sendMessage("❌ サーバー情報の取得に失敗しました。").queue();
                }
            });

        } else if (id.equals("role_toggle_notify")) {
            String notifyRoleId = plugin.getConfigManager().getDiscordNotificationRoleId();
            if (notifyRoleId == null || notifyRoleId.isEmpty()) {
                event.reply("⚠️ 通知ロールが設定されていません。管理者に報告してください。").setEphemeral(true).queue();
                return;
            }

            Guild guild = event.getGuild();
            Member member = event.getMember();
            Role notifyRole = guild.getRoleById(notifyRoleId);

            if (notifyRole == null) {
                event.reply("⚠️ 通知ロールが見つかりません。").setEphemeral(true).queue();
                return;
            }

            if (member.getRoles().contains(notifyRole)) {
                guild.removeRoleFromMember(member, notifyRole).queue();
                event.reply("🔕 お知らせ通知を **OFF** にしました。").setEphemeral(true).queue();
            } else {
                guild.addRoleToMember(member, notifyRole).queue();
                event.reply("🔔 お知らせ通知を **ON** にしました。").setEphemeral(true).queue();
            }
        }
    }
}
