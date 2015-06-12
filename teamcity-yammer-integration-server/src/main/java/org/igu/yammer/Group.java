package org.igu.yammer;

import java.util.Date;

/**
 * Created by iguissouma on 31/05/2015.
 */
public class Group {
    private final long id;
    private final String fullName;



    public Group( long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }





    public String getFullName() {
        return fullName;
    }


    public long getId() {
        return id;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Group{");
        sb.append("id=").append(id);
        sb.append(", fullName='").append(fullName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
