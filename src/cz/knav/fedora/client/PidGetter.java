package cz.knav.fedora.client;

import java.util.HashSet;
import java.util.Set;

public class PidGetter {
    
    private String responseString;
    private int ix = 0;
    private Set<String> pids = new HashSet<String>(); //aaaaaaaaaaaaaaaaaas
    
    public PidGetter(String responseString) {
        super();
        this.responseString = responseString;
    }
    
    public String getNextPid() {
        String r = null;
        while (r == null) {
            int ixSubject = responseString.indexOf("<info:fedora/uuid:", ix);
            if (ixSubject < 0) {
                return null;
            } else {
                ix = responseString.indexOf(">", ixSubject);
                r = responseString.substring(ixSubject + 13, ix);
                if (pids.contains(r)) {
                    r = null;
                } else {
                    pids.add(r);
                }
            }
        }
        return r;
    }

}
