package com.dany.falcon.database;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dany.falcon.ia.Conversation;
import com.dany.falcon.ia.ConversationPreview;
import com.dany.falcon.ia.MemoryItem;
import com.dany.falcon.ia.Message;

public class Database {
    private final String url = "jdbc:sqlite:db.db";
    private static Database instance;

    private Database() {
    }
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
            instance.createTableMemories();
            instance.createTableMessageAndConversation();
        }
        return instance;
    }
    private Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(url);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }

        return conn;
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
    public void createTableMessageAndConversation() {
        String CREATE_TABLE_CHATS = """
        CREATE TABLE IF NOT EXISTS chats (
            id TEXT PRIMARY KEY,
            timestamp INTEGER NOT NULL,
            name TEXT,
            description TEXT
        );
    """;
        String CREATE_TABLE_MESSAGES = """
        CREATE TABLE IF NOT EXISTS messages (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            chat_id TEXT,
            timestamp INTEGER NOT NULL,
            content TEXT,
            position INTEGER NOT NULL,
            sender TEXT NOT NULL,
          
          FOREIGN KEY (chat_id) REFERENCES chats(id)
        
        );
    """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_CHATS);
            System.out.println("Tabla 'CHATS' creada o ya existía.");
            stmt.execute(CREATE_TABLE_MESSAGES);
            System.out.println("Tabla 'MESSAGES' creada o ya existía.");
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }


    // Guardar o actualizar
    public void saveOrUpdateConversation(String id, long timestamp, String name, String description) {
        String sql = """
            INSERT INTO chats (id, timestamp, name, description)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                timestamp = excluded.timestamp,
                name = excluded.name,
                description = excluded.description
        """;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setLong(2, timestamp);
            pstmt.setString(3, name);
            pstmt.setString(4, description);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar/actualizar conversacion: " + e.getMessage());
        }
    }
    public Long saveMessage(Message message, String idChat) {

        String sql = """
            INSERT INTO messages (chat_id, timestamp, content, position, sender)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id;
        """;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idChat);
            pstmt.setLong(2, message.getTimestamp());
            pstmt.setString(3, message.getContent());
            pstmt.setInt(4, message.getPosition());
            pstmt.setString(5, message.getSender().toString());

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                 return rs.getLong("id");
            }else {
                throw new SQLException("No se generó ID");
            }

        } catch (SQLException e) {
            System.out.println("Error al guardar mensaje: " + e.getMessage());
            throw new RuntimeException("Error guardando mensaje", e);

        }
    }

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

    public Map<String, ConversationPreview> getAllConvsPreviws() {
        Map<String, ConversationPreview> previews = new HashMap<>();

        String sql = "SELECT id, timestamp, name, description FROM chats ORDER BY timestamp DESC;";


        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ConversationPreview conv = new ConversationPreview(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getLong("timestamp")
                );
                previews.put(conv.getId(), conv);
            }
        } catch (SQLException e) {
            System.out.println("Error al leer Previws de los chats: " + e.getMessage());
        }

        return previews;
    }

    public Conversation loadConversation(String id) {
        String chatSql = "SELECT timestamp, name, description FROM chats WHERE id = ?;";
        String sqlMessages = "SELECT id, timestamp, content, position, sender  FROM messages WHERE chat_id = ? ORDER BY position ASC;";

        try(Connection conn = connect(); PreparedStatement chatStmt = conn.prepareStatement(chatSql)){
            chatStmt.setString(1, id);
            ResultSet chatResult = chatStmt.executeQuery();
            if (!chatResult.next()) return null;
            Conversation conversation = new Conversation(new ArrayList<>(), id, chatResult.getString("name"), chatResult.getString("description"), chatResult.getLong("timestamp"));

            try(PreparedStatement pstmtMessages = conn.prepareStatement(sqlMessages)){
                pstmtMessages.setString(1, id);
                ResultSet resultMessages = pstmtMessages.executeQuery();
                while (resultMessages.next()) {
                    Message.SenderType sender = Message.SenderType.valueOf(resultMessages.getString("sender"));
                    Message message = new Message(
                            resultMessages.getString("content"),
                            sender,
                            resultMessages.getLong("id"),
                            resultMessages.getInt("position"),
                            resultMessages.getLong("timestamp")
                    );

                    conversation.addMessage(message);
                }

            }
            return conversation;

        } catch (SQLException e) {
            System.out.println("Error cargando conversación: " + e.getMessage());
            return null;
        }



    }




}
