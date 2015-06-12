package org.igu.teamcity;

/**
 * Created by iguissouma on 02/06/2015.
 */
public class YammerMessage {
    public static String doFormatMessage(String project, String build, String statusText) {
        return   project + " #" + build + " " + statusText ;
    }
}
