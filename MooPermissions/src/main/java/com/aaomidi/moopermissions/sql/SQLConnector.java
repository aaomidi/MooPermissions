package com.aaomidi.moopermissions.sql;

import com.aaomidi.moopermissions.MooPermissions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by amir on 2015-12-14.
 */
public class SQLConnector {
    @Getter
    private final MooPermissions instance;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String database;
    private HikariDataSource dataSource;
    private Connection connection;
    private Logger logger;

    public SQLConnector(MooPermissions instance, String host, int port, String username, String password, String database) {
        this.instance = instance;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;

        this.logger = instance.getLogger();
        this.createHikariDataSource();
    }

    private void createHikariDataSource() {
        try {

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?zeroDateTimeBehavior=convertToNull", this.host, this.port, this.database));
            config.setUsername(this.username);
            config.setPassword(this.password);
            config.setMaximumPoolSize(3);
            dataSource = new HikariDataSource(config);
            this.createConnection();
        } catch (Exception ex) {
            throw new Error("Unrecoverable error when creating the HikariDataSource object.", ex);
        }
    }

    private void createConnection() {
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new Error("Unrecoverable error when creating the connection.", e);
        }

    }

    public boolean isConnected() throws SQLException {
        return (this.connection != null && !this.connection.isClosed());

    }

    protected Connection getConnection() throws SQLException {
        if (!isConnected()) {
            this.createConnection();
        }
        return this.connection;
    }


    /**
     * Execute a query to the specified connection.
     *
     * @param query      Query to execute. Example: SELECT * FROM `exampleTable` WHERE `someColumn`=?;
     * @param parameters Parameters to fill in the question marks with.
     * @return ResultSet returned by SQL. This value CAN be null.
     * @throws SQLException
     */
    public ResultSet executeQuery(String query, Object... parameters) {
        int parameterCount = (parameters == null) ? 0 : parameters.length;

        if (StringUtils.countMatches(query, "?") != parameterCount) {
            logger.log(Level.SEVERE, "The number of ? did not match the number of parameters.");
            return null;
        }
        try {
            PreparedStatement statement = prepareStatement(query, parameterCount, parameters);
            return statement.executeQuery();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, String.format("There was a problem executing the following query: %s \n Stack Trace: %s", query, ex));
        }
        return null;
    }

    /**
     * Execute an update to the specified connection.
     *
     * @param query      Query to execute. Example: UPDATE `exampleTable` SET `someColumn` = ? WHERE `otherColumn` = ?;
     * @param parameters Parameters to fill in the question marks with.
     * @return Result integer returned by SQL. If this value is -1 there was a mistake in the number of `?` and parameters.
     * @throws SQLException
     */
    public int executeUpdate(String query, Object... parameters) {
        int parameterCount = (parameters == null) ? 0 : parameters.length;

        if (StringUtils.countMatches(query, "?") != parameterCount) {
            logger.log(Level.SEVERE, "The number of ? did not match the number of parameters.");
            return -1;
        }
        try (PreparedStatement statement = prepareStatement(query, parameterCount, parameters)) {
            return statement.executeUpdate();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, String.format("There was a problem executing the following query: %s \n Stack Trace: %s", query, ex));
        }
        return -1;
    }

    /**
     * Prepare a statement by replacing ? with parameters.
     *
     * @param query
     * @param parameterCount
     * @param parameters
     * @return
     * @throws SQLException
     */
    protected PreparedStatement prepareStatement(String query, int parameterCount, Object... parameters) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement(query);
        Object parameter;

        for (int i = 0, j = 1; i < parameterCount; i++, j++) {
            parameter = parameters[i];
            if (parameter instanceof String) {
                statement.setString(j, (String) parameter);
            } else if (parameter instanceof Integer) {
                statement.setInt(j, (Integer) parameter);
            } else if (parameter instanceof Double) {
                statement.setDouble(j, (Double) parameter);
            } else if (parameter instanceof Float) {
                statement.setFloat(j, (Float) parameter);
            } else if (parameter instanceof Boolean) {
                statement.setBoolean(j, (Boolean) parameter);
            } else {
                statement.setObject(j, parameter);
            }
        }

        return statement;
    }

    /**
     * Disconnect from database.
     */
    public void disconnect() {
        try {
            if (isConnected()) {
                connection.close();
            }
            dataSource.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
