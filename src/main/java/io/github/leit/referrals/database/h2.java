package io.github.leit.referrals.database;

import io.github.leit.referrals.Referrals;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class h2 {


    private SqlService sql;
    private Referrals plugin = (Referrals) Sponge.getPluginManager().getPlugin("referrals").get().getInstance().get();
    private String configDir = plugin.getDefaultConfigDir().toString();
    private String uri = "jdbc:h2:"+ configDir +"/data";
    private Logger logger = plugin.getLogger();


    public DataSource getDataSource(String jdbcUrl) throws SQLException {

        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }

        return sql.getDataSource(jdbcUrl);
    }


    // Create our database
    public void createDatabase() throws SQLException {

        String sqlCreateTable1 = "CREATE TABLE IF NOT EXISTS playersReferred (" +
                " uuid VARCHAR PRIMARY KEY," +
                " isReferred INT NOT NULL," +
                " referredBy VARCHAR )";

        String sqlCreateTable2 = "CREATE TABLE IF NOT EXISTS playersData (" +
                " uuid VARCHAR PRIMARY KEY," +
                " playersReferred INT NOT NULL)";

        Connection conn = getDataSource(uri).getConnection();
        Statement createStatement = conn.createStatement();
        createStatement.execute(sqlCreateTable1);
        createStatement.execute(sqlCreateTable2);
        conn.close();
        logger.info("Database Connection Established!");
    }

    /*
    * Set if a user is Referred
    *
    * @param uuid
    * */
    public void setIsReferred(UUID uuid) throws SQLException {
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("UPDATE playersReferred SET isReferred = ? WHERE uuid = ?");
        pstmt.setInt(1, 1);
        pstmt.setString(2, uuid.toString());
        pstmt.execute();
        conn.close();
    }

    /*
    * Set if a user is not Referred
    *
    * @param uuid
    * */
    public void setIsNotReferred(UUID uuid) throws SQLException {
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("UPDATE playersReferred SET isReferred = ? WHERE uuid = ?");
        pstmt.setInt(1, 0);
        pstmt.setString(2, uuid.toString());
        pstmt.execute();
        conn.close();
    }

    /*
    * Get if a user is Referred
    *
    * @param uuid
    * */
    public boolean getIsReferred(UUID uuid) throws SQLException {
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT isReferred FROM playersReferred WHERE uuid = ?");
        pstmt.setString(1, uuid.toString());

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            if (rs.getInt("isReferred") == 1) {
                conn.close();
                return true;
            } else {
                conn.close();
                return false;
            }
        }
        conn.close();
        return false;
    }

    /*
    * Get if a user exists
    *
    * @param uuid
    * */
    public boolean isUser(UUID uuid) throws SQLException {
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM playersReferred WHERE uuid = ?");
        pstmt.setString(1, uuid.toString());

        if (pstmt.executeQuery().next()) {
            conn.close();
            return true;
        } else {
            conn.close();
            return false;
        }
    }

    /*
    * Create a user, set isReferred to 0, no Referrer
    *
    * @param uuid
    * */
    public void createUser(UUID uuid) throws SQLException {
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmtA = conn.prepareStatement("INSERT INTO playersReferred(uuid, isReferred, referredBy) VALUES(?,?,?)");
        pstmtA.setString(1, uuid.toString());
        pstmtA.setInt(2, 0);
        pstmtA.setString(3, null);
        pstmtA.execute();

        PreparedStatement pstmtB = conn.prepareStatement("INSERT INTO playersData(uuid, playersReferred) VALUES(?,?)");
        pstmtB.setString(1, uuid.toString());
        pstmtB.setInt(2, 0);
        pstmtB.execute();
        conn.close();
    }

    /*
    * Set who a user is ReferredBy
    *
    * @param uuidReferred
    * @param uuidReferrer
    * */
    public void setReferredBy(UUID uuidReferred, UUID uuidReferrer) throws SQLException {
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("UPDATE playersReferred SET referredBy = ? WHERE uuid = ?");
        pstmt.setString(1, uuidReferrer.toString());
        pstmt.setString(2, uuidReferred.toString());

        pstmt.execute();
        conn.close();
    }

    /*
    * Get who a user is Referred by
    *
    * @param uuid
    * */
    public String getReferredBy(UUID uuid) throws SQLException {
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT referredBy FROM playersReferred WHERE uuid = ?");
        pstmt.setString(1, uuid.toString());

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            if (!rs.getString("referredBy").equals(null)) {
                conn.close();
                return rs.getString("referredBy");
            } else {
                conn.close();
                return null;
            }
        }
        conn.close();
        return null;
    }

    /*
    * Get the amount of users a player has referred
    *
    * @param uuid
    * */
    public int getPlayersReferred(UUID uuid) throws SQLException {
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT playersReferred FROM playersData WHERE uuid = ?");
        pstmt.setString(1, uuid.toString());

        ResultSet rs = pstmt.executeQuery();
        conn.close();
        while (rs.next()) {
            conn.close();
            return rs.getInt("playersReferred");
        }
        conn.close();
        return 0;
    }

    /*
    * Add 1 to the amount a user has referred
    *
    * @param uuid
    * */
    public void addToPlayersReferred(UUID uuid) throws SQLException {
        Connection conn = getDataSource(uri).getConnection();
        int currentReferredCount = getPlayersReferred(uuid);
        PreparedStatement pstmt = conn.prepareStatement("UPDATE playersData SET playersReferred = ? WHERE uuid = ?");

        pstmt.setInt(1, ++currentReferredCount);
        pstmt.setString(2, uuid.toString());

        pstmt.execute();
        conn.close();
    }

    /*
    * subtract 1 to the amount a user has referred
    *
    * @param uuid
    * */
    public void removeFromPlayersReferred(UUID uuid) throws SQLException {
        Connection conn = getDataSource(uri).getConnection();
        int currentReferredCount = getPlayersReferred(uuid);
        PreparedStatement pstmt = conn.prepareStatement("UPDATE playersData SET playersReferred = ? WHERE uuid = ?");

        pstmt.setInt(1, --currentReferredCount);
        pstmt.setString(2, uuid.toString());

        pstmt.execute();
        conn.close();
    }

    /*
    * Get a list of the top Referrers, specified amount in list by int
    *
    * @param count
    * */
    public Map<String, Integer> getTopReferrers(int count) throws SQLException {
        Connection conn = getDataSource(uri).getConnection();
        Map<String, Integer> playerList = new HashMap<>();
        int i = 0;
        PreparedStatement pstmt = conn.prepareStatement("SELECT playersReferred, uuid FROM playersData ORDER BY playersReferred DESC");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next())
            if (i < count) {
                playerList.put(rs.getString("uuid"), rs.getInt("playersReferred"));
                i++;
            } else {
                break;
            }
        conn.close();
        return playerList;
    }

}
