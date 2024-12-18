package MainLogin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 데이터 접근 객체(DAO, Data Access Object) 클래스 : 댓글 데이터 처리 역할
public class CommentDAO {
private static final String url = "jdbc:mysql://localhost/userdb";
    private static final String username = "root";
    private static final String paasword = "adgker!123";

    public List<String> getCommentsOrderedByCreatedAt() {
        List<String> comments = new ArrayList<>();
        String query = "SELECT user_id, content, created_at FROM comments ORDER BY created_at ASC";

        try (Connection conn = DriverManager.getConnection(url, username, paasword);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // 댓글 내용과 작성 시간을 ResultSet에서 가져와 리스트에 추가
                String userId = rs.getString("user_id");
                String content = rs.getString("content");
                Timestamp createdAt = rs.getTimestamp("created_at");
                comments.add(userId + ": " + content + " (" + createdAt.toString() + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments; // 정렬된 댓글 리스트 반환
    }


    // 댓글 저장
    public void saveComment(String userId, String content) {
        String sqlInsert = "INSERT INTO comments (comment_id, user_id, content) VALUES (?, ?, ?)";
        String sqlCheckUser = "SELECT COUNT(*) FROM userlist WHERE ID = ?";
        String commentId = UUID.randomUUID().toString().substring(0, 20);

        try (Connection conn = DBConnection.getConnection()) {
            // 디버깅: 연결된 데이터베이스 이름 확인
            System.out.println("Connected to database: " + conn.getCatalog());

            // user_id 검증
            try (PreparedStatement checkUserStmt = conn.prepareStatement(sqlCheckUser)) {
                checkUserStmt.setString(1, userId);
                System.out.println("Executing SQL: " + checkUserStmt);
                ResultSet rs = checkUserStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("댓글 저장 실패! 사용자 ID가 존재하지 않습니다: " + userId);
                    return;
                }
            }

            // 2. 댓글 저장
            try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
                insertStmt.setString(1, commentId); // comment_id
                insertStmt.setString(2, userId);   // user_id
                insertStmt.setString(3, content); // content
                insertStmt.executeUpdate();
                System.out.println("댓글 저장 성공!");
            }
        } catch (SQLException e) {
            System.out.println("댓글 저장 실패!");
            e.printStackTrace();
        }
    }
}
