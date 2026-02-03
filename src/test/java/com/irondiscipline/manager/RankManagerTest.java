package com.irondiscipline.manager;

import com.irondiscipline.IronDiscipline;
import com.irondiscipline.model.Rank;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.MetaNode;
import com.irondiscipline.util.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RankManagerTest {

    @Mock
    private IronDiscipline plugin;
    @Mock
    private LuckPerms luckPerms;
    @Mock
    private UserManager userManager;
    @Mock
    private User user;
    @Mock
    private CachedDataManager cachedDataManager;
    @Mock
    private CachedMetaData cachedMetaData;
    @Mock
    private EventBus eventBus;
    @Mock
    private ConfigManager configManager;
    @Mock
    private Player player;
    @Mock
    private BukkitScheduler scheduler;
    @Mock
    private TaskScheduler taskScheduler;

    private RankManager rankManager;
    private AutoCloseable mocks;
    private MockedStatic<Bukkit> bukkitMock;
    private MockedStatic<MetaNode> metaNodeMock;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Bukkit Static Mocks
        bukkitMock = mockStatic(Bukkit.class);
        bukkitMock.when(Bukkit::getScheduler).thenReturn(scheduler);
        
        org.bukkit.scoreboard.ScoreboardManager scoreboardManager = mock(org.bukkit.scoreboard.ScoreboardManager.class);
        org.bukkit.scoreboard.Scoreboard scoreboard = mock(org.bukkit.scoreboard.Scoreboard.class);
        when(scoreboardManager.getMainScoreboard()).thenReturn(scoreboard);
        // Mock team (often used in TabNametagUtil)
        org.bukkit.scoreboard.Team team = mock(org.bukkit.scoreboard.Team.class);
        when(scoreboard.getTeam(anyString())).thenReturn(team);
        when(scoreboard.registerNewTeam(anyString())).thenReturn(team);

        bukkitMock.when(Bukkit::getScoreboardManager).thenReturn(scoreboardManager);
        
        // Basic Plugin Setup
        when(plugin.getConfigManager()).thenReturn(configManager);
        when(plugin.getTaskScheduler()).thenReturn(taskScheduler);
        when(configManager.getRankMetaKey()).thenReturn("rank");

        // LuckPerms Setup
        when(luckPerms.getUserManager()).thenReturn(userManager);
        when(luckPerms.getEventBus()).thenReturn(eventBus);
        
        // User Mock Setup
        when(userManager.getUser(any(UUID.class))).thenReturn(user);
        when(userManager.loadUser(any(UUID.class))).thenReturn(CompletableFuture.completedFuture(user));
        when(user.getCachedData()).thenReturn(cachedDataManager);
        when(user.getCachedData().getMetaData()).thenReturn(cachedMetaData);
        
        // Data Mutate Mock
        when(user.data()).thenReturn(mock(net.luckperms.api.model.data.NodeMap.class));

        // Scheduler
        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(1);
            r.run();
            return null;
        }).when(scheduler).runTask(any(IronDiscipline.class), any(Runnable.class));

        // TaskScheduler mocks
        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run();
            return null;
        }).when(taskScheduler).runGlobal(any(Runnable.class));

        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(1);
            r.run();
            return null;
        }).when(taskScheduler).runEntity(any(Entity.class), any(Runnable.class));

        // Mock MetaNode static builder
        metaNodeMock = mockStatic(MetaNode.class);
        MetaNode.Builder builderMock = mock(MetaNode.Builder.class);
        MetaNode metaNodeInstance = mock(MetaNode.class);
        
        metaNodeMock.when(() -> MetaNode.builder(anyString(), anyString())).thenReturn(builderMock);
        when(builderMock.build()).thenReturn(metaNodeInstance);

        // Logger mock
        when(plugin.getLogger()).thenReturn(java.util.logging.Logger.getLogger("RankManagerTest"));

        rankManager = new RankManager(plugin, luckPerms);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (metaNodeMock != null) metaNodeMock.close();
        if (bukkitMock != null) bukkitMock.close();
        if (mocks != null) mocks.close();
    }

    @Test
    void testGetRank_DefaultPrivate() {
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        // Return null for meta value
        when(cachedMetaData.getMetaValue("rank")).thenReturn(null);

        Rank rank = rankManager.getRank(player);
        assertEquals(Rank.PRIVATE, rank, "Default rank should be PRIVATE");
    }

    @Test
    void testGetRank_Specific() {
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(cachedMetaData.getMetaValue("rank")).thenReturn("MAJOR");

        Rank rank = rankManager.getRank(player);
        assertEquals(Rank.MAJOR, rank);
    }

    @Test
    void testPromote() {
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.isOnline()).thenReturn(true);
        when(configManager.getMessage(anyString(), anyString(), anyString())).thenReturn("Message");

        // Start as PRIVATE
        when(cachedMetaData.getMetaValue("rank")).thenReturn("PRIVATE");
        
        // Mock save
        when(userManager.saveUser(user)).thenReturn(CompletableFuture.completedFuture(null));

        Rank newRank = rankManager.promote(player).join();

        assertNotNull(newRank);
        assertEquals(Rank.PRIVATE_FIRST_CLASS, newRank);
        
        // Verify LuckPerms update was called (simplified verification of logic flow)
        verify(userManager).saveUser(user);
    }

    @Test
    void testDemote() {
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.isOnline()).thenReturn(true);
        when(configManager.getMessage(anyString(), anyString(), anyString())).thenReturn("Message");

        // Start as CORPORAL
        when(cachedMetaData.getMetaValue("rank")).thenReturn("CORPORAL");
        
        when(userManager.saveUser(user)).thenReturn(CompletableFuture.completedFuture(null));

        Rank newRank = rankManager.demote(player).join();

        assertNotNull(newRank);
        assertEquals(Rank.PRIVATE_FIRST_CLASS, newRank);
    }

    @Test
    void testPromote_MaxRank() {
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        // COMMANDER is highest
        when(cachedMetaData.getMetaValue("rank")).thenReturn("COMMANDER");
        
        Rank newRank = rankManager.promote(player).join();
        
        assertNull(newRank, "Should not promote from highest rank");
        verify(userManager, never()).saveUser(user);
    }
}
