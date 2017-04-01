package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;
import javax.sql.DataSource;

/**
 *
 * @author violentsushi
 */
@Named(value = "utility")
@SessionScoped
public class Utility implements Serializable {

    // resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;
    
    public List<SelectItem> getAvailableMajors() throws SQLException {

        List<SelectItem> list = new ArrayList<>();

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "select * from AVAILABLEMAJOR"
            );

            // retrieve customer data from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                String s = result.getString("MAJORCODE");
                list.add(new SelectItem(s));
            }

        } finally {
            conn.close();
        }

        return list;

    }

}
