package io.github.leit.referrals.database;

import io.github.leit.referrals.Referrals;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class h2 {


    private SqlService sql;
    private Referrals plugin = (Referrals) Sponge.getPluginManager().getPlugin("referrals").get().getInstance().get();
    private String configDir = plugin.getDefaultConfigDir().toString();
    private String uri = "jdbc:h2:"+ configDir +"/data;mode=MySQL";
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
    * Save Data to Database
    *
    * @param List<playerData>
    * */
    public void saveData(List<PlayerData> playerDataList) {
        try (
                Connection conn = getDataSource(uri).getConnection();
                PreparedStatement pspr = conn.prepareStatement("INSERT INTO playersReferred SET uuid = ?, isReferred = ?, referredBy = ? ON DUPLICATE KEY UPDATE uuid = VALUES(uuid), isReferred = VALUES(isReferred), referredBy = VALUES(referredBy)");
                PreparedStatement pspd = conn.prepareStatement("INSERT INTO playersData SET uuid = ?, playersReferred = ? ON DUPLICATE KEY UPDATE uuid = VALUES(uuid), playersReferred = VALUES(playersReferred)");
        ) {
           for (PlayerData playerData: playerDataList){
               pspr.setString(1, playerData.getPlayerUUID().toString());
               pspr.setInt(2, playerData.getIsReferred());
               pspr.setString(3, playerData.getReferredBy().toString());
               pspd.setString(1, playerData.getPlayerUUID().toString());
               pspd.setInt(2, playerData.getPlayersReferred());
               pspr.addBatch();
               pspd.addBatch();
           }
            pspr.executeBatch();
            pspd.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    * Get Data from Database
    *
    * */
    public Optional<List<PlayerData>> loadData(){
        List<PlayerData> playerDataList = new ArrayList<>();
        try (
                Connection conn = getDataSource(uri).getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM playersReferred as pr INNER JOIN " +
                        "playersData as pd ON pr.uuid = pd.uuid")
                ) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                PlayerData playerData = new PlayerData(UUID.fromString(rs.getString("uuid")),
                        rs.getInt("isReferred"),
                        UUID.fromString(rs.getString("referredBy")),
                        rs.getInt("playersReferred"));
                playerDataList.add(playerData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.of(playerDataList);
    }
}
