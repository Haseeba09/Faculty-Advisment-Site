package FacultyAdvisement;

import java.sql.SQLException;
import java.util.Map;

public interface ICRUDHanlder {
    
    // Four basic functions of persistent storage
    void create() throws SQLException;
    void read();
    Map readAll() throws SQLException;
    void update();
    void delete(); 
}
