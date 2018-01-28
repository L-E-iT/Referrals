package io.github.leit.referrals.database;

import io.github.leit.referrals.Referrals;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class h2 {


    private SqlService sql;
    private Referrals plugin = (Referrals) Sponge.getPluginManager().getPlugin("referrals").get().getInstance().get();
    private String configDir = plugin.getDefaultConfigDir().toString();
    private String uri = "jdbc:h2:"+ configDir +"/data";
    private Logger logger = plugin.getLogger();

    private DataSource getDataSource(String jdbcUrl) throws SQLException {

        if (sql == null) {
            Optional<SqlService> optionalSql = Sponge.getServiceManager().provide(SqlService.class);

            if (optionalSql.isPresent()) {
                this.sql = optionalSql.get();
            } else {
                throw new SQLException("Sponge SQL service missing");
            }
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
        try (
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("UPDATE playersReferred SET isReferred = ? WHERE uuid = ?");
        ) {
            pstmt.setInt(1, 1);
            pstmt.setString(2, uuid.toString());
            pstmt.execute();
        } catch (SQLException e) {
            logger.error("Failed to set referred ");
            e.printStackTrace();
        }
    }

    /*
    * Set if a user is not Referred
    *
    * @param uuid
    * */
    public void setIsNotReferred(UUID uuid) throws SQLException {
        try (
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("UPDATE playersReferred SET isReferred = ? WHERE uuid = ?");
        ) {
            pstmt.setInt(1, 0);
            pstmt.setString(2, uuid.toString());
            pstmt.execute();
        } catch (SQLException e) {
            logger.error("Failed to set is not referred");
            e.printStackTrace();
        }
    }

    /*
    * Get if a user is Referred
    *
    * @param uuid
    * */
    public boolean getIsReferred(UUID uuid) throws SQLException {
        try (
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT isReferred FROM playersReferred WHERE uuid = ?");
        ) {
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
        } catch (SQLException e) {
            logger.error("Failed to get if player is referred");
            e.printStackTrace();
        }
        return false;
    }

    /*
    * Get if a user exists
    *
    * @param uuid
    * */
    public boolean isUser(UUID uuid) throws SQLException {
        try (
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM playersReferred WHERE uuid = ?");
        ) {
            pstmt.setString(1, uuid.toString());

            if (pstmt.executeQuery().next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            logger.error("Failed to get if user exists");
            e.printStackTrace();
        }
        return false;
    }

    /*
    * Create a user, set isReferred to 0, no Referrer
    *
    * @param uuid
    * */
    public void createUser(UUID uuid) throws SQLException {
        try (
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmtA = conn.prepareStatement("INSERT INTO playersReferred(uuid, isReferred, referredBy) VALUES(?,?,?)");
        ) {
            pstmtA.setString(1, uuid.toString());
            pstmtA.setInt(2, 0);
            pstmtA.setString(3, null);
            pstmtA.execute();

            PreparedStatement pstmtB = conn.prepareStatement("INSERT INTO playersData(uuid, playersReferred) VALUES(?,?)");
            pstmtB.setString(1, uuid.toString());
            pstmtB.setInt(2, 0);
            pstmtB.execute();
        } catch (SQLException e) {
            logger.error("Failed to create user");
            e.printStackTrace();
        }
    }

    /*
    * Set who a user is ReferredBy
    *
    * @param uuidReferred
    * @param uuidReferrer
    * */
    public void setReferredBy(UUID uuidReferred, UUID uuidReferrer) throws SQLException {
        try (
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("UPDATE playersReferred SET referredBy = ? WHERE uuid = ?");
        ) {
            pstmt.setString(1, uuidReferrer.toString());
            pstmt.setString(2, uuidReferred.toString());

            pstmt.execute();
    } catch (SQLException e) {
            logger.error("Failed to set who user is referred by");
            e.printStackTrace();
        }
    }

    /*
    * Get who a user is Referred by
    *
    * @param uuid
    * */
    public String getReferredBy(UUID uuid) throws SQLException {
        try (
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT referredBy FROM playersReferred WHERE uuid = ?");
        ) {
            pstmt.setString(1, uuid.toString());

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (!rs.getString("referredBy").equals(null)) {
                    String referredBy = rs.getString("referredBy");
                    conn.close();
                    return referredBy;
                } else {
                    conn.close();
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get who user is referred by");
            e.printStackTrace();
        }
        return null;
    }

    /*
    * Get the amount of users a player has referred
    *
    * @param uuid
    * */
    public int getPlayersReferred(UUID uuid) throws SQLException {
        try (
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT playersReferred FROM playersData WHERE uuid = ?");
        ) {
            pstmt.setString(1, uuid.toString());

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int playersReferred = rs.getInt("playersReferred");
                return playersReferred;
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Failed to get amount of players referred");
            e.printStackTrace();
        }
        return 0;
    }

    /*
    * Add 1 to the amount a user has referred
    *
    * @param uuid
    * */
    public void addToPlayersReferred(UUID uuid) throws SQLException {
        try(
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("UPDATE playersData SET playersReferred = ? WHERE uuid = ?");
        ) {
            int currentReferredCount = getPlayersReferred(uuid);

            pstmt.setInt(1, ++currentReferredCount);
            pstmt.setString(2, uuid.toString());

            pstmt.execute();
        } catch (SQLException e) {
            logger.error("Failed to add to amount player has referred");
            e.printStackTrace();
        }
    }

    /*
    * subtract 1 to the amount a user has referred
    *
    * @param uuid
    * */
    public void removeFromPlayersReferred(UUID uuid) throws SQLException {
        try (
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("UPDATE playersData SET playersReferred = ? WHERE uuid = ?");
        ) {
            int currentReferredCount = getPlayersReferred(uuid);
            pstmt.setInt(1, --currentReferredCount);
            pstmt.setString(2, uuid.toString());

            pstmt.execute();
        } catch (SQLException e) {
            logger.error("Failed to remove from amount player has referred");
            e.printStackTrace();
        }
    }

    /*
    * Get a list of the top Referrers, specified amount in list by int
    *
    * @param count
    * */
    public Map<String, Integer> getTopReferrers(int count) throws SQLException {
        final Map<String, Integer> playerList = new HashMap<>();
        try (
        Connection conn = getDataSource(uri).getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT playersReferred, uuid FROM playersData ORDER BY playersReferred DESC");
        ResultSet rs = pstmt.executeQuery();
        ) {
            int i = 0;
            while (rs.next())
                if (i < count) {
                    playerList.put(rs.getString("uuid"), rs.getInt("playersReferred"));
                    i++;
                } else {
                    break;
                }
        } catch (SQLException e) {
            logger.error("Failed to get top referrers");
            e.printStackTrace();
        }
        return playerList;
    }

}
