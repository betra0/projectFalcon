package com.dany.falcon.database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.dany.falcon.ia.MemoryItem;

public class Database {
    private final String url = "jdbc:sqlite:db.db";
    private static Database instance;

    private Database() {
    }
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
            instance.createTableMemories();
        }
        return instance;
    }
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void createTableMemories() {
        String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS memories (
            id TEXT PRIMARY KEY,
            content TEXT NOT NULL,
            timestamp INTEGER NOT NULL,
            type TEXT NOT NULL,
            importance TEXT NOT NULL,
            expiry INTEGER
        );
    """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
            System.out.println("Tabla 'memories' creada o ya existía.");
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }


    // Guardar o actualizar
    public void saveOrUpdateMemory(MemoryItem memory) {
        String sql = """
            INSERT INTO memories (id, content, timestamp, type, importance, expiry)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                content = excluded.content,
                timestamp = excluded.timestamp,
                type = excluded.type,
                importance = excluded.importance,
                expiry = excluded.expiry;
        """;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memory.getId());
            pstmt.setString(2, memory.getContent());
            pstmt.setLong(3, memory.getTimestamp());
            pstmt.setString(4, memory.getType().name());
            pstmt.setString(5, memory.getImportance().name());
            pstmt.setLong(6, memory.getExpiry());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar/actualizar memory: " + e.getMessage());
        }
    }

    // Leer todos
    public List<MemoryItem> getAllMemories() {
        List<MemoryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM memories;";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MemoryItem memory = new MemoryItem(
                        rs.getString("id"),
                        rs.getString("content"),
                        rs.getLong("timestamp"),
                        MemoryItem.MemoryType.valueOf(rs.getString("type")),
                        MemoryItem.ImportanceType.valueOf(rs.getString("importance")),
                        rs.getLong("expiry")
                );
                list.add(memory);
            }
        } catch (SQLException e) {
            System.out.println("Error al leer memories: " + e.getMessage());
        }

        return list;
    }






}
